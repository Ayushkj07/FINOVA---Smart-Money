import React, { useState, useEffect, useRef } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { ShieldCheck, ArrowRight, RefreshCw, AlertCircle, CheckCircle2, ArrowLeft, Mail } from 'lucide-react';

export const OtpVerificationPage: React.FC = () => {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { verifyLoginOtp, resendLoginOtp } = useAuth();

  // Retrieve challenge parameters from URL query params
  const [challengeId, setChallengeId] = useState<string>(() => searchParams.get('challengeId') || '');
  const [maskedEmail, setMaskedEmail] = useState<string>(() => searchParams.get('maskedEmail') || 'your email');

  // 6 separate digit slots
  const [otpDigits, setOtpDigits] = useState<string[]>(['', '', '', '', '', '']);
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  const [loading, setLoading] = useState(false);
  const [resending, setResending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  // Expiration countdown (default 300 seconds = 5 minutes)
  const [expiresIn, setExpiresIn] = useState<number>(300);
  // Resend cooldown timer (default 60 seconds)
  const [resendCooldown, setResendCooldown] = useState<number>(60);

  // Focus the first digit input on mount
  useEffect(() => {
    if (!challengeId) {
      setError('No active verification challenge found. Please sign in with Google again.');
    }
    inputRefs.current[0]?.focus();
  }, [challengeId]);

  // Expiry timer effect
  useEffect(() => {
    if (expiresIn <= 0) return;
    const timer = setInterval(() => {
      setExpiresIn((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(timer);
  }, [expiresIn]);

  // Resend cooldown timer effect
  useEffect(() => {
    if (resendCooldown <= 0) return;
    const cooldownTimer = setInterval(() => {
      setResendCooldown((prev) => (prev > 0 ? prev - 1 : 0));
    }, 1000);
    return () => clearInterval(cooldownTimer);
  }, [resendCooldown]);

  const formatTimer = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  const handleDigitChange = (index: number, value: string) => {
    // Only accept numeric input
    const cleanValue = value.replace(/\D/g, '');

    const newDigits = [...otpDigits];

    if (cleanValue.length > 1) {
      // Handle paste directly into field
      const pastedDigits = cleanValue.slice(0, 6).split('');
      for (let i = 0; i < 6; i++) {
        newDigits[i] = pastedDigits[i] || '';
      }
      setOtpDigits(newDigits);
      const nextIndex = Math.min(pastedDigits.length, 5);
      inputRefs.current[nextIndex]?.focus();
      return;
    }

    newDigits[index] = cleanValue;
    setOtpDigits(newDigits);
    setError(null);

    // Auto-advance to next input if filled
    if (cleanValue && index < 5) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index: number, e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Backspace' && !otpDigits[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e: React.ClipboardEvent<HTMLInputElement>) => {
    e.preventDefault();
    const pastedData = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, 6);
    if (!pastedData) return;

    const newDigits = [...otpDigits];
    for (let i = 0; i < 6; i++) {
      newDigits[i] = pastedData[i] || '';
    }
    setOtpDigits(newDigits);
    setError(null);

    const focusIndex = Math.min(pastedData.length, 5);
    inputRefs.current[focusIndex]?.focus();
  };

  const fullOtp = otpDigits.join('');
  const isComplete = fullOtp.length === 6;

  const handleVerify = async (e?: React.FormEvent) => {
    if (e) e.preventDefault();
    if (!isComplete || loading || expiresIn <= 0) return;

    setError(null);
    setSuccessMessage(null);
    setLoading(true);

    try {
      const auth = await verifyLoginOtp(challengeId, fullOtp);
      setSuccessMessage('Code verified successfully! Launching your FINOVA workspace...');
      setTimeout(() => {
        if (auth.roles && auth.roles.includes('ROLE_ADMIN')) {
          navigate('/admin', { replace: true });
        } else {
          navigate('/dashboard', { replace: true });
        }
      }, 1000);
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Invalid or expired verification code.';
      setError(msg);
      // Clear digits on error and focus first input
      setOtpDigits(['', '', '', '', '', '']);
      inputRefs.current[0]?.focus();
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (resendCooldown > 0 || resending || !challengeId) return;

    setError(null);
    setSuccessMessage(null);
    setResending(true);

    try {
      const res = await resendLoginOtp(challengeId);
      if (res.challengeId) {
        setChallengeId(res.challengeId);
      }
      if (res.maskedEmail) {
        setMaskedEmail(res.maskedEmail);
      }
      setExpiresIn(res.expiresInSeconds || 300);
      setResendCooldown(res.resendCooldownSeconds || 60);
      setOtpDigits(['', '', '', '', '', '']);
      setSuccessMessage('A fresh verification code has been dispatched to your email.');
      inputRefs.current[0]?.focus();
    } catch (err: any) {
      const msg = err.response?.data?.message || err.message || 'Failed to resend code. Please try again.';
      setError(msg);
    } finally {
      setResending(false);
    }
  };

  return (
    <div className="min-h-screen bg-secondary-bg flex flex-col justify-center py-12 px-4 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <Link to="/" className="flex items-center justify-center gap-3 mb-6 group">
          <div className="w-12 h-12 rounded-2xl bg-primary flex items-center justify-center shadow-md shadow-primary/20 group-hover:scale-105 transition-transform">
            <span className="text-white font-black text-2xl tracking-wider">F</span>
          </div>
          <span className="font-extrabold text-2xl tracking-tight text-finova-text">FINOVA</span>
        </Link>
      </div>

      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <div className="bg-white py-8 px-6 shadow-sm border border-finova-border rounded-3xl sm:px-10">
          {/* Header Icon */}
          <div className="flex flex-col items-center text-center mb-6">
            <div className="w-14 h-14 rounded-2xl bg-lavender-100 text-primary flex items-center justify-center mb-4 shadow-sm shadow-primary/10">
              <ShieldCheck className="w-7 h-7" />
            </div>
            <h2 className="text-xl font-extrabold text-finova-text">Two-Factor Email Verification</h2>
            <p className="mt-2 text-xs text-finova-muted max-w-sm">
              We've dispatched a 6-digit single-use security code to your verified Google account:
            </p>
            <div className="mt-2.5 inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-slate-100 border border-slate-200 text-xs font-semibold text-finova-text">
              <Mail className="w-3.5 h-3.5 text-primary" />
              <span>{maskedEmail}</span>
            </div>
          </div>

          {/* Feedback Alerts */}
          {error && (
            <div className="mb-5 p-3 rounded-xl bg-red-50 border border-red-200 flex items-center gap-2.5 text-xs text-finova-danger font-medium animate-shake">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          {successMessage && (
            <div className="mb-5 p-3 rounded-xl bg-sage-50 border border-sage-200 flex items-center gap-2.5 text-xs text-sage-800 font-medium">
              <CheckCircle2 className="w-4 h-4 flex-shrink-0 text-sage-600" />
              <span>{successMessage}</span>
            </div>
          )}

          {/* 6 Digit Input Form */}
          <form onSubmit={handleVerify} className="space-y-6">
            <div>
              <div className="flex justify-between items-center mb-2">
                <label className="text-[11px] font-bold text-finova-text uppercase tracking-wider">
                  Enter 6-Digit Code
                </label>
                <span
                  className={`text-xs font-bold ${
                    expiresIn < 60 ? 'text-finova-danger animate-pulse' : 'text-finova-muted'
                  }`}
                >
                  {expiresIn > 0 ? `Expires in ${formatTimer(expiresIn)}` : 'Expired'}
                </span>
              </div>

              <div className="flex justify-between gap-2 sm:gap-2.5" onPaste={handlePaste}>
                {otpDigits.map((digit, idx) => (
                  <input
                    key={idx}
                    ref={(el) => (inputRefs.current[idx] = el)}
                    type="text"
                    inputMode="numeric"
                    pattern="[0-9]*"
                    maxLength={1}
                    value={digit}
                    disabled={loading || expiresIn <= 0}
                    onChange={(e) => handleDigitChange(idx, e.target.value)}
                    onKeyDown={(e) => handleKeyDown(idx, e)}
                    className={`w-12 h-14 text-center text-xl font-extrabold rounded-xl border bg-white text-finova-text transition-all focus:outline-none ${
                      digit
                        ? 'border-primary ring-2 ring-primary/20 bg-lavender-50/20'
                        : 'border-finova-border focus:border-primary focus:ring-2 focus:ring-primary/20'
                    } ${expiresIn <= 0 ? 'opacity-50 cursor-not-allowed bg-slate-50' : ''}`}
                  />
                ))}
              </div>
            </div>

            {/* Action Buttons */}
            <div className="space-y-3">
              <button
                type="submit"
                disabled={!isComplete || loading || expiresIn <= 0}
                className="w-full flex items-center justify-center gap-2 py-3 px-4 rounded-xl text-sm font-bold text-white bg-primary hover:bg-primary-dark shadow-sm hover:shadow transition-all disabled:opacity-50 disabled:cursor-not-allowed"
              >
                {loading ? (
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                ) : (
                  <>
                    <span>Verify & Continue</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>

              <div className="flex items-center justify-between pt-2 text-xs">
                <span className="text-finova-muted">Didn't receive the code?</span>
                <button
                  type="button"
                  disabled={resendCooldown > 0 || resending}
                  onClick={handleResend}
                  className="font-bold text-primary hover:text-primary-dark disabled:text-finova-muted disabled:cursor-not-allowed inline-flex items-center gap-1 transition-colors"
                >
                  <RefreshCw className={`w-3.5 h-3.5 ${resending ? 'animate-spin' : ''}`} />
                  <span>
                    {resendCooldown > 0 ? `Resend in ${resendCooldown}s` : 'Resend Code'}
                  </span>
                </button>
              </div>
            </div>
          </form>

          {/* Security Notice */}
          <div className="mt-8 pt-6 border-t border-finova-border text-center space-y-3">
            <p className="text-[11px] text-finova-muted leading-relaxed">
              For your account security, single-use codes expire in 5 minutes. Never share this code with anyone.
            </p>
            <div>
              <Link
                to="/login"
                className="inline-flex items-center gap-1 text-xs font-bold text-finova-text hover:text-primary transition-colors"
              >
                <ArrowLeft className="w-3.5 h-3.5" />
                <span>Return to Login</span>
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
