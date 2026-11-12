import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { googleApi } from '../services/api';
import { Lock, Mail, ArrowRight, AlertCircle, Info } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState(() => localStorage.getItem('finova_remember_email') || '');
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [infoMessage, setInfoMessage] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [googleLoading, setGoogleLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();

  const redirectUri = typeof window !== 'undefined'
    ? `${window.location.origin}/settings/connected-apps/callback`
    : 'http://localhost:8080/settings/connected-apps/callback';

  const handleGoogleClick = async () => {
    setError(null);
    setInfoMessage(null);
    try {
      setGoogleLoading(true);
      const res = await googleApi.getConnectUrl('AUTH', redirectUri);
      const url = res.data?.authUrl;
      if (url && url.startsWith('http')) {
        window.location.href = url;
        return;
      }
      setError('Google Sign-In is not configured on this server. Please sign in with your email and password.');
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || '';
      if (msg.includes('not configured') || msg.includes('GOOGLE_CLIENT_ID')) {
        setError('Google Sign-In is not configured on this server. Please sign in with your email and password.');
      } else {
        setError(msg || 'Google Sign-In is temporarily unavailable. Please sign in with your email and password.');
      }
    } finally {
      setGoogleLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setInfoMessage(null);
    setLoading(true);

    try {
      const auth = await login({ email: email.trim(), password });

      if (rememberMe) {
        localStorage.setItem('finova_remember_email', email.trim());
      } else {
        localStorage.removeItem('finova_remember_email');
      }

      // Check if 2FA/Email OTP verification challenge is required
      if (auth.otpRequired && auth.challengeId) {
        const queryParams = new URLSearchParams({
          challengeId: auth.challengeId,
          maskedEmail: auth.maskedEmail || email.trim(),
          flow: 'LOGIN',
        });
        navigate(`/verify-otp?${queryParams.toString()}`);
        return;
      }

      if (auth.roles && auth.roles.includes('ROLE_ADMIN')) {
        navigate('/admin');
      } else {
        navigate('/dashboard');
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid email or password. Please verify your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-secondary-bg flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <Link to="/" className="flex items-center justify-center gap-3 mb-6 group">
          <div className="w-12 h-12 rounded-2xl bg-primary flex items-center justify-center shadow-md shadow-primary/20 group-hover:scale-105 transition-transform">
            <span className="text-white font-black text-2xl tracking-wider">F</span>
          </div>
          <span className="font-extrabold text-2xl tracking-tight text-finova-text">FINOVA</span>
        </Link>
        <h2 className="text-center text-2xl font-extrabold text-finova-text">
          Sign In to Your Account
        </h2>
        <p className="mt-2 text-center text-xs text-finova-muted">
          Smart Money Management for College Students
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-6 shadow-sm border border-finova-border rounded-2xl sm:px-10">
          {error && (
            <div className="mb-5 p-3 rounded-xl bg-red-50 border border-red-200 flex items-center gap-2.5 text-xs text-finova-danger font-medium animate-shake">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {infoMessage && (
            <div className="mb-5 p-3 rounded-xl bg-blue-50 border border-blue-200 flex items-center gap-2.5 text-xs text-blue-700 font-medium">
              <Info className="w-4 h-4 flex-shrink-0" />
              <span>{infoMessage}</span>
            </div>
          )}

          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                Email Address
              </label>
              <div className="relative">
                <Mail className="w-4 h-4 text-finova-muted absolute left-3.5 top-3.5" />
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="name@college.edu"
                  className="w-full pl-10 pr-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-finova-text bg-white"
                />
              </div>
            </div>

            <div>
              <div className="flex items-center justify-between mb-1.5">
                <label className="block text-xs font-bold text-finova-text uppercase tracking-wider">
                  Password
                </label>
                <Link
                  to="/forgot-password"
                  className="text-xs font-semibold text-primary hover:text-primary-dark transition-colors"
                >
                  Forgot password?
                </Link>
              </div>
              <div className="relative">
                <Lock className="w-4 h-4 text-finova-muted absolute left-3.5 top-3.5" />
                <input
                  type="password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••••••"
                  className="w-full pl-10 pr-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary text-finova-text bg-white"
                />
              </div>
            </div>

            {/* Remember Me Checkbox */}
            <div className="flex items-center justify-between py-1">
              <label className="flex items-center gap-2 cursor-pointer select-none">
                <input
                  type="checkbox"
                  checked={rememberMe}
                  onChange={(e) => setRememberMe(e.target.checked)}
                  className="w-4 h-4 rounded text-primary focus:ring-primary border-slate-300 accent-primary cursor-pointer"
                />
                <span className="text-xs text-finova-muted font-medium">Remember me on this device</span>
              </label>
            </div>

            <button
              type="submit"
              disabled={loading || googleLoading}
              className="w-full flex items-center justify-center gap-2 py-3 px-4 rounded-xl text-sm font-bold text-white bg-primary hover:bg-primary-dark shadow-sm hover:shadow transition-all disabled:opacity-50"
            >
              {loading ? (
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                <>
                  <span>Sign In</span>
                  <ArrowRight className="w-4 h-4" />
                </>
              )}
            </button>

            <div className="relative my-4">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-finova-border" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-white px-3 text-finova-muted font-bold tracking-wider">or</span>
              </div>
            </div>

            <button
              type="button"
              onClick={handleGoogleClick}
              disabled={loading || googleLoading}
              className="w-full flex items-center justify-center gap-2.5 py-2.5 px-4 rounded-xl border border-finova-border bg-white hover:bg-slate-50 text-finova-text text-xs font-bold transition-all shadow-sm hover:border-slate-300 group disabled:opacity-50"
            >
              {googleLoading ? (
                <div className="w-4 h-4 border-2 border-primary border-t-transparent rounded-full animate-spin" />
              ) : (
                <svg className="w-4 h-4" viewBox="0 0 24 24">
                  <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                  <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                  <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z" />
                  <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" />
                </svg>
              )}
              <span>Continue with Google</span>
            </button>
          </form>

          <p className="mt-6 text-center text-xs text-finova-muted">
            Don't have an account?{' '}
            <Link to="/register" className="font-bold text-primary hover:text-primary-dark">
              Create free student account
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};
