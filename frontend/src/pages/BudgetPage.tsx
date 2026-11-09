import React, { useState, useEffect } from 'react';
import {
  PieChart as PieIcon,
  Plus,
  AlertTriangle,
  AlertCircle,
  CheckCircle2,
  Trash2,
  Edit2,
  X,
  TrendingUp,
  ShieldAlert
} from 'lucide-react';
import { budgetApi } from '../services/api';
import { Budget, BudgetCategory } from '../types';
import { formatCurrency } from '../utils/formatters';
import { useOnRealtimeUpdate } from '../hooks/useRealtimeSync';

const DEFAULT_CATEGORIES = [
  'Food',
  'Transportation',
  'Education',
  'Shopping',
  'Entertainment',
  'Accommodation',
  'Healthcare',
  'Mobile and Internet',
  'Subscriptions',
  'Personal Care',
  'Other',
];

export const BudgetPage: React.FC = () => {
  const [currentBudget, setCurrentBudget] = useState<Budget | null>(null);
  const [allBudgets, setAllBudgets] = useState<Budget[]>([]);
  const [loading, setLoading] = useState(true);

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [totalLimit, setTotalLimit] = useState('15000');
  const [month, setMonth] = useState(new Date().getMonth() + 1);
  const [year, setYear] = useState(new Date().getFullYear());
  const [categories, setCategories] = useState<{ categoryName: string; allocatedAmount: string }[]>([
    { categoryName: 'Food', allocatedAmount: '6000' },
    { categoryName: 'Transportation', allocatedAmount: '2000' },
    { categoryName: 'Education', allocatedAmount: '2500' },
    { categoryName: 'Entertainment', allocatedAmount: '1500' },
    { categoryName: 'Subscriptions', allocatedAmount: '1000' },
    { categoryName: 'Shopping', allocatedAmount: '2000' },
  ]);

  const fetchBudgetData = async () => {
    setLoading(true);
    try {
      const [curRes, allRes] = await Promise.allSettled([
        budgetApi.getCurrent(),
        budgetApi.getAll(),
      ]);

      if (curRes.status === 'fulfilled') setCurrentBudget(curRes.value.data);
      if (allRes.status === 'fulfilled') setAllBudgets(allRes.value.data);
    } catch (err) {
      console.error('Failed to load budget data', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBudgetData();
  }, []);

  useOnRealtimeUpdate(fetchBudgetData, ['BUDGET_UPDATED', 'BUDGET_DELETED', 'EXPENSE_CREATED', 'EXPENSE_UPDATED', 'EXPENSE_DELETED']);

  const handleOpenModal = () => {
    if (currentBudget) {
      setTotalLimit(String(currentBudget.totalBudgetLimit));
      setMonth(currentBudget.month);
      setYear(currentBudget.year);
      if (currentBudget.categories && currentBudget.categories.length > 0) {
        setCategories(
          currentBudget.categories.map((c) => ({
            categoryName: c.categoryName,
            allocatedAmount: String(c.allocatedAmount || c.limitAmount || 0),
          }))
        );
      }
    }
    setIsModalOpen(true);
  };

  const handleCategoryAllocChange = (index: number, val: string) => {
    const updated = [...categories];
    updated[index].allocatedAmount = val;
    setCategories(updated);
  };

  const handleAddCategoryRow = () => {
    setCategories([...categories, { categoryName: 'Personal Care', allocatedAmount: '1000' }]);
  };

  const handleRemoveCategoryRow = (index: number) => {
    setCategories(categories.filter((_, i) => i !== index));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        totalBudgetLimit: parseFloat(totalLimit),
        month: Number(month),
        year: Number(year),
        categories: categories.map((c) => ({
          categoryName: c.categoryName,
          allocatedAmount: parseFloat(c.allocatedAmount || '0'),
        })),
      };

      const res = await budgetApi.createOrUpdate(payload);
      setCurrentBudget(res.data);
      setIsModalOpen(false);
      fetchBudgetData();
    } catch (err) {
      console.error('Failed to save budget', err);
    }
  };

  // 3-Tier Alert Level Helper
  const getAlertStatus = (spent: number, limit: number) => {
    if (limit <= 0) return { label: 'Normal', color: 'bg-sage-100 text-sage-800 border-sage-200' };
    const pct = (spent / limit) * 100;
    if (pct >= 100) {
      return {
        label: '100% Exceeded',
        color: 'bg-red-100 text-finova-danger border-red-200 font-black',
        barColor: 'bg-finova-danger',
        level: 'danger',
      };
    }
    if (pct >= 90) {
      return {
        label: '90% High Warning',
        color: 'bg-orange-100 text-orange-700 border-orange-200 font-bold',
        barColor: 'bg-orange-500',
        level: 'warning',
      };
    }
    if (pct >= 70) {
      return {
        label: '70% Warning',
        color: 'bg-yellow-100 text-yellow-800 border-yellow-200 font-bold',
        barColor: 'bg-finova-warning',
        level: 'caution',
      };
    }
    return {
      label: 'On Track',
      color: 'bg-sage-100 text-sage-800 border-sage-200 font-semibold',
      barColor: 'bg-primary',
      level: 'normal',
    };
  };

  const totalSpent = currentBudget?.spentAmount || currentBudget?.totalSpentAmount || 0;
  const totalLimitNum = currentBudget?.totalBudgetLimit || 1;
  const overallPct = Math.min(100, Math.round((totalSpent / totalLimitNum) * 100));
  const remainingBudget = Math.max(0, totalLimitNum - totalSpent);
  const overallStatus = getAlertStatus(totalSpent, totalLimitNum);

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-12">
      {/* HEADER & ACTION */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-finova-text">Smart Budget Planner</h1>
          <p className="text-xs text-finova-muted mt-0.5">
            Set proactive spending boundaries with 70%, 90%, and 100% automated alerts.
          </p>
        </div>
        <button
          onClick={handleOpenModal}
          className="inline-flex items-center gap-2 bg-primary hover:bg-primary-dark text-white px-4 py-2.5 rounded-xl text-xs font-bold transition-all shadow-xs"
        >
          <Plus className="w-4 h-4" />
          <span>{currentBudget ? 'Edit Monthly Budget' : 'Create Monthly Budget'}</span>
        </button>
      </div>

      {/* MONTHLY UTILIZATION OVERVIEW CARD */}
      <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
        <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-4">
          <div>
            <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">
              Current Month Budget Limit
            </span>
            <div className="flex items-center gap-3 mt-1">
              <span className="text-3xl font-black text-finova-text">{formatCurrency(totalLimitNum)}</span>
              <span className={`px-2.5 py-1 rounded-full text-xs border ${overallStatus.color}`}>
                {overallStatus.label}
              </span>
            </div>
          </div>

          <div className="flex items-center gap-6 text-xs">
            <div>
              <span className="text-finova-muted block">Total Spent</span>
              <span className="text-base font-extrabold text-finova-danger">{formatCurrency(totalSpent)}</span>
            </div>
            <div>
              <span className="text-finova-muted block">Remaining</span>
              <span className="text-base font-extrabold text-sage-800">{formatCurrency(remainingBudget)}</span>
            </div>
            <div>
              <span className="text-finova-muted block">Utilization</span>
              <span className="text-base font-extrabold text-finova-text">{overallPct}%</span>
            </div>
          </div>
        </div>

        {/* Master Progress Bar */}
        <div className="w-full h-3 rounded-full bg-secondary-bg border border-finova-border overflow-hidden">
          <div
            className={`h-full rounded-full transition-all duration-300 ${overallStatus.barColor}`}
            style={{ width: `${overallPct}%` }}
          />
        </div>

        {/* 3-Tier Alert Legend */}
        <div className="flex flex-wrap items-center gap-4 mt-4 pt-4 border-t border-finova-border text-[11px] text-finova-muted">
          <span className="font-bold text-finova-text">Alert Thresholds:</span>
          <span className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-primary" /> Under 70% Normal
          </span>
          <span className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-finova-warning" /> 70% Caution Warning
          </span>
          <span className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-orange-500" /> 90% High Warning
          </span>
          <span className="flex items-center gap-1.5">
            <span className="w-2.5 h-2.5 rounded-full bg-finova-danger" /> 100% Exceeded
          </span>
        </div>
      </div>

      {/* CATEGORY-WISE BUDGETS GRID */}
      <div>
        <h2 className="text-base font-extrabold text-finova-text mb-3">Category-Wise Allocations</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
          {currentBudget?.categories && currentBudget.categories.length > 0 ? (
            currentBudget.categories.map((cat) => {
              const spent = cat.spentAmount || 0;
              const limit = cat.allocatedAmount || cat.limitAmount || 1;
              const pct = Math.min(100, Math.round((spent / limit) * 100));
              const status = getAlertStatus(spent, limit);

              return (
                <div
                  key={cat.id || cat.categoryName}
                  className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs hover:border-lavender-200 transition-all flex flex-col justify-between"
                >
                  <div>
                    <div className="flex items-center justify-between mb-2">
                      <span className="text-xs font-bold text-finova-text">{cat.categoryName}</span>
                      <span className={`px-2 py-0.5 rounded-md text-[10px] border ${status.color}`}>
                        {status.label}
                      </span>
                    </div>

                    <div className="flex items-baseline justify-between mt-3">
                      <span className="text-lg font-black text-finova-text">{formatCurrency(spent)}</span>
                      <span className="text-xs text-finova-muted">of {formatCurrency(limit)}</span>
                    </div>

                    {/* Progress Bar */}
                    <div className="w-full h-2 rounded-full bg-secondary-bg border border-finova-border overflow-hidden mt-2">
                      <div
                        className={`h-full rounded-full transition-all duration-300 ${status.barColor}`}
                        style={{ width: `${pct}%` }}
                      />
                    </div>
                  </div>

                  <div className="mt-4 pt-3 border-t border-finova-border flex items-center justify-between text-[11px]">
                    <span className="text-finova-muted">Remaining Balance:</span>
                    <span className="font-extrabold text-sage-800">
                      {formatCurrency(Math.max(0, limit - spent))}
                    </span>
                  </div>
                </div>
              );
            })
          ) : (
            <div className="col-span-full p-8 rounded-2xl bg-white border border-finova-border text-center">
              <PieIcon className="w-8 h-8 text-primary mx-auto mb-2 opacity-60" />
              <p className="text-xs font-bold text-finova-text">No active category budgets set</p>
              <p className="text-[11px] text-finova-muted mt-1">
                Define category limits for food, education, and entertainment to prevent overspending.
              </p>
              <button
                onClick={handleOpenModal}
                className="mt-3 inline-flex items-center gap-1.5 px-4 py-2 rounded-xl bg-primary text-white text-xs font-bold"
              >
                <Plus className="w-3.5 h-3.5" /> Set Budget Now
              </button>
            </div>
          )}
        </div>
      </div>

      {/* CREATE / EDIT BUDGET MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-xl w-full p-6 space-y-4 max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <h3 className="text-base font-extrabold text-finova-text">Configure Monthly Budget</h3>
              <button onClick={() => setIsModalOpen(false)} className="text-finova-muted hover:text-finova-text">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4 text-xs">
              <div className="grid grid-cols-3 gap-3">
                <div className="col-span-1">
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">Month</label>
                  <select
                    value={month}
                    onChange={(e) => setMonth(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-xl border border-finova-border text-finova-text bg-white"
                  >
                    {[1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12].map((m) => (
                      <option key={m} value={m}>
                        {new Date(2026, m - 1).toLocaleString('default', { month: 'long' })}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="col-span-1">
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">Year</label>
                  <input
                    type="number"
                    value={year}
                    onChange={(e) => setYear(Number(e.target.value))}
                    className="w-full px-3 py-2 rounded-xl border border-finova-border text-finova-text font-bold"
                  />
                </div>
                <div className="col-span-1">
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Total Cap (₹)
                  </label>
                  <input
                    type="number"
                    required
                    value={totalLimit}
                    onChange={(e) => setTotalLimit(e.target.value)}
                    className="w-full px-3 py-2 rounded-xl border border-finova-border text-finova-text font-black text-primary"
                  />
                </div>
              </div>

              <div>
                <div className="flex items-center justify-between mb-2">
                  <span className="font-bold text-finova-text uppercase tracking-wider">
                    Category Allocations
                  </span>
                  <button
                    type="button"
                    onClick={handleAddCategoryRow}
                    className="text-primary font-bold hover:underline inline-flex items-center gap-1"
                  >
                    <Plus className="w-3.5 h-3.5" /> Add Category
                  </button>
                </div>

                <div className="space-y-2 max-h-56 overflow-y-auto pr-1">
                  {categories.map((c, idx) => (
                    <div key={idx} className="flex items-center gap-2">
                      <select
                        value={c.categoryName}
                        onChange={(e) => {
                          const updated = [...categories];
                          updated[idx].categoryName = e.target.value;
                          setCategories(updated);
                        }}
                        className="flex-1 px-3 py-2 rounded-xl border border-finova-border bg-white text-finova-text"
                      >
                        {DEFAULT_CATEGORIES.map((cat) => (
                          <option key={cat} value={cat}>
                            {cat}
                          </option>
                        ))}
                      </select>
                      <input
                        type="number"
                        placeholder="Limit (₹)"
                        value={c.allocatedAmount}
                        onChange={(e) => handleCategoryAllocChange(idx, e.target.value)}
                        className="w-32 px-3 py-2 rounded-xl border border-finova-border text-finova-text font-bold"
                      />
                      <button
                        type="button"
                        onClick={() => handleRemoveCategoryRow(idx)}
                        className="p-2 text-finova-muted hover:text-finova-danger"
                      >
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  ))}
                </div>
              </div>

              <div className="flex items-center justify-end gap-3 pt-4 border-t border-finova-border">
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
                  Save Budget Plan
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
