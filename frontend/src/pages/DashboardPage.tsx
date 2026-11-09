import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  Wallet,
  ArrowDownLeft,
  ArrowUpRight,
  PiggyBank,
  HandCoins,
  PieChart as PieIcon,
  TrendingUp,
  TrendingDown,
  Sparkles,
  Calendar,
  AlertTriangle,
  CheckCircle2,
  ChevronRight,
  ShieldCheck,
  RefreshCw,
  Plus
} from 'lucide-react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
} from 'recharts';
import { dashboardApi, expenseApi, savingsApi, debtApi, aiApi } from '../services/api';
import { DashboardSummary, DashboardAnalytics, HealthScore, Expense, SavingsGoal, Debt, AiInsights } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';
import { useAuth } from '../contexts/AuthContext';
import { useOnRealtimeUpdate } from '../hooks/useRealtimeSync';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();
  const [timeFilter, setTimeFilter] = useState('THIS_MONTH');
  const [loading, setLoading] = useState(true);

  const [summary, setSummary] = useState<DashboardSummary | null>(null);
  const [analytics, setAnalytics] = useState<DashboardAnalytics | null>(null);
  const [healthScore, setHealthScore] = useState<HealthScore | null>(null);
  const [recentExpenses, setRecentExpenses] = useState<Expense[]>([]);
  const [savingsGoals, setSavingsGoals] = useState<SavingsGoal[]>([]);
  const [debts, setDebts] = useState<Debt[]>([]);
  const [aiInsights, setAiInsights] = useState<AiInsights | null>(null);

  const fetchDashboardData = async () => {
    try {
      const [sumRes, anaRes, healthRes] = await Promise.all([
        dashboardApi.getSummary(timeFilter),
        dashboardApi.getAnalytics(timeFilter),
        dashboardApi.getHealthScore(),
      ]);

      setSummary(sumRes.data);
      setAnalytics(anaRes.data);
      setHealthScore(healthRes.data);

      // Secondary overview data
      const [expRes, savRes, debtRes, aiRes] = await Promise.allSettled([
        expenseApi.getAll(),
        savingsApi.getAll(),
        debtApi.getAll(),
        aiApi.getInsights(),
      ]);

      if (expRes.status === 'fulfilled') setRecentExpenses(expRes.value.data.slice(0, 5));
      if (savRes.status === 'fulfilled') setSavingsGoals(savRes.value.data.slice(0, 3));
      if (debtRes.status === 'fulfilled') setDebts(debtRes.value.data.filter(d => d.status === 'ACTIVE').slice(0, 3));
      if (aiRes.status === 'fulfilled') setAiInsights(aiRes.value.data);
    } catch (err) {
      console.error('Failed to load dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, [timeFilter]);

  useOnRealtimeUpdate(fetchDashboardData);

  const getTimeGreeting = () => {
    const hour = new Date().getHours();
    if (hour < 12) return 'Good morning';
    if (hour < 17) return 'Good afternoon';
    return 'Good evening';
  };

  const getHealthBadgeColor = (grade: string = 'A') => {
    switch (grade) {
      case 'A+':
      case 'A':
        return 'bg-sage-100 text-sage-800 border-sage-200';
      case 'B':
        return 'bg-lavender-100 text-primary-dark border-lavender-200';
      case 'C':
        return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      default:
        return 'bg-red-100 text-finova-danger border-red-200';
    }
  };

  const timeFilterOptions = [
    { label: 'Today', value: 'TODAY' },
    { label: 'This Week', value: 'THIS_WEEK' },
    { label: 'This Month', value: 'THIS_MONTH' },
    { label: 'Last Month', value: 'LAST_MONTH' },
    { label: 'Last 3 Months', value: 'LAST_3_MONTHS' },
    { label: 'This Year', value: 'THIS_YEAR' },
  ];

  const pieColors = ['#9B87F5', '#A8C9B5', '#E8B96B', '#DF858B', '#7E69AB', '#65A982', '#94A3B8'];

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-12">
      {/* 1. WELCOME BANNER & DATE FILTER CONTROLS */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl sm:text-3xl font-extrabold text-finova-text tracking-tight">
            {getTimeGreeting()},{' '}
            <span className="text-primary">{summary?.studentName || user?.fullName || 'Student'}</span>!
          </h1>
          <p className="text-xs sm:text-sm text-finova-muted mt-1">
            Here's your real-time financial overview and pocket money analysis.
          </p>
        </div>

        {/* Date Filters */}
        <div className="flex items-center gap-1.5 p-1 bg-white border border-finova-border rounded-xl shadow-xs overflow-x-auto">
          {timeFilterOptions.map((opt) => (
            <button
              key={opt.value}
              onClick={() => setTimeFilter(opt.value)}
              className={`px-3 py-1.5 rounded-lg text-xs font-semibold whitespace-nowrap transition-all ${
                timeFilter === opt.value
                  ? 'bg-lavender-100 text-primary-dark shadow-xs'
                  : 'text-finova-muted hover:text-finova-text'
              }`}
            >
              {opt.label}
            </button>
          ))}
        </div>
      </div>

      {/* 2. 6 SUMMARY CARDS */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-6 gap-4">
        {/* Card 1: Total Balance */}
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs hover:border-lavender-200 transition-all">
          <div className="flex items-center justify-between mb-3">
            <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Total Balance</span>
            <div className="w-8 h-8 rounded-xl bg-lavender-50 flex items-center justify-center text-primary">
              <Wallet className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-finova-text">
            {formatCurrency(summary?.totalBalance, summary?.currency)}
          </p>
          <div className="flex items-center gap-1 mt-2 text-[11px] font-bold text-finova-success">
            <TrendingUp className="w-3.5 h-3.5" />
            <span>+{summary?.balanceChangePercentage || 0}% vs last mo</span>
          </div>
        </div>

        {/* Card 2: Monthly Income */}
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs hover:border-sage-200 transition-all">
          <div className="flex items-center justify-between mb-3">
            <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Income Inflow</span>
            <div className="w-8 h-8 rounded-xl bg-sage-50 flex items-center justify-center text-sage-700">
              <ArrowDownLeft className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-finova-text">
            {formatCurrency(summary?.monthlyIncome, summary?.currency)}
          </p>
          <div className="flex items-center gap-1 mt-2 text-[11px] font-bold text-sage-800">
            <TrendingUp className="w-3.5 h-3.5" />
            <span>+{summary?.incomeChangePercentage || 0}% change</span>
          </div>
        </div>

        {/* Card 3: Monthly Expenses */}
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs hover:border-red-200 transition-all">
          <div className="flex items-center justify-between mb-3">
            <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Total Expenses</span>
            <div className="w-8 h-8 rounded-xl bg-red-50 flex items-center justify-center text-finova-danger">
              <ArrowUpRight className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-finova-text">
            {formatCurrency(summary?.monthlyExpenses, summary?.currency)}
          </p>
          <div className="flex items-center gap-1 mt-2 text-[11px] font-bold text-finova-danger">
            <TrendingDown className="w-3.5 h-3.5" />
            <span>{summary?.expenseChangePercentage || 0}% vs limit</span>
          </div>
        </div>

        {/* Card 4: Total Savings */}
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs hover:border-lavender-200 transition-all">
          <div className="flex items-center justify-between mb-3">
            <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Total Savings</span>
            <div className="w-8 h-8 rounded-xl bg-lavender-50 flex items-center justify-center text-primary">
              <PiggyBank className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-primary">
            {formatCurrency(summary?.totalSavings, summary?.currency)}
          </p>
          <span className="text-[11px] text-finova-muted font-medium mt-2 block">
            Across active vaults
          </span>
        </div>

        {/* Card 5: Outstanding Debt */}
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs hover:border-yellow-200 transition-all">
          <div className="flex items-center justify-between mb-3">
            <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Active Debts</span>
            <div className="w-8 h-8 rounded-xl bg-yellow-50 flex items-center justify-center text-finova-warning">
              <HandCoins className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-finova-text">
            {formatCurrency(summary?.outstandingDebt, summary?.currency)}
          </p>
          <span className="text-[11px] text-finova-muted font-medium mt-2 block">
            Net pending balance
          </span>
        </div>

        {/* Card 6: Remaining Budget */}
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs hover:border-sage-200 transition-all">
          <div className="flex items-center justify-between mb-3">
            <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Budget Left</span>
            <div className="w-8 h-8 rounded-xl bg-sage-50 flex items-center justify-center text-sage-700">
              <PieIcon className="w-4 h-4" />
            </div>
          </div>
          <p className="text-xl font-black text-sage-800">
            {formatCurrency(summary?.remainingMonthlyBudget, summary?.currency)}
          </p>
          <span className="text-[11px] text-finova-muted font-medium mt-2 block">
            {summary?.budgetUtilizationPercentage || 0}% utilized
          </span>
        </div>
      </div>

      {/* 3. HEALTH SCORE BANNER */}
      {healthScore && (
        <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs flex flex-col lg:flex-row lg:items-center justify-between gap-6">
          <div className="flex items-start sm:items-center gap-4">
            <div className="w-14 h-14 rounded-2xl bg-lavender-100 flex items-center justify-center text-primary flex-shrink-0">
              <ShieldCheck className="w-7 h-7" />
            </div>
            <div>
              <div className="flex items-center gap-3">
                <h3 className="text-base font-extrabold text-finova-text">Student Financial Health Score</h3>
                <span className={`px-2.5 py-0.5 rounded-full text-xs font-black border ${getHealthBadgeColor(healthScore.grade)}`}>
                  Grade {healthScore.grade} ({healthScore.overallScore}/100)
                </span>
              </div>
              <p className="text-xs text-finova-muted mt-1 max-w-xl">
                {healthScore.suggestions?.[0] || 'Keep your monthly spending below 75% of your allowance to boost your savings rate.'}
              </p>
            </div>
          </div>

          <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-4 lg:pt-0 border-t lg:border-t-0 border-finova-border">
            <div className="text-left lg:text-center">
              <span className="text-[10px] uppercase font-bold text-finova-muted">Savings Discipline</span>
              <p className="text-sm font-extrabold text-finova-text mt-0.5">{healthScore.savingsRateScore}/100</p>
            </div>
            <div className="text-left lg:text-center">
              <span className="text-[10px] uppercase font-bold text-finova-muted">Budget Control</span>
              <p className="text-sm font-extrabold text-finova-text mt-0.5">{healthScore.budgetDisciplineScore}/100</p>
            </div>
            <div className="text-left lg:text-center">
              <span className="text-[10px] uppercase font-bold text-finova-muted">Debt Safety</span>
              <p className="text-sm font-extrabold text-finova-text mt-0.5">{healthScore.debtManagementScore}/100</p>
            </div>
            <div className="text-left lg:text-center">
              <span className="text-[10px] uppercase font-bold text-finova-muted">Emergency Buffer</span>
              <p className="text-sm font-extrabold text-finova-text mt-0.5">{healthScore.emergencySavingsScore}/100</p>
            </div>
          </div>
        </div>
      )}

      {/* 4. CHARTS SECTION (Recharts) */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Income vs Expenses Trend (2 cols) */}
        <div className="lg:col-span-2 p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h3 className="text-sm font-extrabold text-finova-text">Income vs. Expense Trend</h3>
              <p className="text-xs text-finova-muted">Monthly financial flow comparison</p>
            </div>
            <div className="flex items-center gap-4 text-xs font-semibold">
              <span className="flex items-center gap-1.5 text-sage-800">
                <span className="w-2.5 h-2.5 rounded-full bg-sage-500" /> Income
              </span>
              <span className="flex items-center gap-1.5 text-primary">
                <span className="w-2.5 h-2.5 rounded-full bg-primary" /> Expenses
              </span>
            </div>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart
                data={analytics?.incomeVsExpenseTrend || [
                  { label: 'W1', income: 6000, expense: 2200 },
                  { label: 'W2', income: 7000, expense: 3400 },
                  { label: 'W3', income: 6500, expense: 2800 },
                  { label: 'W4', income: 8000, expense: 3100 },
                ]}
                margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
              >
                <defs>
                  <linearGradient id="colorIncome" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#A8C9B5" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#A8C9B5" stopOpacity={0.0} />
                  </linearGradient>
                  <linearGradient id="colorExpense" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#9B87F5" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#9B87F5" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#EEEEF2" />
                <XAxis dataKey="label" tick={{ fontSize: 11, fill: '#858392' }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fontSize: 11, fill: '#858392' }} axisLine={false} tickLine={false} />
                <Tooltip
                  formatter={(val: any) => [formatCurrency(Number(val)), '']}
                  contentStyle={{ backgroundColor: '#FFFFFF', borderRadius: '12px', border: '1px solid #EEEEF2', fontSize: '12px' }}
                />
                <Area type="monotone" dataKey="income" stroke="#65A982" strokeWidth={2} fillOpacity={1} fill="url(#colorIncome)" />
                <Area type="monotone" dataKey="expense" stroke="#9B87F5" strokeWidth={2} fillOpacity={1} fill="url(#colorExpense)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Expense Category Breakdown (1 col) */}
        <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
          <div className="mb-4">
            <h3 className="text-sm font-extrabold text-finova-text">Category Breakdown</h3>
            <p className="text-xs text-finova-muted">Where your money went this period</p>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={
                    analytics?.expenseCategoryBreakdown?.length
                      ? analytics.expenseCategoryBreakdown
                      : [
                          { name: 'Food & Dining', value: 3500 },
                          { name: 'Transportation', value: 1200 },
                          { name: 'Education', value: 1800 },
                          { name: 'Entertainment', value: 1000 },
                          { name: 'Subscriptions', value: 800 },
                        ]
                  }
                  innerRadius={60}
                  outerRadius={85}
                  paddingAngle={3}
                  dataKey="value"
                >
                  {(analytics?.expenseCategoryBreakdown || [1, 2, 3, 4, 5]).map((_, index) => (
                    <Cell key={`cell-${index}`} fill={pieColors[index % pieColors.length]} />
                  ))}
                </Pie>
                <Tooltip
                  formatter={(val: any) => [formatCurrency(Number(val)), 'Amount']}
                  contentStyle={{ backgroundColor: '#FFFFFF', borderRadius: '12px', border: '1px solid #EEEEF2', fontSize: '12px' }}
                />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* 5. RECENT ACTIVITY & UPCOMING PAYMENTS & SAVINGS GOALS */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recent Transactions (2 cols) */}
        <div className="lg:col-span-2 p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-extrabold text-finova-text">Recent Outflows</h3>
              <p className="text-xs text-finova-muted">Your latest transactions across campus & lifestyle</p>
            </div>
            <Link to="/expenses" className="text-xs font-bold text-primary hover:text-primary-dark flex items-center gap-1">
              <span>View All</span>
              <ChevronRight className="w-3.5 h-3.5" />
            </Link>
          </div>

          <div className="divide-y divide-finova-border">
            {recentExpenses.length > 0 ? (
              recentExpenses.map((exp) => (
                <div key={exp.id} className="py-3.5 flex items-center justify-between">
                  <div className="flex items-center gap-3">
                    <div className="w-10 h-10 rounded-xl bg-secondary-bg flex items-center justify-center text-finova-text font-bold text-xs border border-finova-border">
                      {exp.categoryName?.[0] || 'E'}
                    </div>
                    <div>
                      <p className="text-xs font-bold text-finova-text">{exp.title}</p>
                      <span className="text-[11px] text-finova-muted">
                        {exp.categoryName} • {formatDate(exp.expenseDate || exp.date)} • {exp.paymentMethod}
                      </span>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="text-xs font-extrabold text-finova-danger">
                      -{formatCurrency(exp.amount)}
                    </p>
                  </div>
                </div>
              ))
            ) : (
              <div className="py-8 text-center text-xs text-finova-muted">
                No recent transactions recorded. Add an expense to get started.
              </div>
            )}
          </div>
        </div>

        {/* Active Savings Goals Preview (1 col) */}
        <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div>
                <h3 className="text-sm font-extrabold text-finova-text">Savings Vaults</h3>
                <p className="text-xs text-finova-muted">Milestones you are working toward</p>
              </div>
              <Link to="/savings" className="text-xs font-bold text-primary hover:text-primary-dark">
                Manage
              </Link>
            </div>

            <div className="space-y-4">
              {savingsGoals.length > 0 ? (
                savingsGoals.map((goal) => (
                  <div key={goal.id} className="p-3.5 rounded-xl bg-secondary-bg border border-finova-border">
                    <div className="flex items-center justify-between mb-1.5">
                      <span className="text-xs font-bold text-finova-text">{goal.goalName || goal.name}</span>
                      <span className="text-xs font-bold text-primary">
                        {formatCurrency(goal.currentAmount)} / {formatCurrency(goal.targetAmount)}
                      </span>
                    </div>
                    {/* Progress Bar */}
                    <div className="w-full h-2 rounded-full bg-white border border-finova-border overflow-hidden">
                      <div
                        className="h-full bg-primary rounded-full transition-all duration-300"
                        style={{
                          width: `${Math.min(100, (goal.currentAmount / goal.targetAmount) * 100)}%`,
                        }}
                      />
                    </div>
                    <span className="text-[10px] text-finova-muted mt-1.5 block">
                      {Math.round((goal.currentAmount / goal.targetAmount) * 100)}% reached • Target: {formatDate(goal.targetDate)}
                    </span>
                  </div>
                ))
              ) : (
                <div className="p-6 rounded-xl bg-secondary-bg border border-finova-border text-center">
                  <PiggyBank className="w-8 h-8 text-primary mx-auto mb-2 opacity-60" />
                  <p className="text-xs font-bold text-finova-text">No active goals yet</p>
                  <p className="text-[11px] text-finova-muted mt-0.5">Start saving for your next tech upgrade or trip.</p>
                  <Link
                    to="/savings"
                    className="mt-3 inline-flex items-center gap-1.5 text-xs font-bold text-primary hover:underline"
                  >
                    <Plus className="w-3.5 h-3.5" /> Create Goal
                  </Link>
                </div>
              )}
            </div>
          </div>

          {/* AI Quick Insight Box */}
          <div className="mt-6 p-4 rounded-xl bg-lavender-50 border border-lavender-200">
            <div className="flex items-center gap-2 text-xs font-bold text-primary-dark mb-1">
              <Sparkles className="w-3.5 h-3.5 text-primary" />
              <span>FINOVA AI Insight</span>
            </div>
            <p className="text-xs text-finova-text leading-relaxed">
              {aiInsights?.topSavingOpportunities?.[0] ||
                aiInsights?.financialTips?.[0] ||
                'Tip: Cook meals on weekdays with roommates to save an estimated ₹2,400 monthly on food delivery.'}
            </p>
            <Link
              to="/ai"
              className="mt-2.5 inline-flex items-center gap-1 text-[11px] font-bold text-primary hover:text-primary-dark"
            >
              <span>Ask FINOVA AI</span>
              <ChevronRight className="w-3 h-3" />
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
