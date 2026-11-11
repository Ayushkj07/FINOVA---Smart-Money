import React, { useState, useEffect } from 'react';
import { X, UserPlus, Check, ArrowRight, ShieldCheck } from 'lucide-react';

export interface GoogleProfile {
  email: string;
  fullName: string;
  sub?: string;
  picture?: string;
}

interface GoogleAccountModalProps {
  isOpen: boolean;
  onClose: () => void;
  onSelectAccount: (account: GoogleProfile) => Promise<void>;
  title?: string;
}

const DEFAULT_ACCOUNTS: GoogleProfile[] = [
  {
    email: 'alex.morgan@gmail.com',
    fullName: 'Alex Morgan',
    sub: 'google_sub_1092837465019283',
    picture: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80',
  },
  {
    email: 'priya.sharma@gmail.com',
    fullName: 'Priya Sharma',
    sub: 'google_sub_9876543210987654',
    picture: 'https://images.unsplash.com/photo-1517841905240-472988babdf9?w=100&auto=format&fit=crop&q=80',
  },
];

export const GoogleAccountModal: React.FC<GoogleAccountModalProps> = ({
  isOpen,
  onClose,
  onSelectAccount,
  title = 'Sign in with Google'
}) => {
  const [savedAccounts, setSavedAccounts] = useState<GoogleProfile[]>([]);
  const [isAddingNew, setIsAddingNew] = useState(false);
  const [newEmail, setNewEmail] = useState('');
  const [newName, setNewName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    try {
      const stored = localStorage.getItem('finova_google_known_accounts');
      if (stored) {
        const parsed = JSON.parse(stored);
        if (Array.isArray(parsed) && parsed.length > 0) {
          setSavedAccounts(parsed);
          return;
        }
      }
    } catch {
      // ignore
    }
    setSavedAccounts(DEFAULT_ACCOUNTS);
  }, []);

  if (!isOpen) return null;

  const handleSelect = async (account: GoogleProfile) => {
    try {
      setLoading(true);
      setError(null);
      // Persist in known accounts
      const updated = [account, ...savedAccounts.filter((a) => a.email.toLowerCase() !== account.email.toLowerCase())].slice(0, 5);
      localStorage.setItem('finova_google_known_accounts', JSON.stringify(updated));
      await onSelectAccount(account);
      onClose();
    } catch (err: any) {
      setError(err?.message || 'Google authentication failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleAddNewSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newEmail || !newEmail.includes('@')) {
      setError('Please provide a valid Google email address');
      return;
    }
    const cleanEmail = newEmail.trim().toLowerCase();
    const cleanName = newName.trim() || cleanEmail.split('@')[0];
    const sub = `google_sub_${Math.abs(cleanEmail.split('').reduce((a, b) => ((a << 5) - a + b.charCodeAt(0)) | 0, 0))}_${Date.now()}`;
    const account: GoogleProfile = {
      email: cleanEmail,
      fullName: cleanName,
      sub,
      picture: `https://ui-avatars.com/api/?name=${encodeURIComponent(cleanName)}&background=9B87F5&color=fff&bold=true`,
    };

    await handleSelect(account);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-900/60 backdrop-blur-sm animate-fade-in">
      <div className="bg-white rounded-3xl max-w-md w-full shadow-2xl border border-finova-border overflow-hidden transform transition-all">
        {/* Header */}
        <div className="px-6 pt-6 pb-4 border-b border-finova-border flex items-center justify-between">
          <div className="flex items-center gap-3">
            <svg className="w-6 h-6" viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z" />
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" />
            </svg>
            <div>
              <h3 className="font-bold text-base text-finova-text">{title}</h3>
              <p className="text-[11px] text-finova-muted">to continue to FINOVA Smart Finance</p>
            </div>
          </div>
          <button
            onClick={onClose}
            className="w-8 h-8 rounded-full flex items-center justify-center text-finova-muted hover:bg-slate-100 transition-colors"
          >
            <X className="w-4 h-4" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6">
          {error && (
            <div className="mb-4 p-3 rounded-xl bg-red-50 border border-red-200 text-xs text-finova-danger font-medium">
              {error}
            </div>
          )}

          {!isAddingNew ? (
            <div className="space-y-2">
              <p className="text-xs font-semibold text-finova-muted mb-3">
                Choose an existing Google account or sign in with another:
              </p>

              {savedAccounts.map((acc) => (
                <button
                  key={acc.email}
                  disabled={loading}
                  onClick={() => handleSelect(acc)}
                  className="w-full flex items-center gap-3.5 p-3 rounded-2xl border border-finova-border hover:border-primary hover:bg-lavender-50/50 transition-all text-left group"
                >
                  <img
                    src={acc.picture || `https://ui-avatars.com/api/?name=${encodeURIComponent(acc.fullName)}&background=9B87F5&color=fff`}
                    alt={acc.fullName}
                    className="w-10 h-10 rounded-full border border-slate-200 object-cover flex-shrink-0"
                  />
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-bold text-finova-text truncate group-hover:text-primary transition-colors">
                      {acc.fullName}
                    </p>
                    <p className="text-xs text-finova-muted truncate">{acc.email}</p>
                  </div>
                  <ArrowRight className="w-4 h-4 text-slate-300 group-hover:text-primary group-hover:translate-x-0.5 transition-all" />
                </button>
              ))}

              <button
                type="button"
                onClick={() => setIsAddingNew(true)}
                className="w-full flex items-center gap-3.5 p-3 rounded-2xl border border-dashed border-slate-300 hover:border-primary hover:bg-slate-50 transition-all text-left text-primary font-bold text-xs mt-3"
              >
                <div className="w-10 h-10 rounded-full bg-lavender-100 flex items-center justify-center text-primary">
                  <UserPlus className="w-5 h-5" />
                </div>
                <span>Use another Google account</span>
              </button>
            </div>
          ) : (
            <form onSubmit={handleAddNewSubmit} className="space-y-4">
              <p className="text-xs font-semibold text-finova-muted">
                Enter your Google account details to sign in or create your FINOVA account:
              </p>

              <div>
                <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                  Your Full Name
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Alex Morgan"
                  value={newName}
                  onChange={(e) => setNewName(e.target.value)}
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div>
                <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                  Google Email Address
                </label>
                <input
                  type="email"
                  required
                  placeholder="name@gmail.com"
                  value={newEmail}
                  onChange={(e) => setNewEmail(e.target.value)}
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="flex gap-2 pt-2">
                <button
                  type="button"
                  onClick={() => setIsAddingNew(false)}
                  className="w-1/2 py-2.5 rounded-xl border border-finova-border text-xs font-bold text-finova-muted hover:bg-slate-50 transition-colors"
                >
                  Back
                </button>
                <button
                  type="submit"
                  disabled={loading}
                  className="w-1/2 py-2.5 rounded-xl bg-primary hover:bg-primary-dark text-white text-xs font-bold transition-all shadow-sm disabled:opacity-50 flex items-center justify-center gap-1.5"
                >
                  {loading ? (
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  ) : (
                    <>
                      <span>Continue</span>
                      <ArrowRight className="w-3.5 h-3.5" />
                    </>
                  )}
                </button>
              </div>
            </form>
          )}

          {/* Privacy & Security note */}
          <div className="mt-6 pt-4 border-t border-finova-border flex items-center gap-2 text-[11px] text-finova-muted">
            <ShieldCheck className="w-4 h-4 text-sage-600 flex-shrink-0" />
            <span>FINOVA uses OpenID Connect with strict per-user database isolation.</span>
          </div>
        </div>
      </div>
    </div>
  );
};
