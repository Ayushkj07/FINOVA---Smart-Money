import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';
import { googleApi } from '../services/api';
import { User, Mail, Lock, GraduationCap, Coins, ArrowRight, AlertCircle, CheckCircle2 } from 'lucide-react';

export const RegisterPage: React.FC = () => {
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    confirmPassword: '',
    collegeName: '',
    studentStatus: 'Undergraduate',
    currency: 'INR',
  });
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const { register } = useAuth();
  const navigate = useNavigate();

  const handleGoogleSignup = async () => {
    try {
      setLoading(true);
      setError(null);
      const res = await googleApi.getConnectUrl('AUTH');
      if (res.data?.authUrl) {
        window.location.href = res.data.authUrl;
      }
    } catch (err: any) {
      setError(err.response?.data?.message || 'Google registration is currently unconfigured or unavailable.');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match');
      return;
    }
    if (formData.password.length < 8) {
      setError('Password must be at least 8 characters long');
      return;
    }

    setLoading(true);
    try {
      await register({
        fullName: formData.fullName,
        email: formData.email,
        password: formData.password,
        collegeName: formData.collegeName,
        studentStatus: formData.studentStatus,
        currency: formData.currency,
      });
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.response?.data?.message || 'Registration failed. Email might already be registered.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-secondary-bg flex flex-col justify-center py-12 sm:px-6 lg:px-8">
      <div className="sm:mx-auto sm:w-full sm:max-w-md">
        <Link to="/" className="flex items-center justify-center gap-3 mb-6">
          <div className="w-12 h-12 rounded-2xl bg-primary flex items-center justify-center shadow-md shadow-primary/20">
            <span className="text-white font-black text-2xl tracking-wider">F</span>
          </div>
          <span className="font-extrabold text-2xl tracking-tight text-finova-text">FINOVA</span>
        </Link>
        <h2 className="text-center text-2xl font-extrabold text-finova-text">
          Create Your Student Account
        </h2>
        <p className="mt-2 text-center text-xs text-finova-muted">
          Smart Money Management for College Students
        </p>
      </div>

      <div className="mt-8 sm:mx-auto sm:w-full sm:max-w-lg">
        <div className="bg-white py-8 px-6 shadow-sm border border-finova-border rounded-2xl sm:px-10">
          {error && (
            <div className="mb-5 p-3 rounded-xl bg-red-50 border border-red-200 flex items-center gap-2.5 text-xs text-finova-danger font-medium">
              <AlertCircle className="w-4 h-4 flex-shrink-0" />
              <span>{error}</span>
            </div>
          )}

          <form className="space-y-4" onSubmit={handleSubmit}>
            <div>
              <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                Full Name
              </label>
              <div className="relative">
                <User className="w-4 h-4 text-finova-muted absolute left-3.5 top-3.5" />
                <input
                  type="text"
                  name="fullName"
                  required
                  value={formData.fullName}
                  onChange={handleChange}
                  placeholder="Alex Morgan"
                  className="w-full pl-10 pr-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                Student Email Address
              </label>
              <div className="relative">
                <Mail className="w-4 h-4 text-finova-muted absolute left-3.5 top-3.5" />
                <input
                  type="email"
                  name="email"
                  required
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="alex@college.edu"
                  className="w-full pl-10 pr-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                />
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                  Password
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 text-finova-muted absolute left-3.5 top-3.5" />
                  <input
                    type="password"
                    name="password"
                    required
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Min 8 characters"
                    className="w-full pl-10 pr-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                  Confirm Password
                </label>
                <div className="relative">
                  <Lock className="w-4 h-4 text-finova-muted absolute left-3.5 top-3.5" />
                  <input
                    type="password"
                    name="confirmPassword"
                    required
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    placeholder="Re-type password"
                    className="w-full pl-10 pr-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                  />
                </div>
              </div>
            </div>

            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                  College / University
                </label>
                <div className="relative">
                  <GraduationCap className="w-4 h-4 text-finova-muted absolute left-3.5 top-3.5" />
                  <input
                    type="text"
                    name="collegeName"
                    value={formData.collegeName}
                    onChange={handleChange}
                    placeholder="e.g. Stanford University"
                    className="w-full pl-10 pr-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                  />
                </div>
              </div>

              <div>
                <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                  Student Status
                </label>
                <select
                  name="studentStatus"
                  value={formData.studentStatus}
                  onChange={handleChange}
                  className="w-full px-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                >
                  <option value="Undergraduate">Undergraduate</option>
                  <option value="Postgraduate">Postgraduate</option>
                  <option value="Doctoral / PhD">Doctoral / PhD</option>
                  <option value="High School">High School</option>
                  <option value="Bootcamp / Intern">Bootcamp / Intern</option>
                </select>
              </div>
            </div>

            <div>
              <label className="block text-xs font-bold text-finova-text uppercase tracking-wider mb-1.5">
                Preferred Currency
              </label>
              <div className="relative">
                <Coins className="w-4 h-4 text-finova-muted absolute left-3.5 top-3.5" />
                <select
                  name="currency"
                  value={formData.currency}
                  onChange={handleChange}
                  className="w-full pl-10 pr-4 py-2.5 text-sm rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                >
                  <option value="INR">INR (₹) - Indian Rupee</option>
                  <option value="USD">USD ($) - US Dollar</option>
                  <option value="EUR">EUR (€) - Euro</option>
                  <option value="GBP">GBP (£) - British Pound</option>
                  <option value="CAD">CAD ($) - Canadian Dollar</option>
                </select>
              </div>
            </div>

            <div className="pt-2">
              <button
                type="submit"
                disabled={loading}
                className="w-full flex items-center justify-center gap-2 py-3 px-4 rounded-xl text-sm font-bold text-white bg-primary hover:bg-primary-dark shadow-sm hover:shadow transition-all disabled:opacity-50"
              >
                {loading ? (
                  <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
                ) : (
                  <>
                    <span>Create My Account</span>
                    <ArrowRight className="w-4 h-4" />
                  </>
                )}
              </button>
            </div>

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
              onClick={handleGoogleSignup}
              disabled={loading}
              className="w-full flex items-center justify-center gap-2.5 py-2.5 px-4 rounded-xl border border-finova-border bg-white hover:bg-slate-50 text-finova-text text-xs font-bold transition-all shadow-sm hover:border-slate-300"
            >
              <svg className="w-4 h-4" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" />
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" />
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z" />
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z" />
              </svg>
              <span>Sign up with Google</span>
            </button>
          </form>

          <p className="mt-6 text-center text-xs text-finova-muted">
            Already have an account?{' '}
            <Link to="/login" className="font-bold text-primary hover:text-primary-dark">
              Sign In
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
};
