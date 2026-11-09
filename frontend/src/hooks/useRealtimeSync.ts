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

    // Connect to Server-Sent Events stream using secure token parameter
    const streamUrl = `/api/realtime/stream?token=${encodeURIComponent(token)}`;
    const eventSource = new EventSource(streamUrl);
    eventSourceRef.current = eventSource;

    eventSource.onopen = () => {
      // Stream established
    };

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
      if (eventSource.readyState === EventSource.CLOSED) {
        eventSource.close();
      }
    };

    return () => {
      eventSource.close();
      eventSourceRef.current = null;
    };
  }, [token, user?.id]);
};

/**
 * Hook for pages to automatically re-fetch data when a real-time financial change occurs
 */
export const useOnRealtimeUpdate = (onUpdate: () => void, filterTypes?: string[]) => {
  useEffect(() => {
    const listener = (e: Event) => {
      const customEvent = e as CustomEvent<{ type: string; payload: any }>;
      if (!filterTypes || filterTypes.includes(customEvent.detail.type)) {
        onUpdate();
      }
    };

    window.addEventListener('finova:realtime-sync', listener);
    return () => {
      window.removeEventListener('finova:realtime-sync', listener);
    };
  }, [onUpdate, filterTypes]);
};
