import React, { Component, ErrorInfo, ReactNode } from 'react';
import { AlertTriangle, RefreshCw, Home } from 'lucide-react';

interface Props {
  children: ReactNode;
  fallback?: ReactNode;
}

interface State {
  hasError: boolean;
  error: Error | null;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false,
    error: null,
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error('FINOVA Uncaught React Error caught by ErrorBoundary:', error, errorInfo);
  }

  private handleReset = () => {
    this.setState({ hasError: false, error: null });
    window.location.reload();
  };

  public render() {
    if (this.state.hasError) {
      if (this.props.fallback) {
        return this.props.fallback;
      }

      return (
        <div className="min-h-screen bg-[#FAFAFC] flex items-center justify-center p-6">
          <div className="bg-white rounded-3xl p-8 border border-finova-border shadow-lg max-w-lg w-full text-center space-y-5">
            <div className="w-16 h-16 rounded-2xl bg-lavender-100 text-primary mx-auto flex items-center justify-center">
              <AlertTriangle className="w-8 h-8 text-primary" />
            </div>
            <h2 className="text-xl font-extrabold text-finova-text">We encountered an issue</h2>
            <p className="text-xs text-finova-muted leading-relaxed">
              FINOVA encountered an unexpected UI display error. Your session and financial records are completely safe.
            </p>
            {this.state.error?.message && (
              <div className="p-3 rounded-xl bg-red-50 border border-red-200 text-left text-[11px] text-finova-danger font-mono break-all max-h-28 overflow-y-auto">
                {this.state.error.message}
              </div>
            )}
            <div className="flex items-center justify-center gap-3 pt-2">
              <button
                onClick={this.handleReset}
                className="flex items-center gap-2 px-5 py-2.5 rounded-xl bg-primary text-white text-xs font-bold hover:bg-primary-dark transition-all shadow-sm"
              >
                <RefreshCw className="w-3.5 h-3.5" /> Reload Application
              </button>
              <button
                onClick={() => {
                  this.setState({ hasError: false, error: null });
                  window.location.href = '/dashboard';
                }}
                className="flex items-center gap-2 px-5 py-2.5 rounded-xl border border-finova-border text-finova-text text-xs font-bold hover:bg-secondary-bg transition-all"
              >
                <Home className="w-3.5 h-3.5" /> Dashboard
              </button>
            </div>
          </div>
        </div>
      );
    }

    return this.props.children;
  }
}
