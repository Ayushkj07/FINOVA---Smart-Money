import React, { useState, useEffect } from 'react';
import {
  HandCoins,
  Plus,
  ArrowDownLeft,
  ArrowUpRight,
  CheckCircle2,
  AlertCircle,
  X,
  Trash2,
  Edit2,
  DollarSign,
  TrendingDown,
  Calendar
} from 'lucide-react';
import { debtApi } from '../services/api';
import { Debt, DebtType } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';
import { useOnRealtimeUpdate } from '../hooks/useRealtimeSync';

export const DebtPage: React.FC = () => {
  const [debts, setDebts] = useState<Debt[]>([]);
  const [loading, setLoading] = useState(true);
  const [activeTab, setActiveTab] = useState<'ALL' | 'BORROWED' | 'LENT'>('ALL');

  // Add / Edit Modal
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingDebt, setEditingDebt] = useState<Debt | null>(null);
  const [formData, setFormData] = useState({
    personName: '',
    debtType: 'BORROWED' as DebtType,
    initialAmount: '',
    dueDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
    description: '',
    interestRate: '0',
  });

  // Repayment Modal
  const [isRepayOpen, setIsRepayOpen] = useState(false);
  const [activeDebt, setActiveDebt] = useState<Debt | null>(null);
  const [repayAmount, setRepayAmount] = useState('');
  const [repayNote, setRepayNote] = useState('');

  const fetchDebts = async () => {
    setLoading(true);
    try {
      const res = await debtApi.getAll();
      setDebts(res.data);
    } catch (err) {
      console.error('Failed to load debts', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDebts();
  }, []);

  useOnRealtimeUpdate(fetchDebts, ['DEBT_UPDATED', 'DEBT_DELETED']);

  const handleOpenAddModal = (type: DebtType = 'BORROWED') => {
    setEditingDebt(null);
    setFormData({
      personName: '',
      debtType: type,
      initialAmount: '',
      dueDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
      description: '',
      interestRate: '0',
    });
    setIsModalOpen(true);
  };

  const handleOpenRepayModal = (debt: Debt) => {
    setActiveDebt(debt);
    const balance = debt.remainingAmount ?? debt.outstandingBalance ?? 0;
    setRepayAmount(String(balance));
    setRepayNote('Payment settlement');
    setIsRepayOpen(true);
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Delete this loan record?')) return;
    try {
      await debtApi.delete(id);
      setDebts((prev) => prev.filter((d) => d.id !== id));
    } catch (err) {
      console.error('Failed to delete debt', err);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload = {
        personName: formData.personName,
        debtType: formData.debtType,
        initialAmount: parseFloat(formData.initialAmount),
        dueDate: formData.dueDate,
        description: formData.description,
        interestRate: parseFloat(formData.interestRate || '0'),
      };

      if (editingDebt) {
        const res = await debtApi.update(editingDebt.id, payload);
        setDebts((prev) => prev.map((d) => (d.id === editingDebt.id ? res.data : d)));
      } else {
        const res = await debtApi.create(payload);
        setDebts((prev) => [res.data, ...prev]);
      }
      setIsModalOpen(false);
    } catch (err) {
      console.error('Failed to save debt', err);
    }
  };

  const handleAddRepayment = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeDebt) return;
    try {
      const res = await debtApi.addRepayment(activeDebt.id, {
        amount: parseFloat(repayAmount),
        note: repayNote,
        repaymentDate: new Date().toISOString().split('T')[0],
      });
      setDebts((prev) => prev.map((d) => (d.id === activeDebt.id ? res.data : d)));
      setIsRepayOpen(false);
    } catch (err) {
      console.error('Failed to record repayment', err);
    }
  };

  const filteredDebts = debts.filter((d) => {
    if (activeTab === 'ALL') return true;
    const t = d.debtType || d.type;
    return t === activeTab;
  });

  const totalBorrowedPending = debts
    .filter((d) => (d.debtType || d.type) === 'BORROWED' && d.status !== 'PAID')
    .reduce((acc, curr) => acc + Number(curr.remainingAmount ?? curr.outstandingBalance ?? 0), 0);

  const totalLentPending = debts
    .filter((d) => (d.debtType || d.type) === 'LENT' && d.status !== 'PAID')
    .reduce((acc, curr) => acc + Number(curr.remainingAmount ?? curr.outstandingBalance ?? 0), 0);

  const netBalance = totalLentPending - totalBorrowedPending;

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-12">
      {/* HEADER & ACTION */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-finova-text">Debts & Peer Loans</h1>
          <p className="text-xs text-finova-muted mt-0.5">
            Track money borrowed from or lent to college friends, roommates, and family.
          </p>
        </div>
        <div className="flex items-center gap-2">
          <button
            onClick={() => handleOpenAddModal('BORROWED')}
            className="inline-flex items-center gap-1.5 bg-red-50 hover:bg-red-100 text-finova-danger border border-red-200 px-3.5 py-2.5 rounded-xl text-xs font-bold transition-all"
          >
            <ArrowDownLeft className="w-4 h-4" />
            <span>I Borrowed</span>
          </button>
          <button
            onClick={() => handleOpenAddModal('LENT')}
            className="inline-flex items-center gap-1.5 bg-sage-50 hover:bg-sage-100 text-sage-800 border border-sage-200 px-3.5 py-2.5 rounded-xl text-xs font-bold transition-all"
          >
            <ArrowUpRight className="w-4 h-4" />
            <span>I Lent Money</span>
          </button>
        </div>
      </div>

      {/* METRICS */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">I Owe (Borrowed)</span>
          <p className="text-2xl font-black text-finova-danger mt-1">{formatCurrency(totalBorrowedPending)}</p>
          <span className="text-[11px] text-finova-muted mt-1.5 block">Pending debts to pay back</span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Owed to Me (Lent)</span>
          <p className="text-2xl font-black text-sage-800 mt-1">{formatCurrency(totalLentPending)}</p>
          <span className="text-[11px] text-finova-muted mt-1.5 block">Pending money to collect</span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Net Position</span>
          <p className={`text-2xl font-black mt-1 ${netBalance >= 0 ? 'text-sage-800' : 'text-finova-danger'}`}>
            {netBalance >= 0 ? `+${formatCurrency(netBalance)}` : `-${formatCurrency(Math.abs(netBalance))}`}
          </p>
          <span className="text-[11px] text-finova-muted mt-1.5 block">
            {netBalance >= 0 ? 'You are owed more than you owe' : 'You owe more than you are owed'}
          </span>
        </div>
      </div>

      {/* TABS */}
      <div className="flex items-center gap-2 border-b border-finova-border pb-3">
        {(['ALL', 'BORROWED', 'LENT'] as const).map((tab) => (
          <button
            key={tab}
            onClick={() => setActiveTab(tab)}
            className={`px-4 py-2 rounded-xl text-xs font-bold transition-all ${
              activeTab === tab
                ? 'bg-lavender-100 text-primary-dark shadow-xs'
                : 'text-finova-muted hover:text-finova-text'
            }`}
          >
            {tab === 'ALL' ? 'All Loans' : tab === 'BORROWED' ? 'Borrowed (I Owe)' : 'Lent (Owed to Me)'}
          </button>
        ))}
      </div>

      {/* DEBTS GRID */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {loading ? (
          <div className="col-span-full py-12 text-center text-finova-muted text-xs">Loading loans...</div>
        ) : filteredDebts.length === 0 ? (
          <div className="col-span-full p-12 rounded-2xl bg-white border border-finova-border text-center">
            <HandCoins className="w-10 h-10 text-primary mx-auto mb-3 opacity-60" />
            <h3 className="text-base font-bold text-finova-text">No Debt Records Found</h3>
            <p className="text-xs text-finova-muted mt-1">Track loans between classmates with clear settlement records.</p>
          </div>
        ) : (
          filteredDebts.map((d) => {
            const isPaid = d.status === 'PAID';
            const isBorrowed = (d.debtType || d.type) === 'BORROWED';
            const person = d.personName || d.counterpartyName || 'Peer';
            const remaining = d.remainingAmount ?? d.outstandingBalance ?? 0;

            return (
              <div
                key={d.id}
                className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs hover:border-lavender-200 transition-all flex flex-col justify-between"
              >
                <div>
                  <div className="flex items-start justify-between gap-2 mb-3">
                    <div>
                      <div className="flex items-center gap-2">
                        <span className="font-extrabold text-sm text-finova-text">{person}</span>
                        <span
                          className={`px-2 py-0.5 rounded-md text-[10px] font-bold ${
                            isBorrowed ? 'bg-red-50 text-finova-danger' : 'bg-sage-50 text-sage-800'
                          }`}
                        >
                          {isBorrowed ? 'I Borrowed' : 'I Lent'}
                        </span>
                      </div>
                      {d.description && (
                        <p className="text-xs text-finova-muted mt-1 line-clamp-1">{d.description}</p>
                      )}
                    </div>

                    <span
                      className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold border ${
                        isPaid
                          ? 'bg-sage-100 text-sage-800 border-sage-200'
                          : 'bg-yellow-50 text-yellow-800 border-yellow-200'
                      }`}
                    >
                      {d.status}
                    </span>
                  </div>

                  <div className="my-4 space-y-1">
                    <span className="text-[11px] text-finova-muted uppercase font-bold">Remaining Balance</span>
                    <div className="flex items-baseline gap-2">
                      <span className={`text-2xl font-black ${isBorrowed ? 'text-finova-danger' : 'text-sage-800'}`}>
                        {formatCurrency(remaining)}
                      </span>
                      <span className="text-xs text-finova-muted">of {formatCurrency(d.initialAmount)}</span>
                    </div>
                    <div className="text-[11px] text-finova-muted pt-1 flex items-center gap-1">
                      <Calendar className="w-3.5 h-3.5" /> Due: {formatDate(d.dueDate)}
                    </div>
                  </div>
                </div>

                <div className="pt-4 border-t border-finova-border flex items-center justify-between gap-2">
                  <button
                    onClick={() => handleOpenRepayModal(d)}
                    disabled={isPaid}
                    className="flex-1 py-2 px-3 rounded-xl bg-lavender-50 hover:bg-lavender-100 text-primary-dark border border-lavender-200 text-xs font-bold transition-colors disabled:opacity-50"
                  >
                    Record Repayment
                  </button>
                  <button
                    onClick={() => handleDelete(d.id)}
                    className="p-2 rounded-xl hover:bg-red-50 text-finova-muted hover:text-finova-danger"
                  >
                    <Trash2 className="w-3.5 h-3.5" />
                  </button>
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* ADD / EDIT DEBT MODAL */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-md w-full p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <h3 className="text-base font-extrabold text-finova-text">
                {formData.debtType === 'BORROWED' ? 'Record Borrowed Money' : 'Record Money Lent'}
              </h3>
              <button onClick={() => setIsModalOpen(false)} className="text-finova-muted hover:text-finova-text">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSubmit} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  {formData.debtType === 'BORROWED' ? "Lender's Name (Who gave you money?)" : "Borrower's Name (Who owes you?)"}
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Rahul Verma / Roommate"
                  value={formData.personName}
                  onChange={(e) => setFormData({ ...formData, personName: e.target.value })}
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
                    min="1"
                    required
                    placeholder="2000"
                    value={formData.initialAmount}
                    onChange={(e) => setFormData({ ...formData, initialAmount: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text font-bold"
                  />
                </div>

                <div>
                  <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                    Due Date
                  </label>
                  <input
                    type="date"
                    required
                    value={formData.dueDate}
                    onChange={(e) => setFormData({ ...formData, dueDate: e.target.value })}
                    className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                  />
                </div>
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Reason / Description
                </label>
                <input
                  type="text"
                  placeholder="e.g. Weekend concert tickets split"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
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
                  Save Loan Record
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* RECORD REPAYMENT MODAL */}
      {isRepayOpen && activeDebt && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-sm w-full p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <div>
                <h3 className="text-sm font-extrabold text-finova-text">Record Repayment</h3>
                <span className="text-[11px] text-finova-muted">with {activeDebt.personName || activeDebt.counterpartyName}</span>
              </div>
              <button onClick={() => setIsRepayOpen(false)} className="text-finova-muted hover:text-finova-text">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleAddRepayment} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Repayment Amount (₹)
                </label>
                <input
                  type="number"
                  min="1"
                  max={activeDebt.remainingAmount ?? activeDebt.outstandingBalance ?? activeDebt.initialAmount}
                  required
                  value={repayAmount}
                  onChange={(e) => setRepayAmount(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text font-black text-primary text-lg"
                />
                <span className="text-[10px] text-finova-muted mt-1 block">
                  Remaining pending: {formatCurrency(activeDebt.remainingAmount ?? activeDebt.outstandingBalance)}
                </span>
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Settlement Note
                </label>
                <input
                  type="text"
                  placeholder="e.g. Paid via Google Pay"
                  value={repayNote}
                  onChange={(e) => setRepayNote(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-finova-border">
                <button
                  type="button"
                  onClick={() => setIsRepayOpen(false)}
                  className="px-4 py-2 rounded-xl border border-finova-border font-bold text-finova-muted"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl bg-primary hover:bg-primary-dark font-bold text-white shadow-xs"
                >
                  Confirm Settlement
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
