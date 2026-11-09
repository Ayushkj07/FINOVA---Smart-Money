import React, { useState, useEffect } from 'react';
import {
  ArrowDownLeft,
  Plus,
  Search,
  Filter,
  Trash2,
  Edit2,
  Calendar,
  X,
  TrendingUp,
  Repeat,
  Wallet
} from 'lucide-react';
import { incomeApi } from '../services/api';
import { Income, PaymentMethod } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';
import { useOnRealtimeUpdate } from '../hooks/useRealtimeSync';

const INCOME_CATEGORIES = [
  'Family Allowance',
  'Part-time Job',
  'Internship Stipend',
  'Freelancing',
  'Scholarship',
  'Business Income',
  'Gift',
  'Other',
];

const PAYMENT_METHODS: PaymentMethod[] = ['UPI', 'BANK_TRANSFER', 'CASH', 'DEBIT_CARD', 'CREDIT_CARD'];

export const IncomePage: React.FC = () => {
  const [incomes, setIncomes] = useState<Income[]>([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedCategory, setSelectedCategory] = useState<string>('ALL');

  // Modal State
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingIncome, setEditingIncome] = useState<Income | null>(null);
  const [formData, setFormData] = useState({
    title: '',
    amount: '',
    categoryName: 'Family Allowance',
    incomeDate: new Date().toISOString().split('T')[0],
    paymentMethod: 'UPI' as PaymentMethod,
    description: '',
    isRecurring: false,
  });

  const fetchIncomes = async () => {
    setLoading(true);
    try {
      const res = await incomeApi.getAll();
      setIncomes(res.data);
    } catch (err) {
      console.error('Failed to fetch incomes', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchIncomes();
  }, []);

  useOnRealtimeUpdate(fetchIncomes, ['INCOME_CREATED', 'INCOME_UPDATED', 'INCOME_DELETED']);

  const handleOpenAddModal = () => {
    setEditingIncome(null);
    setFormData({
      title: '',
      amount: '',
      categoryName: 'Family Allowance',
      incomeDate: new Date().toISOString().split('T')[0],
      paymentMethod: 'UPI',
      description: '',
      isRecurring: false,
    });
    setIsModalOpen(true);
  };

  const handleOpenEditModal = (income: Income) => {
    setEditingIncome(income);
    setFormData({
      title: income.title,
      amount: String(income.amount),
      categoryName: income.categoryName || income.category,
      incomeDate: income.incomeDate || income.date || new Date().toISOString().split('T')[0],
      paymentMethod: income.paymentMethod,
      description: income.description || '',
      isRecurring: income.isRecurring || false,
    });
    setIsModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this income entry?')) return;
    try {
      await incomeApi.delete(id);
      setIncomes((prev) => prev.filter((i) => i.id !== id));
    } catch (err) {
      console.error('Failed to delete income', err);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        title: formData.title,
        amount: parseFloat(formData.amount),
        categoryName: formData.categoryName,
        incomeDate: formData.incomeDate,
        paymentMethod: formData.paymentMethod,
        description: formData.description,
        isRecurring: formData.isRecurring,
      };

      if (editingIncome) {
        const res = await incomeApi.update(editingIncome.id, payload);
        setIncomes((prev) => prev.map((item) => (item.id === editingIncome.id ? res.data : item)));
      } else {
        const res = await incomeApi.create(payload);
        setIncomes((prev) => [res.data, ...prev]);
      }
      setIsModalOpen(false);
    } catch (err) {
      console.error('Failed to save income', err);
    }
  };

  // Filtered & Search items
  const filteredIncomes = incomes.filter((i) => {
    const catName = i.categoryName || i.category || '';
    const matchesSearch =
      i.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      catName.toLowerCase().includes(searchQuery.toLowerCase()) ||
      (i.description && i.description.toLowerCase().includes(searchQuery.toLowerCase()));
    const matchesCategory = selectedCategory === 'ALL' || catName === selectedCategory;
    return matchesSearch && matchesCategory;
  });

  const totalInflow = incomes.reduce((acc, curr) => acc + Number(curr.amount), 0);
  const recurringInflow = incomes.filter((i) => i.isRecurring).reduce((acc, curr) => acc + Number(curr.amount), 0);

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-12">
      {/* HEADER & METRIC SUMMARY */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-finova-text">Income & Inflows</h1>
          <p className="text-xs text-finova-muted mt-0.5">
            Track pocket money, family support, freelancing, and college stipends.
          </p>
        </div>
        <button
          onClick={handleOpenAddModal}
          className="inline-flex items-center gap-2 bg-primary hover:bg-primary-dark text-white px-4 py-2.5 rounded-xl text-xs font-bold transition-all shadow-xs"
        >
          <Plus className="w-4 h-4" />
          <span>Add Inflow</span>
        </button>
      </div>

      {/* METRIC CARDS */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Total Inflow</span>
          <p className="text-2xl font-black text-finova-text mt-1">{formatCurrency(totalInflow)}</p>
          <span className="text-[11px] font-bold text-finova-success flex items-center gap-1 mt-1.5">
            <TrendingUp className="w-3.5 h-3.5" /> All recorded earnings
          </span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Recurring Inflow</span>
          <p className="text-2xl font-black text-sage-800 mt-1">{formatCurrency(recurringInflow)}</p>
          <span className="text-[11px] text-finova-muted mt-1.5 flex items-center gap-1">
            <Repeat className="w-3.5 h-3.5" /> Monthly allowances & stipends
          </span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Active Inflow Entries</span>
          <p className="text-2xl font-black text-primary mt-1">{incomes.length}</p>
          <span className="text-[11px] text-finova-muted mt-1.5 flex items-center gap-1">
            <Wallet className="w-3.5 h-3.5" /> Total transactions logged
          </span>
        </div>
      </div>

      {/* FILTERS & SEARCH BAR */}
      <div className="flex flex-col sm:flex-row items-center justify-between gap-3 bg-white p-4 rounded-2xl border border-finova-border shadow-xs">
        <div className="relative w-full sm:w-80">
          <Search className="w-4 h-4 text-finova-muted absolute left-3.5 top-3" />
          <input
            type="text"
            placeholder="Search by title, source, or notes..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="w-full pl-9 pr-4 py-2 text-xs rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
          />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto overflow-x-auto">
          <Filter className="w-3.5 h-3.5 text-finova-muted flex-shrink-0" />
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="px-3 py-1.5 text-xs rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
          >
            <option value="ALL">All Sources</option>
            {INCOME_CATEGORIES.map((cat) => (
              <option key={cat} value={cat}>
                {cat}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* INCOMES TABLE */}
      <div className="bg-white rounded-2xl border border-finova-border shadow-xs overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-secondary-bg border-b border-finova-border text-finova-muted font-bold uppercase tracking-wider">
              <tr>
                <th className="py-3.5 px-6">Source / Title</th>
                <th className="py-3.5 px-6">Category</th>
                <th className="py-3.5 px-6">Date</th>
                <th className="py-3.5 px-6">Method</th>
                <th className="py-3.5 px-6">Type</th>
                <th className="py-3.5 px-6 text-right">Amount</th>
                <th className="py-3.5 px-6 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-finova-border font-medium text-finova-text">
              {loading ? (
                <tr>
                  <td colSpan={7} className="py-12 text-center text-finova-muted">
                    Loading income records...
                  </td>
                </tr>
              ) : filteredIncomes.length === 0 ? (
                <tr>
                  <td colSpan={7} className="py-12 text-center text-finova-muted">
                    No income records found matching your filters.
                  </td>
                </tr>
              ) : (
                filteredIncomes.map((item) => (
                  <tr key={item.id} className="hover:bg-secondary-bg/50 transition-colors">
                    <td className="py-4 px-6">
                      <div className="font-bold">{item.title}</div>
                      {item.description && (
                        <div className="text-[11px] text-finova-muted font-normal mt-0.5">{item.description}</div>
                      )}
                    </td>
                    <td className="py-4 px-6">
                      <span className="px-2.5 py-1 rounded-lg bg-lavender-50 text-primary-dark font-semibold text-[11px] border border-lavender-200">
                        {item.categoryName || item.category}
                      </span>
                    </td>
                    <td className="py-4 px-6 text-finova-muted">{formatDate(item.incomeDate || item.date)}</td>
                    <td className="py-4 px-6">
                      <span className="px-2 py-0.5 rounded-md bg-secondary-bg border border-finova-border text-[11px]">
                        {item.paymentMethod}
                      </span>
                    </td>
                    <td className="py-4 px-6">
                      {item.isRecurring ? (
                        <span className="px-2 py-0.5 rounded-md bg-sage-50 text-sage-800 border border-sage-200 text-[10px] font-bold flex items-center gap-1 w-fit">
                          <Repeat className="w-3 h-3" /> Recurring
                        </span>
                      ) : (
                        <span className="text-finova-muted text-[11px]">One-time</span>
                      )}
                    </td>
                    <td className="py-4 px-6 text-right font-black text-sage-800 text-sm">
                      +{formatCurrency(item.amount)}
                    </td>
                    <td className="py-4 px-6 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <button
                          onClick={() => handleOpenEditModal(item)}
                          className="p-1.5 rounded-lg hover:bg-secondary-bg text-finova-muted hover:text-finova-text transition-colors"
                        >
                          <Edit2 className="w-3.5 h-3.5" />
                        </button>
                        <button
                          onClick={() => handleDelete(item.id)}
                          className="p-1.5 rounded-lg hover:bg-red-50 text-finova-muted hover:text-finova-danger transition-colors"
                        >
                          <Trash2 className="w-3.5 h-3.5" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* ADD / EDIT MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-lg w-full p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <h3 className="text-base font-extrabold text-finova-text">
                {editingIncome ? 'Edit Income Entry' : 'Record New Income'}
              </h3>
              <button
                onClick={() => setIsModalOpen(false)}
                className="p-1 rounded-lg text-finova-muted hover:bg-secondary-bg"
              >
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Income Title / Source
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Monthly Pocket Money from Parents"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Amount (₹)
                  </label>
                  <input
                    type="number"
                    step="0.01"
                    min="1"
                    required
                    placeholder="15000"
                    value={formData.amount}
                    onChange={(e) => setFormData({ ...formData, amount: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text font-bold"
                  />
                </div>

                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Category
                  </label>
                  <select
                    value={formData.categoryName}
                    onChange={(e) => setFormData({ ...formData, categoryName: e.target.value })}
                    className="w-full px-3 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                  >
                    {INCOME_CATEGORIES.map((cat) => (
                      <option key={cat} value={cat}>
                        {cat}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Date Received
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.incomeDate}
                    onChange={(e) => setFormData({ ...formData, incomeDate: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                  />
                </div>

                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Payment Method
                  </label>
                  <select
                    value={formData.paymentMethod}
                    onChange={(e) => setFormData({ ...formData, paymentMethod: e.target.value as PaymentMethod })}
                    className="w-full px-3 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text bg-white"
                  >
                    {PAYMENT_METHODS.map((m) => (
                      <option key={m} value={m}>
                        {m}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Notes / Description (Optional)
                </label>
                <textarea
                  rows={2}
                  placeholder="Optional notes or details..."
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  className="w-full px-3.5 py-2 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="flex items-center gap-2 pt-1">
                <input
                  type="checkbox"
                  id="recurringCheck"
                  checked={formData.isRecurring}
                  onChange={(e) => setFormData({ ...formData, isRecurring: e.target.checked })}
                  className="rounded border-finova-border text-primary focus:ring-primary"
                />
                <label htmlFor="recurringCheck" className="text-xs font-semibold text-finova-text cursor-pointer">
                  Repeat monthly as recurring inflow
                </label>
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-finova-border">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  className="px-4 py-2 rounded-xl border border-finova-border font-bold text-finova-muted hover:text-finova-text"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl bg-primary hover:bg-primary-dark font-bold text-white shadow-xs"
                >
                  {editingIncome ? 'Update Inflow' : 'Save Inflow'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
