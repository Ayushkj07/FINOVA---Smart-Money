import React, { useState, useEffect } from 'react';
import {
  Users2,
  Calendar as CalendarIcon,
  Award,
  BookOpen,
  LineChart as LineChartIcon,
  Plus,
  CheckCircle2,
  X,
  TrendingUp,
  Smile,
  Frown,
  Meh,
  Flame,
  Check,
  ChevronLeft,
  ChevronRight
} from 'lucide-react';
import { featuresApi } from '../services/api';
import { ExpenseSplit, FinancialJournal, ChallengeParticipant, SentimentType } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';

export const AdvancedPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'SPLITS' | 'CALENDAR' | 'CHALLENGES' | 'JOURNAL' | 'PREDICTOR'>('SPLITS');

  // --- 1. BILL SPLITTER STATE ---
  const [splits, setSplits] = useState<ExpenseSplit[]>([]);
  const [isSplitModalOpen, setIsSplitModalOpen] = useState(false);
  const [splitTitle, setSplitTitle] = useState('');
  const [splitAmount, setSplitAmount] = useState('');
  const [membersInput, setMembersInput] = useState('Alex, Rahul, Priya');

  // --- 2. CALENDAR STATE ---
  const [calMonth, setCalMonth] = useState(new Date().getMonth() + 1);
  const [calYear, setCalYear] = useState(new Date().getFullYear());
  const [calEvents, setCalEvents] = useState<any[]>([]);

  // --- 3. CHALLENGES STATE ---
  const [challenges, setChallenges] = useState<ChallengeParticipant[]>([]);

  // --- 4. JOURNAL STATE ---
  const [journals, setJournals] = useState<FinancialJournal[]>([]);
  const [isJournalModalOpen, setIsJournalModalOpen] = useState(false);
  const [journalForm, setJournalForm] = useState({
    title: '',
    reflectionText: '',
    sentiment: 'PROUD' as SentimentType,
    entryDate: new Date().toISOString().split('T')[0],
  });

  // --- 5. PREDICTOR STATE ---
  const [predictorData, setPredictorData] = useState<any>(null);

  // Load active tab data
  useEffect(() => {
    if (activeTab === 'SPLITS') {
      featuresApi.getSplits().then((res) => setSplits(res.data)).catch(console.error);
    } else if (activeTab === 'CALENDAR') {
      featuresApi.getCalendar(calMonth, calYear).then((res) => setCalEvents(res.data)).catch(console.error);
    } else if (activeTab === 'CHALLENGES') {
      featuresApi.getChallenges().then((res) => setChallenges(res.data)).catch(console.error);
    } else if (activeTab === 'JOURNAL') {
      featuresApi.getJournal().then((res) => setJournals(res.data)).catch(console.error);
    } else if (activeTab === 'PREDICTOR') {
      featuresApi.getPredictor().then((res) => setPredictorData(res.data)).catch(console.error);
    }
  }, [activeTab, calMonth, calYear]);

  // Handle Create Split
  const handleCreateSplit = async (e: React.FormEvent) => {
    e.preventDefault();
    const names = membersInput.split(',').map((n) => n.trim()).filter(Boolean);
    if (names.length === 0) return;
    const total = parseFloat(splitAmount);
    const perPerson = total / (names.length + 1); // including current user

    try {
      const res = await featuresApi.createSplit({
        title: splitTitle,
        totalAmount: total,
        splitType: 'EQUAL',
        members: names.map((name) => ({
          memberName: name,
          allocatedAmount: perPerson,
          hasPaid: false,
        })),
      });
      setSplits((prev) => [res.data, ...prev]);
      setIsSplitModalOpen(false);
      setSplitTitle('');
      setSplitAmount('');
    } catch (err) {
      console.error('Failed to create split', err);
    }
  };

  const handleMarkPaid = async (splitId: number, memberId: number) => {
    try {
      const res = await featuresApi.markMemberPaid(splitId, memberId);
      setSplits((prev) => prev.map((s) => (s.id === splitId ? res.data : s)));
    } catch (err) {
      console.error('Failed to update member status', err);
    }
  };

  const handleSettleSplit = async (splitId: number) => {
    try {
      const res = await featuresApi.settleSplit(splitId);
      setSplits((prev) => prev.map((s) => (s.id === splitId ? res.data : s)));
    } catch (err) {
      console.error('Failed to settle split', err);
    }
  };

  // Handle Journal Save
  const handleSaveJournal = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await featuresApi.createJournal(journalForm);
      setJournals((prev) => [res.data, ...prev]);
      setIsJournalModalOpen(false);
      setJournalForm({
        title: '',
        reflectionText: '',
        sentiment: 'PROUD',
        entryDate: new Date().toISOString().split('T')[0],
      });
    } catch (err) {
      console.error('Failed to save journal', err);
    }
  };

  const getSentimentIcon = (sentiment: SentimentType) => {
    switch (sentiment) {
      case 'PROUD':
        return <Smile className="w-4 h-4 text-sage-700" />;
      case 'MOTIVATED':
        return <Flame className="w-4 h-4 text-primary" />;
      case 'REGRETFUL':
        return <Frown className="w-4 h-4 text-finova-danger" />;
      default:
        return <Meh className="w-4 h-4 text-finova-warning" />;
    }
  };

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-12">
      {/* HEADER & TAB BUTTONS */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-finova-text">Advanced Financial Suite</h1>
          <p className="text-xs text-finova-muted mt-0.5">
            Bill splitting, monthly calendar events, savings challenges, and emotional spending journals.
          </p>
        </div>

        {/* Tab Pills */}
        <div className="flex items-center gap-1.5 p-1 bg-white border border-finova-border rounded-xl shadow-xs overflow-x-auto">
          {[
            { id: 'SPLITS', label: 'Bill Splitter', icon: <Users2 className="w-3.5 h-3.5" /> },
            { id: 'CALENDAR', label: 'Financial Calendar', icon: <CalendarIcon className="w-3.5 h-3.5" /> },
            { id: 'CHALLENGES', label: 'Savings Challenges', icon: <Award className="w-3.5 h-3.5" /> },
            { id: 'JOURNAL', label: 'Money Journal', icon: <BookOpen className="w-3.5 h-3.5" /> },
            { id: 'PREDICTOR', label: 'Expense Predictor', icon: <LineChartIcon className="w-3.5 h-3.5" /> },
          ].map((tab) => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id as any)}
              className={`inline-flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-bold whitespace-nowrap transition-all ${
                activeTab === tab.id
                  ? 'bg-lavender-100 text-primary-dark shadow-xs'
                  : 'text-finova-muted hover:text-finova-text'
              }`}
            >
              {tab.icon}
              <span>{tab.label}</span>
            </button>
          ))}
        </div>
      </div>

      {/* 1. ROOMMATE BILL SPLITTER TAB */}
      {activeTab === 'SPLITS' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-base font-extrabold text-finova-text">Shared Expense Splits</h2>
            <button
              onClick={() => setIsSplitModalOpen(true)}
              className="inline-flex items-center gap-1.5 bg-primary text-white text-xs font-bold px-4 py-2 rounded-xl hover:bg-primary-dark"
            >
              <Plus className="w-4 h-4" />
              <span>Create New Split</span>
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {splits.length === 0 ? (
              <div className="col-span-full p-12 rounded-2xl bg-white border border-finova-border text-center">
                <Users2 className="w-10 h-10 text-primary mx-auto mb-3 opacity-60" />
                <h3 className="text-base font-bold text-finova-text">No Split Expenses Recorded</h3>
                <p className="text-xs text-finova-muted mt-1">
                  Split apartment WiFi, groceries, weekend trips, or group dinners with roommates.
                </p>
              </div>
            ) : (
              splits.map((s) => (
                <div key={s.id} className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs flex flex-col justify-between">
                  <div>
                    <div className="flex items-start justify-between gap-2 mb-2">
                      <h3 className="text-sm font-extrabold text-finova-text">{s.title}</h3>
                      <span
                        className={`px-2 py-0.5 rounded-md text-[10px] font-bold border ${
                          s.isSettled ? 'bg-sage-100 text-sage-800 border-sage-200' : 'bg-lavender-50 text-primary-dark border-lavender-200'
                        }`}
                      >
                        {s.isSettled ? 'Fully Settled' : 'Pending Payments'}
                      </span>
                    </div>

                    <div className="text-xl font-black text-finova-text my-2">
                      {formatCurrency(s.totalAmount)}
                    </div>

                    <div className="mt-4 pt-3 border-t border-finova-border space-y-2">
                      <span className="text-[10px] uppercase font-bold text-finova-muted block">Members Breakdown:</span>
                      {s.members?.map((m) => (
                        <div key={m.id} className="flex items-center justify-between text-xs py-1">
                          <span className="font-semibold text-finova-text">{m.memberName || m.name}</span>
                          <div className="flex items-center gap-2">
                            <span className="font-bold text-finova-muted">
                              {formatCurrency(m.allocatedAmount ?? m.owedAmount ?? 0)}
                            </span>
                            {m.hasPaid ?? m.isPaid ? (
                              <span className="px-2 py-0.5 rounded-md bg-sage-50 text-sage-800 text-[10px] font-bold">
                                Paid
                              </span>
                            ) : (
                              <button
                                onClick={() => handleMarkPaid(s.id, m.id)}
                                className="px-2 py-0.5 rounded-md bg-secondary-bg hover:bg-sage-100 text-finova-muted hover:text-sage-800 text-[10px] font-bold border border-finova-border"
                              >
                                Mark Paid
                              </button>
                            )}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {!s.isSettled && (
                    <button
                      onClick={() => handleSettleSplit(s.id)}
                      className="mt-4 w-full py-2 rounded-xl bg-sage-50 hover:bg-sage-100 text-sage-800 border border-sage-200 font-bold text-xs transition-colors"
                    >
                      Settle All & Close
                    </button>
                  )}
                </div>
              ))
            )}
          </div>
        </div>
      )}

      {/* 2. FINANCIAL CALENDAR TAB */}
      {activeTab === 'CALENDAR' && (
        <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs space-y-4">
          <div className="flex items-center justify-between pb-3 border-b border-finova-border">
            <h2 className="text-base font-extrabold text-finova-text">
              Monthly Financial Calendar ({new Date(calYear, calMonth - 1).toLocaleString('default', { month: 'long' })}{' '}
              {calYear})
            </h2>
            <div className="flex items-center gap-2">
              <button
                onClick={() => setCalMonth((prev) => (prev === 1 ? 12 : prev - 1))}
                className="p-1.5 rounded-lg border border-finova-border hover:bg-secondary-bg"
              >
                <ChevronLeft className="w-4 h-4" />
              </button>
              <button
                onClick={() => setCalMonth((prev) => (prev === 12 ? 1 : prev + 1))}
                className="p-1.5 rounded-lg border border-finova-border hover:bg-secondary-bg"
              >
                <ChevronRight className="w-4 h-4" />
              </button>
            </div>
          </div>

          <div className="grid grid-cols-7 gap-2 text-center text-xs font-bold text-finova-muted pb-2">
            <div>Sun</div>
            <div>Mon</div>
            <div>Tue</div>
            <div>Wed</div>
            <div>Thu</div>
            <div>Fri</div>
            <div>Sat</div>
          </div>

          {/* Calendar Grid Demo */}
          <div className="grid grid-cols-7 gap-2">
            {Array.from({ length: 31 }, (_, i) => i + 1).map((day) => {
              const hasSalary = day === 1;
              const hasSub = day === 15 || day === 28;
              const hasDebt = day === 10;

              return (
                <div
                  key={day}
                  className={`min-h-[75px] p-2 rounded-xl border text-left flex flex-col justify-between transition-all ${
                    day === new Date().getDate()
                      ? 'border-primary bg-lavender-50/40'
                      : 'border-finova-border bg-white hover:border-lavender-200'
                  }`}
                >
                  <span className="text-[11px] font-bold text-finova-text">{day}</span>
                  <div className="space-y-1">
                    {hasSalary && (
                      <span className="block px-1.5 py-0.5 rounded bg-sage-100 text-sage-800 text-[9px] font-extrabold truncate">
                        💰 Pocket Money
                      </span>
                    )}
                    {hasSub && (
                      <span className="block px-1.5 py-0.5 rounded bg-lavender-100 text-primary-dark text-[9px] font-extrabold truncate">
                        🔁 Renewal Due
                      </span>
                    )}
                    {hasDebt && (
                      <span className="block px-1.5 py-0.5 rounded bg-yellow-100 text-yellow-800 text-[9px] font-extrabold truncate">
                        🤝 Loan Repay
                      </span>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      )}

      {/* 3. SAVINGS CHALLENGES TAB */}
      {activeTab === 'CHALLENGES' && (
        <div className="space-y-6">
          <div>
            <h2 className="text-base font-extrabold text-finova-text">Gamified Money Challenges</h2>
            <p className="text-xs text-finova-muted mt-0.5">
              Turn disciplined habits into achievements. Complete milestones and unlock financial badges.
            </p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            {[
              {
                id: 1,
                title: '30-Day No-Zomato Challenge',
                desc: 'Prepare meals or eat in college canteen for 30 consecutive days.',
                target: '₹4,500 Saved',
                progress: 68,
                days: '18 / 30 Days',
                badge: 'Kitchen Master 🍳',
              },
              {
                id: 2,
                title: '52-Week ₹100 Stash Challenge',
                desc: 'Add ₹100 each week directly into your primary savings vault.',
                target: '₹5,200 Target',
                progress: 42,
                days: 'Week 22',
                badge: 'Consistency Champ 🏆',
              },
              {
                id: 3,
                title: 'Zero-Spend Weekend',
                desc: 'Spend zero non-essential rupees on Saturday and Sunday.',
                target: '₹1,200 Saved',
                progress: 100,
                days: 'Completed!',
                badge: 'Weekend Zen 🧘',
              },
            ].map((c) => (
              <div key={c.id} className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs flex flex-col justify-between">
                <div>
                  <div className="flex items-center justify-between mb-2">
                    <span className="px-2.5 py-1 rounded-full bg-lavender-100 text-primary-dark text-[10px] font-extrabold">
                      {c.badge}
                    </span>
                    <span className="text-[11px] font-bold text-sage-800">{c.target}</span>
                  </div>

                  <h3 className="text-sm font-extrabold text-finova-text mt-2">{c.title}</h3>
                  <p className="text-xs text-finova-muted mt-1 leading-relaxed">{c.desc}</p>

                  <div className="my-4">
                    <div className="flex items-center justify-between text-xs mb-1">
                      <span className="font-semibold text-finova-muted">Progress</span>
                      <span className="font-bold text-primary">{c.progress}%</span>
                    </div>
                    <div className="w-full h-2 rounded-full bg-secondary-bg overflow-hidden">
                      <div className="h-full bg-primary rounded-full" style={{ width: `${c.progress}%` }} />
                    </div>
                    <span className="text-[10px] text-finova-muted mt-1 block">{c.days}</span>
                  </div>
                </div>

                <button className="w-full py-2 rounded-xl bg-primary hover:bg-primary-dark text-white text-xs font-bold transition-all shadow-xs">
                  Log Progress Today
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* 4. FINANCIAL JOURNAL TAB */}
      {activeTab === 'JOURNAL' && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-base font-extrabold text-finova-text">Daily Money Reflection Journal</h2>
              <p className="text-xs text-finova-muted mt-0.5">
                Understand the psychology behind your spending. Track emotional triggers and financial pride.
              </p>
            </div>
            <button
              onClick={() => setIsJournalModalOpen(true)}
              className="inline-flex items-center gap-1.5 bg-primary text-white text-xs font-bold px-4 py-2 rounded-xl hover:bg-primary-dark"
            >
              <Plus className="w-4 h-4" />
              <span>New Journal Entry</span>
            </button>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {journals.length === 0 ? (
              <div className="col-span-full p-12 rounded-2xl bg-white border border-finova-border text-center">
                <BookOpen className="w-10 h-10 text-primary mx-auto mb-3 opacity-60" />
                <h3 className="text-base font-bold text-finova-text">No Journal Entries Yet</h3>
                <p className="text-xs text-finova-muted mt-1">
                  Start logging your daily thoughts to curb impulse buying and build mindfulness.
                </p>
              </div>
            ) : (
              journals.map((j) => (
                <div key={j.id} className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs space-y-2">
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-2">
                      {getSentimentIcon(j.sentiment)}
                      <span className="font-extrabold text-xs text-finova-text">{j.title}</span>
                    </div>
                    <span className="text-[10px] text-finova-muted font-medium">{formatDate(j.entryDate)}</span>
                  </div>
                  <p className="text-xs text-finova-muted leading-relaxed">{j.reflectionText || j.notes}</p>
                </div>
              ))
            )}
          </div>
        </div>
      )}

      {/* 5. FUTURE EXPENSE PREDICTOR TAB */}
      {activeTab === 'PREDICTOR' && (
        <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs space-y-6">
          <div>
            <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-lavender-100 text-primary-dark text-xs font-bold mb-2">
              <LineChartIcon className="w-3.5 h-3.5 text-primary" />
              <span>AI Historical Prediction Model</span>
            </div>
            <h2 className="text-lg font-extrabold text-finova-text">Next Month Expense Forecast</h2>
            <p className="text-xs text-finova-muted mt-1 max-w-2xl">
              Based on your last 90 days of transactions, active recurring subscriptions, and campus routine, FINOVA predicts your upcoming month outflows.
            </p>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
            <div className="p-5 rounded-2xl bg-secondary-bg border border-finova-border">
              <span className="text-[11px] font-bold text-finova-muted uppercase">Projected Spend</span>
              <p className="text-2xl font-black text-finova-danger mt-1">₹11,400.00</p>
              <span className="text-[11px] text-finova-muted mt-1 block">Expected upcoming month total</span>
            </div>
            <div className="p-5 rounded-2xl bg-secondary-bg border border-finova-border">
              <span className="text-[11px] font-bold text-finova-muted uppercase">Fixed Recurring Outflow</span>
              <p className="text-2xl font-black text-primary mt-1">₹3,200.00</p>
              <span className="text-[11px] text-finova-muted mt-1 block">Rent, WiFi, Subscriptions</span>
            </div>
            <div className="p-5 rounded-2xl bg-secondary-bg border border-finova-border">
              <span className="text-[11px] font-bold text-finova-muted uppercase">Recommended Safe Limit</span>
              <p className="text-2xl font-black text-sage-800 mt-1">₹14,000.00</p>
              <span className="text-[11px] text-finova-muted mt-1 block">Leaves ₹3,000 for emergency vault</span>
            </div>
          </div>
        </div>
      )}

      {/* CREATE SPLIT MODAL */}
      {isSplitModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-md w-full p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <h3 className="text-base font-extrabold text-finova-text">New Shared Expense Split</h3>
              <button onClick={() => setIsSplitModalOpen(false)} className="text-finova-muted hover:text-finova-text">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleCreateSplit} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Expense Title / Occasion
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Apartment WiFi, Goa Hotel Booking"
                  value={splitTitle}
                  onChange={(e) => setSplitTitle(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Total Bill Amount (₹)
                </label>
                <input
                  type="number"
                  min="1"
                  required
                  placeholder="3000"
                  value={splitAmount}
                  onChange={(e) => setSplitAmount(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text font-bold"
                />
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Roommates / Friends (Comma-separated)
                </label>
                <input
                  type="text"
                  required
                  placeholder="Rahul, Priya, David"
                  value={membersInput}
                  onChange={(e) => setMembersInput(e.target.value)}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
                <span className="text-[10px] text-finova-muted mt-1 block">
                  Amount will be split equally between everyone including yourself.
                </span>
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-finova-border">
                <button
                  type="button"
                  onClick={() => setIsSplitModalOpen(false)}
                  className="px-4 py-2 rounded-xl border border-finova-border font-bold text-finova-muted"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl bg-primary hover:bg-primary-dark font-bold text-white shadow-xs"
                >
                  Create Split
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* CREATE JOURNAL ENTRY MODAL */}
      {isJournalModalOpen && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-md w-full p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <h3 className="text-base font-extrabold text-finova-text">New Reflection Entry</h3>
              <button onClick={() => setIsJournalModalOpen(false)} className="text-finova-muted hover:text-finova-text">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleSaveJournal} className="space-y-4 text-xs">
              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Entry Title
                </label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Resisted buying sneakers on sale!"
                  value={journalForm.title}
                  onChange={(e) => setJournalForm({ ...journalForm, title: e.target.value })}
                  className="w-full px-3.5 py-2.5 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  How did you feel about your spending today?
                </label>
                <div className="grid grid-cols-4 gap-2">
                  {(['PROUD', 'MOTIVATED', 'NEUTRAL', 'REGRETFUL'] as SentimentType[]).map((s) => (
                    <button
                      key={s}
                      type="button"
                      onClick={() => setJournalForm({ ...journalForm, sentiment: s })}
                      className={`p-2 rounded-xl border text-[11px] font-bold flex flex-col items-center gap-1 transition-all ${
                        journalForm.sentiment === s
                          ? 'border-primary bg-lavender-50 text-primary-dark'
                          : 'border-finova-border text-finova-muted hover:bg-secondary-bg'
                      }`}
                    >
                      {getSentimentIcon(s)}
                      <span>{s}</span>
                    </button>
                  ))}
                </div>
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Reflection Notes
                </label>
                <textarea
                  rows={3}
                  required
                  placeholder="Write a few lines on what triggered today's decisions..."
                  value={journalForm.reflectionText}
                  onChange={(e) => setJournalForm({ ...journalForm, reflectionText: e.target.value })}
                  className="w-full px-3.5 py-2 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-finova-border">
                <button
                  type="button"
                  onClick={() => setIsJournalModalOpen(false)}
                  className="px-4 py-2 rounded-xl border border-finova-border font-bold text-finova-muted"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl bg-primary hover:bg-primary-dark font-bold text-white shadow-xs"
                >
                  Save Entry
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
