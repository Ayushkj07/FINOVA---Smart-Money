import React from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Sidebar } from '../components/common/Sidebar';
import { Navbar } from '../components/common/Navbar';
import { useRealtimeSync } from '../hooks/useRealtimeSync';

export const AppLayout: React.FC = () => {
  const location = useLocation();
  useRealtimeSync();

  const getPageTitle = (pathname: string) => {
    if (pathname.includes('/dashboard')) return 'Student Financial Overview';
    if (pathname.includes('/income')) return 'Income & Pocket Money';
    if (pathname.includes('/expenses')) return 'Expense & Outflow Tracking';
    if (pathname.includes('/budgets')) return 'Smart Budget Planner';
    if (pathname.includes('/savings')) return 'Savings Goals & Vaults';
    if (pathname.includes('/debts')) return 'Peer Debts & Borrowings';
    if (pathname.includes('/subscriptions')) return 'Subscription Manager';
    if (pathname.includes('/ai')) return 'FINOVA AI Financial Assistant';
    if (pathname.includes('/analytics')) return 'Financial Analytics & Reports';
    if (pathname.includes('/advanced')) return 'Advanced Financial Tools';
    if (pathname.includes('/admin')) return 'Administrator Command Center';
    if (pathname.includes('/profile')) return 'Account & Preferences';
    return 'Financial Dashboard';
  };

  return (
    <div className="flex min-h-screen bg-[#FAFAFC]">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <Navbar title={getPageTitle(location.pathname)} />
        <main className="flex-1 p-8 overflow-y-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
