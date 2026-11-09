import { useEffect, useRef } from 'react';
import { useAuth } from '../contexts/AuthContext';

export interface RealtimeEventPayload {
  type: string;
  timestamp: number;
  data: any;
}

export const useRealtimeSync = () => {
  const { token, user } = useAuth();
  const eventSourceRef = useRef<EventSource | null>(null);

  useEffect(() => {
    if (!token || !user) {
      if (eventSourceRef.current) {
        eventSourceRef.current.close();
        eventSourceRef.current = null;
      }
      return;
    }

    // Skip EventSource if using offline/demo token or running on static hosting without backend
    const isOfflineToken = token.startsWith('finova_demo_') || token.startsWith('finova_google_') || token.startsWith('finova_jwt_');
    const isLocal = typeof window !== 'undefined' && (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1');
    const hasRemoteBackend = Boolean((import.meta as any).env?.VITE_API_BASE_URL);

    if (isOfflineToken || (!isLocal && !hasRemoteBackend)) {
      // Offline / Vercel preview: do not attempt SSE connection to prevent continuous reconnection overhead
      return;
    }

    try {
      const streamUrl = `/api/realtime/stream?token=${encodeURIComponent(token)}`;
      const eventSource = new EventSource(streamUrl);
      eventSourceRef.current = eventSource;

      const handleEvent = (eventType: string, event: MessageEvent) => {
        try {
          const payload = JSON.parse(event.data);
          window.dispatchEvent(
            new CustomEvent('finova:realtime-sync', {
              detail: { type: eventType, payload }
            })
          );
        } catch {
          window.dispatchEvent(
            new CustomEvent('finova:realtime-sync', {
              detail: { type: eventType, payload: event.data }
            })
          );
        }
      };

      const financialEvents = [
        'EXPENSE_CREATED',
        'EXPENSE_UPDATED',
        'EXPENSE_DELETED',
        'INCOME_CREATED',
        'INCOME_UPDATED',
        'INCOME_DELETED',
        'BUDGET_UPDATED',
        'BUDGET_DELETED',
        'SAVINGS_UPDATED',
        'SAVINGS_DELETED',
        'DEBT_UPDATED',
        'DEBT_DELETED',
        'SUBSCRIPTION_UPDATED',
        'SUBSCRIPTION_DELETED',
        'DASHBOARD_SYNC'
      ];

      financialEvents.forEach((evt) => {
        eventSource.addEventListener(evt, (e: MessageEvent) => handleEvent(evt, e));
      });

      eventSource.onerror = () => {
        // Close immediately on error to prevent browser infinite reconnect loop
        if (eventSourceRef.current) {
          eventSourceRef.current.close();
          eventSourceRef.current = null;
        }
      };

      return () => {
        if (eventSourceRef.current) {
          eventSourceRef.current.close();
          eventSourceRef.current = null;
        }
      };
    } catch {
      // EventSource not supported or blocked
    }
  }, [token, user?.id]);
};

/**
 * Hook for pages to automatically re-fetch data when a real-time financial change occurs
 */
export const useOnRealtimeUpdate = (onUpdate: () => void, filterTypes?: string[]) => {
  const savedCallback = useRef(onUpdate);
  useEffect(() => {
    savedCallback.current = onUpdate;
  });

  useEffect(() => {
    const listener = (e: Event) => {
      const customEvent = e as CustomEvent<{ type: string; payload: any }>;
      if (!filterTypes || filterTypes.includes(customEvent.detail.type)) {
        savedCallback.current?.();
      }
    };

    window.addEventListener('finova:realtime-sync', listener);
    return () => {
      window.removeEventListener('finova:realtime-sync', listener);
    };
  }, [filterTypes]);
};
