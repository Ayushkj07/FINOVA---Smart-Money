import React, { useState, useEffect } from 'react';
import {
  BarChart3,
  Download,
  Calendar,
  FileSpreadsheet,
  FileText,
  Table,
  TrendingUp,
  PieChart as PieIcon,
  CreditCard,
  ArrowDownLeft,
  ArrowUpRight
} from 'lucide-react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  LineChart,
  Line,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend
} from 'recharts';
import { dashboardApi, reportsApi } from '../services/api';
import { DashboardAnalytics, DashboardSummary } from '../types';
import { formatCurrency } from '../utils/formatters';

export const AnalyticsPage: React.FC = () => {
  const [period, setPeriod] = useState('MONTHLY');
  const [loading, setLoading] = useState(true);
  const [analytics, setAnalytics] = useState<DashboardAnalytics | null>(null);
  const [summary, setSummary] = useState<DashboardSummary | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      try {
        const [anaRes, sumRes] = await Promise.all([
          dashboardApi.getAnalytics(period === 'MONTHLY' ? 'THIS_MONTH' : 'THIS_YEAR'),
          dashboardApi.getSummary(period === 'MONTHLY' ? 'THIS_MONTH' : 'THIS_YEAR'),
        ]);
        setAnalytics(anaRes.data);
        setSummary(sumRes.data);
      } catch (err) {
        console.error('Failed to load analytics', err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [period]);

  const handleExportCsv = () => {
    window.open(reportsApi.exportCsvUrl(), '_blank');
  };

  const handleExportExcel = () => {
    window.open(reportsApi.exportExcelUrl(period), '_blank');
  };

  const handleExportPdf = () => {
    window.open(reportsApi.exportPdfUrl(period), '_blank');
  };

  const pieColors = ['#9B87F5', '#A8C9B5', '#E8B96B', '#DF858B', '#7E69AB', '#65A982', '#94A3B8'];

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-12">
      {/* HEADER & EXPORT ACTIONS */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-extrabold text-finova-text">Financial Analytics & Reports</h1>
          <p className="text-xs text-finova-muted mt-0.5">
            Deep-dive into income vs. expenses, day-wise spending distribution, and export statements.
          </p>
        </div>

        {/* Period Selector & Download Buttons */}
        <div className="flex flex-wrap items-center gap-2">
          <div className="flex items-center gap-1 p-1 bg-white border border-finova-border rounded-xl shadow-xs">
            <button
              onClick={() => setPeriod('MONTHLY')}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                period === 'MONTHLY'
                  ? 'bg-lavender-100 text-primary-dark shadow-xs'
                  : 'text-finova-muted hover:text-finova-text'
              }`}
            >
              This Month
            </button>
            <button
              onClick={() => setPeriod('ANNUAL')}
              className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
                period === 'ANNUAL'
                  ? 'bg-lavender-100 text-primary-dark shadow-xs'
                  : 'text-finova-muted hover:text-finova-text'
              }`}
            >
              This Year
            </button>
          </div>

          <button
            onClick={handleExportCsv}
            className="inline-flex items-center gap-1.5 px-3 py-2 rounded-xl bg-white hover:bg-secondary-bg border border-finova-border text-xs font-bold text-finova-text transition-colors shadow-xs"
          >
            <Table className="w-3.5 h-3.5 text-sage-700" />
            <span>CSV</span>
          </button>

          <button
            onClick={handleExportExcel}
            className="inline-flex items-center gap-1.5 px-3 py-2 rounded-xl bg-white hover:bg-secondary-bg border border-finova-border text-xs font-bold text-finova-text transition-colors shadow-xs"
          >
            <FileSpreadsheet className="w-3.5 h-3.5 text-sage-800" />
            <span>Excel (.xlsx)</span>
          </button>

          <button
            onClick={handleExportPdf}
            className="inline-flex items-center gap-1.5 px-3 py-2 rounded-xl bg-primary hover:bg-primary-dark text-white text-xs font-bold transition-all shadow-xs"
          >
            <FileText className="w-3.5 h-3.5" />
            <span>PDF Statement</span>
          </button>
        </div>
      </div>

      {/* METRIC BANNER */}
      <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Total Inflows</span>
          <p className="text-2xl font-black text-sage-800 mt-1">
            {formatCurrency(summary?.monthlyIncome)}
          </p>
          <span className="text-[11px] text-finova-muted mt-1 block">Total allowances & stipends</span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Total Outflows</span>
          <p className="text-2xl font-black text-finova-danger mt-1">
            {formatCurrency(summary?.monthlyExpenses)}
          </p>
          <span className="text-[11px] text-finova-muted mt-1 block">Total purchases & payments</span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Net Surplus / Deficit</span>
          <p className="text-2xl font-black text-primary mt-1">
            {formatCurrency((summary?.monthlyIncome || 0) - (summary?.monthlyExpenses || 0))}
          </p>
          <span className="text-[11px] text-finova-muted mt-1 block">Net balance generated</span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Savings Rate</span>
          <p className="text-2xl font-black text-sage-800 mt-1">{analytics?.savingsRate || 22}%</p>
          <span className="text-[11px] text-finova-muted mt-1 block">Portion of income retained</span>
        </div>
      </div>

      {/* CHARTS ROW 1 */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Monthly Spending Trend Bar Chart */}
        <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
          <div className="mb-4">
            <h3 className="text-sm font-extrabold text-finova-text">Periodic Spending Outflows</h3>
            <p className="text-xs text-finova-muted">Distribution across consecutive weeks / intervals</p>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart
                data={analytics?.monthlySpendingTrend || [
                  { period: 'Week 1', amount: 2400 },
                  { period: 'Week 2', amount: 3100 },
                  { period: 'Week 3', amount: 1950 },
                  { period: 'Week 4', amount: 2800 },
                ]}
                margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
              >
                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#EEEEF2" />
                <XAxis dataKey="period" tick={{ fontSize: 11, fill: '#858392' }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fontSize: 11, fill: '#858392' }} axisLine={false} tickLine={false} />
                <Tooltip
                  formatter={(val: any) => [formatCurrency(Number(val)), 'Spent']}
                  contentStyle={{ backgroundColor: '#FFFFFF', borderRadius: '12px', border: '1px solid #EEEEF2', fontSize: '12px' }}
                />
                <Bar dataKey="amount" fill="#9B87F5" radius={[6, 6, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Expense Category Pie Breakdown */}
        <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
          <div className="mb-4">
            <h3 className="text-sm font-extrabold text-finova-text">Category Outflow Proportion</h3>
            <p className="text-xs text-finova-muted">Visualizing lifestyle vs. essential student spending</p>
          </div>
          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie
                  data={
                    analytics?.expenseCategoryBreakdown?.length
                      ? analytics.expenseCategoryBreakdown
                      : [
                          { name: 'Food', value: 4500 },
                          { name: 'Transportation', value: 1500 },
                          { name: 'Education', value: 2000 },
                          { name: 'Shopping', value: 1200 },
                        ]
                  }
                  innerRadius={55}
                  outerRadius={80}
                  paddingAngle={3}
                  dataKey="value"
                >
                  {(analytics?.expenseCategoryBreakdown || [1, 2, 3, 4]).map((_, index) => (
                    <Cell key={`cell-${index}`} fill={pieColors[index % pieColors.length]} />
                  ))}
                </Pie>
                <Tooltip
                  formatter={(val: any) => [formatCurrency(Number(val)), 'Amount']}
                  contentStyle={{ backgroundColor: '#FFFFFF', borderRadius: '12px', border: '1px solid #EEEEF2', fontSize: '12px' }}
                />
                <Legend iconType="circle" wrapperStyle={{ fontSize: '11px', paddingTop: '8px' }} />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* CHARTS ROW 2: SAVINGS GROWTH & INFLOW VS OUTFLOW */}
      <div className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs">
        <div className="mb-4">
          <h3 className="text-sm font-extrabold text-finova-text">Cumulative Savings & Inflow Trajectory</h3>
          <p className="text-xs text-finova-muted">Progress across all active student goals and vaults</p>
        </div>
        <div className="h-64 w-full">
          <ResponsiveContainer width="100%" height="100%">
            <LineChart
              data={analytics?.savingsGrowthTrend || [
                { label: 'Jan', savings: 3000, income: 15000, expense: 9000 },
                { label: 'Feb', savings: 6500, income: 16000, expense: 9500 },
                { label: 'Mar', savings: 10200, income: 17500, expense: 11000 },
                { label: 'Apr', savings: 14750, income: 18000, expense: 10500 },
              ]}
              margin={{ top: 10, right: 10, left: -20, bottom: 0 }}
            >
              <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#EEEEF2" />
              <XAxis dataKey="label" tick={{ fontSize: 11, fill: '#858392' }} axisLine={false} tickLine={false} />
              <YAxis tick={{ fontSize: 11, fill: '#858392' }} axisLine={false} tickLine={false} />
              <Tooltip
                formatter={(val: any) => [formatCurrency(Number(val)), '']}
                contentStyle={{ backgroundColor: '#FFFFFF', borderRadius: '12px', border: '1px solid #EEEEF2', fontSize: '12px' }}
              />
              <Legend iconType="circle" wrapperStyle={{ fontSize: '11px' }} />
              <Line type="monotone" dataKey="savings" stroke="#9B87F5" strokeWidth={3} dot={{ r: 4 }} name="Total Savings Stash" />
              <Line type="monotone" dataKey="income" stroke="#65A982" strokeWidth={2} strokeDasharray="4 4" name="Monthly Inflows" />
              <Line type="monotone" dataKey="expense" stroke="#DF858B" strokeWidth={2} strokeDasharray="4 4" name="Monthly Expenses" />
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>
    </div>
  );
};
