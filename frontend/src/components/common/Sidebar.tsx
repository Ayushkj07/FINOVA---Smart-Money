import React from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import {
  LayoutDashboard,
  ArrowDownLeft,
  ArrowUpRight,
  PieChart,
  PiggyBank,
  HandCoins,
  Repeat,
  Sparkles,
  BarChart3,
  Layers,
  Shield,
  User,
  LogOut,
  Blocks,
} from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';

interface SidebarProps {
  isOpen?: boolean;
}

export const Sidebar: React.FC<SidebarProps> = () => {
  const { logout, isAdmin } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const navItemClass = ({ isActive }: { isActive: boolean }) =>
    `flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-semibold transition-all duration-150 ${
      isActive
        ? 'bg-lavender-100 text-primary-dark shadow-sm border border-lavender-200'
        : 'text-finova-muted hover:text-finova-text hover:bg-secondary-bg'
    }`;

  return (
    <aside className="w-64 flex-shrink-0 bg-white border-r border-finova-border flex flex-col justify-between min-h-screen p-5 select-none">
      {/* Brand Logo & Header */}
      <div>
        <div className="flex items-center gap-3 px-2 mb-8 cursor-pointer" onClick={() => navigate('/dashboard')}>
          <div className="w-10 h-10 rounded-2xl bg-primary flex items-center justify-center shadow-sm">
            <span className="text-white font-extrabold text-lg tracking-wider">F</span>
          </div>
          <div>
            <div className="flex items-center gap-1.5">
              <span className="font-extrabold text-base tracking-tight text-finova-text">FINOVA</span>
              <span className="text-[10px] font-bold px-1.5 py-0.5 rounded-md bg-sage-100 text-sage-700">v1.0</span>
            </div>
            <p className="text-[10px] text-finova-muted font-medium">Smart Money. Smarter Future.</p>
          </div>
        </div>

        {/* Navigation Section: Overview */}
        <div className="space-y-6">
          <div>
            <span className="px-3 text-[10px] font-bold tracking-wider uppercase text-finova-muted/80">
              Overview
            </span>
            <nav className="mt-2 space-y-1">
              <NavLink to="/dashboard" className={navItemClass}>
                <LayoutDashboard className="w-4 h-4" />
                <span>Dashboard</span>
              </NavLink>

              <NavLink to="/income" className={navItemClass}>
                <ArrowDownLeft className="w-4 h-4 text-finova-success" />
                <span>Income & Earnings</span>
              </NavLink>

              <NavLink to="/expenses" className={navItemClass}>
                <ArrowUpRight className="w-4 h-4 text-finova-danger" />
                <span>Expenses & Outflows</span>
              </NavLink>
            </nav>
          </div>

          {/* Navigation Section: Planning */}
          <div>
            <span className="px-3 text-[10px] font-bold tracking-wider uppercase text-finova-muted/80">
              Planning & Growth
            </span>
            <nav className="mt-2 space-y-1">
              <NavLink to="/budgets" className={navItemClass}>
                <PieChart className="w-4 h-4" />
                <span>Smart Budgets</span>
              </NavLink>

              <NavLink to="/savings" className={navItemClass}>
                <PiggyBank className="w-4 h-4" />
                <span>Savings Goals</span>
              </NavLink>

              <NavLink to="/debts" className={navItemClass}>
                <HandCoins className="w-4 h-4" />
                <span>Debts & Loans</span>
              </NavLink>

              <NavLink to="/subscriptions" className={navItemClass}>
                <Repeat className="w-4 h-4" />
                <span>Subscriptions</span>
              </NavLink>
            </nav>
          </div>

          {/* Navigation Section: Intelligence */}
          <div>
            <span className="px-3 text-[10px] font-bold tracking-wider uppercase text-finova-muted/80">
              Intelligence
            </span>
            <nav className="mt-2 space-y-1">
              <NavLink to="/ai" className={navItemClass}>
                <Sparkles className="w-4 h-4 text-primary" />
                <span className="flex-1">FINOVA AI</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded bg-primary text-white">AI</span>
              </NavLink>

              <NavLink to="/analytics" className={navItemClass}>
                <BarChart3 className="w-4 h-4" />
                <span>Analytics & Reports</span>
              </NavLink>

              <NavLink to="/advanced" className={navItemClass}>
                <Layers className="w-4 h-4" />
                <span>Advanced Features</span>
              </NavLink>

              <NavLink to="/settings/connected-apps" className={navItemClass}>
                <Blocks className="w-4 h-4 text-primary" />
                <span className="flex-1">Connected Apps</span>
                <span className="text-[9px] font-bold px-1.5 py-0.5 rounded bg-sage-100 text-sage-700">Google</span>
              </NavLink>
            </nav>
          </div>

          {/* Navigation Section: Admin if permitted */}
          {isAdmin && (
            <div>
              <span className="px-3 text-[10px] font-bold tracking-wider uppercase text-finova-muted/80">
                Administration
              </span>
              <nav className="mt-2 space-y-1">
                <NavLink to="/admin" className={navItemClass}>
                  <Shield className="w-4 h-4 text-primary" />
                  <span>Admin Portal</span>
                </NavLink>
              </nav>
            </div>
          )}
        </div>
      </div>

      {/* Sidebar Footer: Profile & Sign out */}
      <div className="pt-4 border-t border-finova-border space-y-1">
        <NavLink to="/profile" className={navItemClass}>
          <User className="w-4 h-4" />
          <span>My Profile</span>
        </NavLink>

        <button
          onClick={handleLogout}
          className="w-full flex items-center gap-3 px-3.5 py-2.5 rounded-xl text-xs font-semibold text-finova-danger hover:bg-red-50 transition-all duration-150"
        >
          <LogOut className="w-4 h-4" />
          <span>Sign Out</span>
        </button>
      </div>
    </aside>
  );
};
