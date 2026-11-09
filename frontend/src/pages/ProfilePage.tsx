import React, { useState, useEffect } from 'react';
import {
  User,
  Mail,
  GraduationCap,
  Coins,
  Lock,
  Bell,
  CheckCircle2,
  AlertCircle,
  Save,
  KeyRound
} from 'lucide-react';
import { useAuth } from '../contexts/AuthContext';
import { authApi } from '../services/api';

export const ProfilePage: React.FC = () => {
  const { user, updateProfile } = useAuth();

  // Profile Form State
  const [profileData, setProfileData] = useState({
    fullName: user?.fullName || '',
    collegeName: user?.collegeName || '',
    studentStatus: user?.studentStatus || 'Undergraduate',
    currency: user?.currency || 'INR',
    monthlyAllowance: user?.monthlyAllowance ? String(user?.monthlyAllowance) : '15000',
  });

  // Password State
  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
    confirmNewPassword: '',
  });

  const [profileSuccess, setProfileSuccess] = useState(false);
  const [passwordSuccess, setPasswordSuccess] = useState(false);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (user) {
      setProfileData({
        fullName: user.fullName || '',
        collegeName: user.collegeName || '',
        studentStatus: user.studentStatus || 'Undergraduate',
        currency: user.currency || 'INR',
        monthlyAllowance: user.monthlyAllowance ? String(user.monthlyAllowance) : '15000',
      });
    }
  }, [user]);

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setProfileSuccess(false);
    try {
      await updateProfile({
        fullName: profileData.fullName,
        collegeName: profileData.collegeName,
        studentStatus: profileData.studentStatus,
        currency: profileData.currency,
        monthlyAllowance: parseFloat(profileData.monthlyAllowance || '0'),
      });
      setProfileSuccess(true);
      setTimeout(() => setProfileSuccess(false), 3000);
    } catch (err) {
      console.error('Failed to update profile', err);
    } finally {
      setLoading(false);
    }
  };

  const handleChangePassword = async (e: React.FormEvent) => {
    e.preventDefault();
    setPasswordError(null);
    setPasswordSuccess(false);

    if (passwordData.newPassword !== passwordData.confirmNewPassword) {
      setPasswordError('New passwords do not match');
      return;
    }
    if (passwordData.newPassword.length < 8) {
      setPasswordError('New password must be at least 8 characters');
      return;
    }

    try {
      await authApi.changePassword({
        currentPassword: passwordData.currentPassword,
        newPassword: passwordData.newPassword,
      });
      setPasswordSuccess(true);
      setPasswordData({ currentPassword: '', newPassword: '', confirmNewPassword: '' });
      setTimeout(() => setPasswordSuccess(false), 3000);
    } catch (err: any) {
      setPasswordError(err.response?.data?.message || 'Incorrect current password.');
    }
  };

  return (
    <div className="space-y-6 max-w-4xl mx-auto pb-12">
      <div>
        <h1 className="text-2xl font-extrabold text-finova-text">Account & Preferences</h1>
        <p className="text-xs text-finova-muted mt-0.5">
          Manage your personal student details, monthly allowance targets, and account security.
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
        {/* Left Column: Avatar & Summary */}
        <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs text-center space-y-4">
          <div className="w-20 h-20 rounded-3xl bg-lavender-100 text-primary font-black text-2xl flex items-center justify-center mx-auto border-2 border-lavender-200 shadow-sm">
            {user?.fullName?.[0] || 'S'}
          </div>

          <div>
            <h3 className="text-base font-extrabold text-finova-text">{user?.fullName}</h3>
            <p className="text-xs text-finova-muted mt-0.5">{user?.email}</p>
            <span className="inline-block mt-2 px-3 py-1 rounded-full bg-sage-100 text-sage-800 text-[10px] font-bold border border-sage-200">
              {user?.roles?.includes('ROLE_ADMIN') ? 'System Admin' : 'Verified Student'}
            </span>
          </div>

          <div className="pt-4 border-t border-finova-border text-left space-y-2 text-xs">
            <div>
              <span className="text-[10px] uppercase font-bold text-finova-muted block">College</span>
              <span className="font-semibold text-finova-text">{user?.collegeName || 'Not configured'}</span>
            </div>
            <div>
              <span className="text-[10px] uppercase font-bold text-finova-muted block">Currency</span>
              <span className="font-semibold text-finova-text">{user?.currency || 'INR (₹)'}</span>
            </div>
          </div>
        </div>

        {/* Right Column: Profile & Security Forms */}
        <div className="md:col-span-2 space-y-6">
          {/* PROFILE FORM */}
          <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs space-y-4">
            <h3 className="text-sm font-extrabold text-finova-text">Personal Details</h3>

            {profileSuccess && (
              <div className="p-3 rounded-xl bg-sage-50 border border-sage-200 text-xs text-sage-800 flex items-center gap-2 font-bold">
                <CheckCircle2 className="w-4 h-4" />
                <span>Profile preferences updated successfully!</span>
              </div>
            )}

            <form onSubmit={handleUpdateProfile} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Full Name
                </label>
                <input
                  type="text"
                  required
                  value={profileData.fullName}
                  onChange={(e) => setProfileData({ ...profileData, fullName: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    College / University
                  </label>
                  <input
                    type="text"
                    value={profileData.collegeName}
                    onChange={(e) => setProfileData({ ...profileData, collegeName: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                  />
                </div>

                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Academic Status
                  </label>
                  <select
                    value={profileData.studentStatus}
                    onChange={(e) => setProfileData({ ...profileData, studentStatus: e.target.value })}
                    className="w-full px-3 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                  >
                    <option value="Undergraduate">Undergraduate</option>
                    <option value="Postgraduate">Postgraduate</option>
                    <option value="Doctoral / PhD">Doctoral / PhD</option>
                    <option value="High School">High School</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Preferred Currency
                  </label>
                  <select
                    value={profileData.currency}
                    onChange={(e) => setProfileData({ ...profileData, currency: e.target.value })}
                    className="w-full px-3 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                  >
                    <option value="INR">INR (₹) - Indian Rupee</option>
                    <option value="USD">USD ($) - US Dollar</option>
                    <option value="EUR">EUR (€) - Euro</option>
                    <option value="GBP">GBP (£) - British Pound</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Monthly Allowance Target (₹)
                  </label>
                  <input
                    type="number"
                    min="0"
                    value={profileData.monthlyAllowance}
                    onChange={(e) => setProfileData({ ...profileData, monthlyAllowance: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text font-bold"
                  />
                </div>
              </div>

              <div className="pt-2">
                <button
                  type="submit"
                  disabled={loading}
                  className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-primary hover:bg-primary-dark text-white font-bold text-xs transition-all shadow-xs disabled:opacity-50"
                >
                  <Save className="w-4 h-4" />
                  <span>Save Profile</span>
                </button>
              </div>
            </form>
          </div>

          {/* CHANGE PASSWORD */}
          <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs space-y-4">
            <h3 className="text-sm font-extrabold text-finova-text">Change Password</h3>

            {passwordSuccess && (
              <div className="p-3 rounded-xl bg-sage-50 border border-sage-200 text-xs text-sage-800 flex items-center gap-2 font-bold">
                <CheckCircle2 className="w-4 h-4" />
                <span>Password updated successfully!</span>
              </div>
            )}

            {passwordError && (
              <div className="p-3 rounded-xl bg-red-50 border border-red-200 text-xs text-finova-danger flex items-center gap-2 font-bold">
                <AlertCircle className="w-4 h-4" />
                <span>{passwordError}</span>
              </div>
            )}

            <form onSubmit={handleChangePassword} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Current Password
                </label>
                <input
                  type="password"
                  required
                  value={passwordData.currentPassword}
                  onChange={(e) => setPasswordData({ ...passwordData, currentPassword: e.target.value })}
                  placeholder="••••••••••••"
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    New Password
                  </label>
                  <input
                    type="password"
                    required
                    placeholder="Min 8 characters"
                    value={passwordData.newPassword}
                    onChange={(e) => setPasswordData({ ...passwordData, newPassword: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                  />
                </div>

                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Confirm New Password
                  </label>
                  <input
                    type="password"
                    required
                    placeholder="Re-type new password"
                    value={passwordData.confirmNewPassword}
                    onChange={(e) => setPasswordData({ ...passwordData, confirmNewPassword: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                  />
                </div>
              </div>

              <div className="pt-2">
                <button
                  type="submit"
                  className="inline-flex items-center gap-2 px-5 py-2.5 rounded-xl bg-secondary-bg hover:bg-lavender-50 border border-finova-border text-finova-text font-bold text-xs transition-all"
                >
                  <KeyRound className="w-4 h-4 text-primary" />
                  <span>Update Password</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};
