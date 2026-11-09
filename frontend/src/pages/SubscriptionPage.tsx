import React, { useState, useEffect } from 'react';
import {
  Repeat,
  Plus,
  Calendar,
  Clock,
  Sparkles,
  Trash2,
  Edit2,
  X,
  TrendingDown,
  CheckCircle2,
  AlertCircle
} from 'lucide-react';
import { subscriptionApi } from '../services/api';
import { Subscription, BillingCycle } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';
import { useOnRealtimeUpdate } from '../hooks/useRealtimeSync';

const POPULAR_SERVICES = [
  { name: 'Spotify Student', category: 'Entertainment', amount: 59, cycle: 'MONTHLY' },
  { name: 'Netflix Mobile', category: 'Entertainment', amount: 149, cycle: 'MONTHLY' },
  { name: 'YouTube Premium Student', category: 'Entertainment', amount: 79, cycle: 'MONTHLY' },
  { name: 'GitHub Copilot / Student', category: 'Education', amount: 0, cycle: 'MONTHLY' },
  { name: 'Amazon Prime Student', category: 'Shopping', amount: 749, cycle: 'ANNUAL' },
  { name: 'ChatGPT Plus', category: 'Education', amount: 1999, cycle: 'MONTHLY' },
  { name: 'Duolingo Super', category: 'Education', amount: 160, cycle: 'MONTHLY' },
];

export const SubscriptionPage: React.FC = () => {
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([]);
  const [loading, setLoading] = useState(true);

  // Add / Edit Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingSub, setEditingSub] = useState<Subscription | null>(null);
  const [formData, setFormData] = useState({
    name: '',
    amount: '',
    billingCycle: 'MONTHLY' as BillingCycle,
    category: 'Entertainment',
    nextBillingDate: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    isActive: true,
    remindBeforeDays: 3,
  });

  const fetchSubscriptions = async () => {
    setLoading(true);
    try {
      const res = await subscriptionApi.getAll();
      setSubscriptions(res.data);
    } catch (err) {
      console.error('Failed to load subscriptions', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSubscriptions();
  }, []);

  useOnRealtimeUpdate(fetchSubscriptions, ['SUBSCRIPTION_UPDATED', 'SUBSCRIPTION_DELETED']);

  const handleOpenAddModal = (preset?: any) => {
    setEditingSub(null);
    setFormData({
      name: preset?.name || '',
      amount: preset ? String(preset.amount) : '',
      billingCycle: preset?.cycle || 'MONTHLY',
      category: preset?.category || 'Entertainment',
      nextBillingDate: new Date(Date.now() + 15 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      isActive: true,
      remindBeforeDays: 3,
    });
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (sub: Subscription) => {
    setEditingSub(sub);
    setFormData({
      name: sub.name,
      amount: String(sub.amount),
      billingCycle: sub.billingCycle,
      category: sub.category,
      nextBillingDate: sub.nextBillingDate || sub.nextRenewalDate || new Date().toISOString().split('T')[0],
      isActive: sub.isActive,
      remindBeforeDays: sub.remindBeforeDays ?? sub.reminderDaysBefore ?? 3,
    });
    setIsModalOpen(true);
  };

  const handleToggle = async (id: number) => {
    try {
      const res = await subscriptionApi.toggle(id);
      setSubscriptions((prev) => prev.map((s) => (s.id === id ? res.data : s)));
    } catch (err) {
      console.error('Failed to toggle subscription', err);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this subscription?')) return;
    try {
      await subscriptionApi.delete(id);
      setSubscriptions((prev) => prev.filter((s) => s.id !== id));
    } catch (err) {
      console.error('Failed to delete subscription', err);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        name: formData.name,
        amount: parseFloat(formData.amount),
        billingCycle: formData.billingCycle,
        category: formData.category,
        nextBillingDate: formData.nextBillingDate,
        isActive: formData.isActive,
        remindBeforeDays: Number(formData.remindBeforeDays),
      };

      if (editingSub) {
        const res = await subscriptionApi.update(editingSub.id, payload);
        setSubscriptions((prev) => prev.map((s) => (s.id === editingSub.id ? res.data : s)));
      } else {
        const res = await subscriptionApi.create(payload);
        setSubscriptions((prev) => [res.data, ...prev]);
      }
      setIsModalOpen(false);
    } catch (err) {
      console.error('Failed to save subscription', err);
    }
  };

  // Days until next billing
  const getDaysUntil = (dateStr: string) => {
    try {
      const target = new Date(dateStr).getTime();
      const today = new Date().setHours(0, 0, 0, 0);
      const diff = Math.ceil((target - today) / (1000 * 60 * 60 * 24));
      return diff;
    } catch {
      return 0;
    }
  };

  // Monthly expenditure normalized
  const monthlyRecurringSpend = subscriptions
    .filter((s) => s.isActive)
    .reduce((acc, curr) => {
      const amt = Number(curr.amount);
      if (curr.billingCycle === 'ANNUAL') return acc + amt / 12;
      if (curr.billingCycle === 'WEEKLY') return acc + amt * 4;
      return acc + amt;
    }, 0);

  const activeCount = subscriptions.filter((s) => s.isActive).length;

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-12">
      {/* HEADER & ACTION */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-finova-text">Subscription Radar</h1>
          <p className="text-xs text-finova-muted mt-0.5">
            Keep track of student streaming plans, software memberships, and renewal alerts.
          </p>
        </div>
        <button
          onClick={() => handleOpenAddModal()}
          className="inline-flex items-center gap-2 bg-primary hover:bg-primary-dark text-white px-4 py-2.5 rounded-xl text-xs font-bold transition-all shadow-xs"
        >
          <Plus className="w-4 h-4" />
          <span>Add Subscription</span>
        </button>
      </div>

      {/* METRIC CARDS */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">
            Monthly Recurring Cost
          </span>
          <p className="text-2xl font-black text-primary mt-1">{formatCurrency(monthlyRecurringSpend)}</p>
          <span className="text-[11px] text-finova-muted mt-1.5 block">Estimated monthly drain</span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">
            Active Memberships
          </span>
          <p className="text-2xl font-black text-finova-text mt-1">{activeCount}</p>
          <span className="text-[11px] text-sage-800 font-bold mt-1.5 flex items-center gap-1">
            <CheckCircle2 className="w-3.5 h-3.5" /> Out of {subscriptions.length} tracked
          </span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">
            Annual Equivalent Cost
          </span>
          <p className="text-2xl font-black text-finova-danger mt-1">
            {formatCurrency(monthlyRecurringSpend * 12)}
          </p>
          <span className="text-[11px] text-finova-muted mt-1.5 block">Yearly total across subscriptions</span>
        </div>
      </div>

      {/* QUICK PRESET TEMPLATES */}
      <div className="p-4 rounded-2xl bg-white border border-finova-border shadow-xs">
        <span className="text-xs font-bold text-finova-text block mb-2.5">
          Quick-Add Student Subscriptions
        </span>
        <div className="flex flex-wrap gap-2">
          {POPULAR_SERVICES.map((preset) => (
            <button
              key={preset.name}
              onClick={() => handleOpenAddModal(preset)}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-xl bg-secondary-bg hover:bg-lavender-50 hover:border-lavender-200 border border-finova-border text-xs font-semibold text-finova-text transition-colors"
            >
              <Plus className="w-3 h-3 text-primary" />
              <span>{preset.name}</span>
              <span className="text-finova-muted text-[10px]">({formatCurrency(preset.amount)})</span>
            </button>
          ))}
        </div>
      </div>

      {/* SUBSCRIPTIONS GRID */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-full py-12 text-center text-finova-muted text-xs">
            Loading subscriptions...
          </div>
        ) : subscriptions.length === 0 ? (
          <div className="col-span-full p-12 rounded-2xl bg-white border border-finova-border text-center">
            <Repeat className="w-10 h-10 text-primary mx-auto mb-3 opacity-60" />
            <h3 className="text-base font-bold text-finova-text">No Subscriptions Tracked</h3>
            <p className="text-xs text-finova-muted mt-1">
              Add your Spotify, Netflix, gym, or software renewals to avoid sudden surprise deductions.
            </p>
          </div>
        ) : (
          subscriptions.map((s) => {
            const nextDate = s.nextBillingDate || s.nextRenewalDate || '';
            const daysLeft = getDaysUntil(nextDate);
            const isUrgent = daysLeft >= 0 && daysLeft <= 3 && s.isActive;

            return (
              <div
                key={s.id}
                className={`p-6 rounded-2xl bg-white border shadow-xs transition-all flex flex-col justify-between ${
                  isUrgent ? 'border-orange-300 ring-1 ring-orange-200' : 'border-finova-border hover:border-lavender-200'
                }`}
              >
                <div>
                  <div className="flex items-start justify-between gap-2 mb-3">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-xl bg-lavender-100 text-primary font-black flex items-center justify-center text-sm">
                        {s.name[0]}
                      </div>
                      <div>
                        <h3 className="text-sm font-extrabold text-finova-text">{s.name}</h3>
                        <span className="text-[11px] text-finova-muted">{s.category}</span>
                      </div>
                    </div>

                    <button
                      onClick={() => handleToggle(s.id)}
                      className={`px-2.5 py-1 rounded-full text-[10px] font-bold border transition-colors ${
                        s.isActive
                          ? 'bg-sage-100 text-sage-800 border-sage-200'
                          : 'bg-secondary-bg text-finova-muted border-finova-border'
                      }`}
                    >
                      {s.isActive ? 'Active' : 'Paused'}
                    </button>
                  </div>

                  <div className="my-4">
                    <div className="flex items-baseline gap-1.5">
                      <span className="text-2xl font-black text-finova-text">{formatCurrency(s.amount)}</span>
                      <span className="text-xs text-finova-muted font-semibold">/{s.billingCycle.toLowerCase()}</span>
                    </div>

                    <div className="mt-3 flex items-center gap-2 text-xs">
                      <Clock className="w-3.5 h-3.5 text-finova-muted" />
                      <span className="text-finova-muted">Next billing:</span>
                      <span className="font-bold text-finova-text">{formatDate(s.nextBillingDate || s.nextRenewalDate)}</span>
                    </div>

                    {s.isActive && (
                      <div className="mt-1.5">
                        {daysLeft < 0 ? (
                          <span className="text-[11px] text-finova-danger font-bold">Renewal overdue</span>
                        ) : daysLeft === 0 ? (
                          <span className="text-[11px] text-orange-600 font-extrabold">Renews Today!</span>
                        ) : (
                          <span
                            className={`text-[11px] font-bold ${
                              daysLeft <= 3 ? 'text-orange-600' : 'text-finova-muted'
                            }`}
                          >
                            Renews in {daysLeft} day{daysLeft > 1 ? 's' : ''}
                          </span>
                        )}
                      </div>
                    )}
                  </div>
                </div>

                <div className="pt-4 border-t border-finova-border flex items-center justify-between gap-2">
                  <button
                    onClick={() => handleOpenEditModal(s)}
                    className="flex-1 py-1.5 px-3 rounded-xl bg-secondary-bg hover:bg-lavender-50 text-finova-text text-xs font-bold transition-colors"
                  >
                    Edit Plan
                  </button>
                  <button
                    onClick={() => handleDelete(s.id)}
                    className="p-2 rounded-xl hover:bg-red-50 text-finova-muted hover:text-finova-danger transition-colors"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* ADD / EDIT MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-md w-full p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <h3 className="text-base font-extrabold text-finova-text">
                {editingSub ? 'Edit Subscription' : 'Add Subscription'}
              </h3>
              <button onClick={() => setIsModalOpen(false)} className="text-finova-muted hover:text-finova-text">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Service / Provider Name
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Spotify Premium Student"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Cost (₹)
                  </label>
                  <input
                    type="number"
                    min="0"
                    step="0.01"
                    required
                    placeholder="119"
                    value={formData.amount}
                    onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text font-bold"
                  />
                </div>

                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Cycle
                  </label>
                  <select
                    value={formData.billingCycle}
                    onChange={(e) => setFormData({ ...formData, billingCycle: e.target.value as BillingCycle })}
                    className="w-full px-3 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                  >
                    <option value="MONTHLY">Monthly</option>
                    <option value="ANNUAL">Annual</option>
                    <option value="WEEKLY">Weekly</option>
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Category
                  </label>
                  <select
                    value={formData.category}
                    onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                    className="w-full px-3 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                  >
                    <option value="Entertainment">Entertainment</option>
                    <option value="Education">Education</option>
                    <option value="Productivity">Productivity</option>
                    <option value="Shopping">Shopping</option>
                    <option value="Cloud & Tech">Cloud & Tech</option>
                    <option value="Fitness">Fitness</option>
                    <option value="Other">Other</option>
                  </select>
                </div>

                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Next Renewal Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.nextBillingDate}
                    onChange={(e) => setFormData({ ...formData, nextBillingDate: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                  />
                </div>
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-finova-border">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 rounded-xl border border-finova-border font-bold text-finova-muted"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl bg-primary hover:bg-primary-dark font-bold text-white shadow-xs"
                >
                  Save Subscription
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
