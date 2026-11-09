import React, { useState, useEffect } from 'react';
import {
  PiggyBank,
  Plus,
  Calendar,
  CheckCircle2,
  TrendingUp,
  X,
  Trash2,
  Edit2,
  Coins,
  Sparkles,
  ArrowRight
} from 'lucide-react';
import { savingsApi } from '../services/api';
import { SavingsGoal } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';
import { useOnRealtimeUpdate } from '../hooks/useRealtimeSync';

export const SavingsPage: React.FC = () => {
  const [goals, setGoals] = useState<SavingsGoal[]>([]);
  const [loading, setLoading] = useState(true);

  // Goal Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingGoal, setEditingGoal] = useState<SavingsGoal | null>(null);
  const [goalForm, setGoalForm] = useState({
    goalName: '',
    targetAmount: '',
    currentAmount: '0',
    targetDate: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    description: '',
  });

  // Contribution Modal State
  const [isContribOpen, setIsContribOpen] = useState(false);
  const [activeGoal, setActiveGoal] = useState<SavingsGoal | null>(null);
  const [contribAmount, setContribAmount] = useState('');
  const [contribNote, setContribNote] = useState('');

  const fetchGoals = async () => {
    setLoading(true);
    try {
      const res = await savingsApi.getAll();
      setGoals(res.data);
    } catch (err) {
      console.error('Failed to load savings goals', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchGoals();
  }, []);

  useOnRealtimeUpdate(fetchGoals, ['SAVINGS_UPDATED', 'SAVINGS_DELETED']);

  const handleOpenAddModal = () => {
    setEditingGoal(null);
    setGoalForm({
      goalName: '',
      targetAmount: '',
      currentAmount: '0',
      targetDate: new Date(Date.now() + 90 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      description: '',
    });
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (goal: SavingsGoal) => {
    setEditingGoal(goal);
    setGoalForm({
      goalName: goal.goalName || goal.name || '',
      targetAmount: String(goal.targetAmount),
      currentAmount: String(goal.currentAmount),
      targetDate: goal.targetDate,
      description: goal.description || '',
    });
    setIsModalOpen(true);
  };

  const handleOpenContribModal = (goal: SavingsGoal) => {
    setActiveGoal(goal);
    setContribAmount('500');
    setContribNote('Weekly pocket money stash');
    setIsContribOpen(true);
  };

  const handleDeleteGoal = async (id: number) => {
    if (!window.confirm('Delete this savings vault?')) return;
    try {
      await savingsApi.delete(id);
      setGoals((prev) => prev.filter((g) => g.id !== id));
    } catch (err) {
      console.error('Failed to delete goal', err);
    }
  };

  const handleSaveGoal = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        goalName: goalForm.goalName,
        targetAmount: parseFloat(goalForm.targetAmount),
        currentAmount: parseFloat(goalForm.currentAmount || '0'),
        targetDate: goalForm.targetDate,
        description: goalForm.description,
      };

      if (editingGoal) {
        const res = await savingsApi.update(editingGoal.id, payload);
        setGoals((prev) => prev.map((g) => (g.id === editingGoal.id ? res.data : g)));
      } else {
        const res = await savingsApi.create(payload);
        setGoals((prev) => [res.data, ...prev]);
      }
      setIsModalOpen(false);
    } catch (err) {
      console.error('Failed to save savings goal', err);
    }
  };

  const handleAddContribution = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeGoal) return;
    try {
      const res = await savingsApi.addContribution(activeGoal.id, {
        amount: parseFloat(contribAmount),
        note: contribNote,
      });
      setGoals((prev) => prev.map((g) => (g.id === activeGoal.id ? res.data : g)));
      setIsContribOpen(false);
    } catch (err) {
      console.error('Failed to add contribution', err);
    }
  };

  const totalSaved = goals.reduce((acc, curr) => acc + Number(curr.currentAmount), 0);
  const totalTarget = goals.reduce((acc, curr) => acc + Number(curr.targetAmount), 0);
  const overallPct = totalTarget > 0 ? Math.round((totalSaved / totalTarget) * 100) : 0;

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-12">
      {/* HEADER & ACTION */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-finova-text">Savings Goals & Vaults</h1>
          <p className="text-xs text-finova-muted mt-0.5">
            Set aside funds for tech upgrades, college trips, certifications, and emergencies.
          </p>
        </div>
        <button
          onClick={handleOpenAddModal}
          className="inline-flex items-center gap-2 bg-primary hover:bg-primary-dark text-white px-4 py-2.5 rounded-xl text-xs font-bold transition-all shadow-xs"
        >
          <Plus className="w-4 h-4" />
          <span>New Savings Vault</span>
        </button>
      </div>

      {/* METRIC SUMMARY */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Total Stashed</span>
          <p className="text-2xl font-black text-primary mt-1">{formatCurrency(totalSaved)}</p>
          <span className="text-[11px] font-bold text-sage-800 flex items-center gap-1 mt-1.5">
            <TrendingUp className="w-3.5 h-3.5" /> Across {goals.length} active vaults
          </span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Total Cumulative Target</span>
          <p className="text-2xl font-black text-finova-text mt-1">{formatCurrency(totalTarget)}</p>
          <span className="text-[11px] text-finova-muted mt-1.5">Combined dream milestone targets</span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Overall Progress</span>
          <p className="text-2xl font-black text-sage-800 mt-1">{overallPct}%</p>
          <div className="w-full h-1.5 rounded-full bg-secondary-bg overflow-hidden mt-2">
            <div className="h-full bg-primary rounded-full" style={{ width: `${overallPct}%` }} />
          </div>
        </div>
      </div>

      {/* GOALS GRID */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-full py-12 text-center text-finova-muted text-xs">
            Loading savings vaults...
          </div>
        ) : goals.length === 0 ? (
          <div className="col-span-full p-12 rounded-2xl bg-white border border-finova-border text-center">
            <PiggyBank className="w-10 h-10 text-primary mx-auto mb-3 opacity-60" />
            <h3 className="text-base font-bold text-finova-text">No Savings Goals Configured</h3>
            <p className="text-xs text-finova-muted mt-1 max-w-sm mx-auto">
              Create your first savings vault to lock funds for a new laptop, semester fees, or emergency buffer.
            </p>
            <button
              onClick={handleOpenAddModal}
              className="mt-4 inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-primary text-white text-xs font-bold"
            >
              <Plus className="w-4 h-4" /> Create Goal
            </button>
          </div>
        ) : (
          goals.map((g) => {
            const current = Number(g.currentAmount);
            const target = Number(g.targetAmount);
            const pct = target > 0 ? Math.min(100, Math.round((current / target) * 100)) : 0;
            const remaining = Math.max(0, target - current);
            const isCompleted = current >= target;

            return (
              <div
                key={g.id}
                className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs hover:border-lavender-200 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-start justify-between gap-2 mb-3">
                    <div>
                      <h3 className="text-base font-extrabold text-finova-text">{g.goalName || g.name}</h3>
                      {g.description && (
                        <p className="text-xs text-finova-muted mt-0.5 line-clamp-1">{g.description}</p>
                      )}
                    </div>
                    {isCompleted ? (
                      <span className="px-2.5 py-1 rounded-full bg-sage-100 text-sage-800 text-[10px] font-black border border-sage-200">
                        Achieved! 🎉
                      </span>
                    ) : (
                      <span className="px-2.5 py-1 rounded-full bg-lavender-50 text-primary-dark text-[10px] font-bold border border-lavender-200">
                        {pct}% Reached
                      </span>
                    )}
                  </div>

                  <div className="my-4">
                    <div className="flex items-baseline justify-between text-xs mb-1.5">
                      <span className="text-2xl font-black text-primary">{formatCurrency(current)}</span>
                      <span className="font-semibold text-finova-muted">Target: {formatCurrency(target)}</span>
                    </div>

                    {/* Progress Bar */}
                    <div className="w-full h-2.5 rounded-full bg-secondary-bg border border-finova-border overflow-hidden">
                      <div
                        className={`h-full rounded-full transition-all duration-300 ${
                          isCompleted ? 'bg-sage-600' : 'bg-primary'
                        }`}
                        style={{ width: `${pct}%` }}
                      />
                    </div>

                    <div className="flex items-center justify-between text-[11px] text-finova-muted mt-2">
                      <span>Target Date: {formatDate(g.targetDate)}</span>
                      <span>Remaining: {formatCurrency(remaining)}</span>
                    </div>
                  </div>
                </div>

                <div className="pt-4 border-t border-finova-border flex items-center justify-between gap-2">
                  <button
                    onClick={() => handleOpenContribModal(g)}
                    disabled={isCompleted}
                    className="flex-1 inline-flex items-center justify-center gap-1.5 py-2 px-3 rounded-xl bg-sage-50 hover:bg-sage-100 text-sage-800 border border-sage-200 text-xs font-bold transition-colors disabled:opacity-50"
                  >
                    <Coins className="w-3.5 h-3.5 text-sage-700" />
                    <span>+ Add Money</span>
                  </button>
                  <button
                    onClick={() => handleOpenEditModal(g)}
                    className="p-2 rounded-xl hover:bg-secondary-bg text-finova-muted hover:text-finova-text transition-colors"
                  >
                    <Edit2 className="w-3.5 h-3.5" />
                  </button>
                  <button
                    onClick={() => handleDeleteGoal(g.id)}
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

      {/* ADD / EDIT GOAL MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-md w-full p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <h3 className="text-base font-extrabold text-finova-text">
                {editingGoal ? 'Edit Savings Goal' : 'Create New Savings Vault'}
              </h3>
              <button onClick={() => setIsModalOpen(false)} className="text-finova-muted hover:text-finova-text">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveGoal} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Goal Name
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. MacBook M3 Stash, Goa Trip, Emergency Fund"
                  value={goalForm.goalName}
                  onChange={(e) => setGoalForm({ ...goalForm, goalName: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Target (₹)
                  </label>
                  <input
                    type="number"
                    min="1"
                    required
                    placeholder="50000"
                    value={goalForm.targetAmount}
                    onChange={(e) => setGoalForm({ ...goalForm, targetAmount: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text font-bold"
                  />
                </div>

                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Initial Deposit (₹)
                  </label>
                  <input
                    type="number"
                    min="0"
                    placeholder="0"
                    value={goalForm.currentAmount}
                    onChange={(e) => setGoalForm({ ...goalForm, currentAmount: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text font-bold"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Target Completion Date
                </label>
                <input
                  type="date"
                  required
                  value={goalForm.targetDate}
                  onChange={(e) => setGoalForm({ ...goalForm, targetDate: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Description / Purpose (Optional)
                </label>
                <textarea
                  rows={2}
                  placeholder="e.g. Setting aside 20% of freelancing income"
                  value={goalForm.description}
                  onChange={(e) => setGoalForm({ ...goalForm, description: e.target.value })}
                  className="w-full px-3.5 py-2 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
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
                  {editingGoal ? 'Update Goal' : 'Save Vault'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* QUICK CONTRIBUTION MODAL */}
      {isContribOpen && activeGoal && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-sm w-full p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <div>
                <h3 className="text-sm font-extrabold text-finova-text">Add to {activeGoal.goalName || activeGoal.name}</h3>
                <span className="text-[11px] text-finova-muted">Instant deposit into your vault</span>
              </div>
              <button onClick={() => setIsContribOpen(false)} className="text-finova-muted hover:text-finova-text">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleAddContribution} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Deposit Amount (₹)
                </label>
                <input
                  type="number"
                  min="1"
                  required
                  placeholder="500"
                  value={contribAmount}
                  onChange={(e) => setContribAmount(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text font-black text-primary text-lg"
                />
              </div>

              {/* Quick Preset Buttons */}
              <div className="grid grid-cols-3 gap-2">
                {['200', '500', '1000'].map((amt) => (
                  <button
                    key={amt}
                    type="button"
                    onClick={() => setContribAmount(amt)}
                    className="py-1.5 rounded-lg border border-finova-border text-xs font-bold text-finova-text hover:bg-secondary-bg"
                  >
                    +₹{amt}
                  </button>
                ))}
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Note (Optional)
                </label>
                <input
                  type="text"
                  placeholder="e.g. Saved by skipping coffee"
                  value={contribNote}
                  onChange={(e) => setContribNote(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-finova-border">
                <button
                  type="button"
                  onClick={() => setIsContribOpen(false)}
                  className="px-4 py-2 rounded-xl border border-finova-border font-bold text-finova-muted"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl bg-sage-600 hover:bg-sage-700 font-bold text-white shadow-xs"
                >
                  Deposit Now
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
