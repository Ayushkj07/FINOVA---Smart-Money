import React, { useEffect, useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { CheckCircle2, AlertCircle, RefreshCw } from 'lucide-react';
import { googleApi, authApi } from '../services/api';

export const GoogleCallbackPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();

  const [status, setStatus] = useState<'processing' | 'success' | 'error'>('processing');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  useEffect(() => {
    const handleCallback = async () => {
      const code = searchParams.get('code');
      const state = searchParams.get('state');
      const error = searchParams.get('error');

      if (error) {
        setStatus('error');
        setErrorMessage(`Google authentication was cancelled or returned an error: ${error}`);
        return;
      }

      if (!code) {
        setStatus('error');
        setErrorMessage('No authorization code was found in the Google callback URL.');
        return;
      }

      // Extract service name and guest flag from state (state format: uuid_userId_service)
      let service = 'ALL';
      let isGuest = false;
      if (state) {
        const parts = state.split('_');
        if (parts.length >= 3) {
          isGuest = parts[1] === 'guest';
          service = parts[parts.length - 1];
        }
      }

      try {
        if (isGuest || service === 'AUTH') {
          // Authentication / Registration flow
          const res = await authApi.googleLogin({ code });
          localStorage.setItem('finova_token', res.data.token);
          if (res.data.refreshToken) {
            localStorage.setItem('finova_refresh_token', res.data.refreshToken);
          }
          const userObj = {
            id: res.data.id,
            email: res.data.email,
            fullName: res.data.fullName,
            roles: res.data.roles,
            currency: res.data.currency,
            collegeName: res.data.collegeName,
          };
          localStorage.setItem('finova_user', JSON.stringify(userObj));
          setStatus('success');
          setTimeout(() => {
            window.location.href = '/dashboard';
          }, 1500);
        } else {
          // Connecting an incremental Google Service (Calendar, Sheets, Drive, Gmail)
          await googleApi.callback({
            code,
            state: state || undefined,
            service,
          });

          setStatus('success');
          setTimeout(() => {
            navigate('/settings/connected-apps', { replace: true });
          }, 1800);
        }
      } catch (err: any) {
        setStatus('error');
        setErrorMessage(
          err.response?.data?.message || 'Failed to exchange authorization code with Google services.'
        );
      }
    };

    handleCallback();
  }, [searchParams, navigate]);

  return (
    <div className="min-h-screen bg-secondary-bg flex items-center justify-center p-6">
      <div className="bg-white rounded-3xl p-8 border border-finova-border shadow-md max-w-md w-full text-center space-y-5">
        {status === 'processing' && (
          <div className="space-y-4">
            <div className="w-14 h-14 rounded-2xl bg-lavender-100 text-primary mx-auto flex items-center justify-center animate-pulse">
              <RefreshCw className="w-7 h-7 animate-spin" />
            </div>
            <h2 className="text-base font-bold text-finova-text">Connecting Google Services...</h2>
            <p className="text-xs text-finova-muted">
              Securely exchanging authorization credentials and encrypting your OAuth tokens at rest.
            </p>
          </div>
        )}

        {status === 'success' && (
          <div className="space-y-4">
            <div className="w-14 h-14 rounded-2xl bg-sage-100 text-sage-700 mx-auto flex items-center justify-center">
              <CheckCircle2 className="w-8 h-8 text-finova-success" />
            </div>
            <h2 className="text-base font-bold text-finova-text">Google Connected Successfully!</h2>
            <p className="text-xs text-finova-muted">
              Redirecting you to Connected Apps settings...
            </p>
          </div>
        )}

        {status === 'error' && (
          <div className="space-y-4">
            <div className="w-14 h-14 rounded-2xl bg-red-100 text-finova-danger mx-auto flex items-center justify-center">
              <AlertCircle className="w-8 h-8" />
            </div>
            <h2 className="text-base font-bold text-finova-text">Integration Error</h2>
            <p className="text-xs text-finova-danger">{errorMessage}</p>
            <div className="pt-2">
              <button
                onClick={() => navigate('/settings/connected-apps')}
                className="px-5 py-2.5 rounded-2xl bg-primary hover:bg-primary-dark text-white text-xs font-semibold"
              >
                Return to Connected Apps
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
