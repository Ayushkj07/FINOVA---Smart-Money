import React, { useState, useEffect } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Bell, Sparkles, User, LogOut, Settings, CheckCheck, ExternalLink, Blocks } from 'lucide-react';
import { useAuth } from '../../contexts/AuthContext';
import { notificationApi } from '../../services/api';
import { NotificationItem } from '../../types';

interface NavbarProps {
  title?: string;
}

export const Navbar: React.FC<NavbarProps> = ({ title = 'Financial Dashboard' }) => {
  const { user, logout, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState<number>(0);
  const [notifications, setNotifications] = useState<NotificationItem[]>([]);
  const [showNotifications, setShowNotifications] = useState<boolean>(false);
  const [showProfileMenu, setShowProfileMenu] = useState<boolean>(false);

  useEffect(() => {
    loadNotifications();
    const interval = setInterval(loadNotifications, 30000);
    return () => clearInterval(interval);
  }, []);

  const loadNotifications = async () => {
    try {
      const res = await notificationApi.getAll();
      setNotifications(res.data.slice(0, 5));
      const countRes = await notificationApi.getUnreadCount();
      setUnreadCount(countRes.data.unreadCount);
    } catch (e) {
      // ignore
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationApi.markAllRead();
      setUnreadCount(0);
      setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
    } catch (e) {
      // ignore
    }
  };

  return (
    <header className="sticky top-0 z-30 flex items-center justify-between h-20 px-8 bg-white/90 backdrop-blur-md border-b border-finova-border">
      {/* Title & Greeting */}
      <div>
        <h1 className="text-xl font-bold text-finova-text tracking-tight">{title}</h1>
        <p className="text-xs text-finova-muted">
          Welcome back, <span className="font-semibold text-primary">{user?.fullName || 'Student'}</span>
          {user?.collegeName && <span className="hidden sm:inline"> • {user.collegeName}</span>}
        </p>
      </div>

      {/* Right Controls */}
      <div className="flex items-center gap-4">
        {/* FINOVA AI Assistant Quick Pill */}
        <button
          onClick={() => navigate('/ai')}
          className="hidden md:inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full text-xs font-semibold bg-lavender-100 text-primary-dark border border-lavender-200 hover:bg-lavender-200 transition-all duration-150 shadow-sm"
        >
          <Sparkles className="w-3.5 h-3.5 text-primary" />
          <span>Ask FINOVA AI</span>
        </button>

        {/* Notifications Center Popover */}
        <div className="relative">
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="relative p-2.5 rounded-xl border border-finova-border text-finova-text hover:bg-secondary-bg hover:border-lavender-200 transition-all"
            title="Notifications"
          >
            <Bell className="w-4 h-4" />
            {unreadCount > 0 && (
              <span className="absolute -top-1 -right-1 flex items-center justify-center w-5 h-5 text-[10px] font-bold text-white bg-primary rounded-full shadow-sm animate-pulse">
                {unreadCount > 9 ? '9+' : unreadCount}
              </span>
            )}
          </button>

          {showNotifications && (
            <div className="absolute right-0 mt-3 w-80 md:w-96 bg-white border border-finova-border rounded-card shadow-xl p-4 z-50 animate-in fade-in slide-in-from-top-2">
              <div className="flex items-center justify-between pb-3 border-b border-finova-border">
                <div className="flex items-center gap-2">
                  <h3 className="text-sm font-bold text-finova-text">Notifications</h3>
                  {unreadCount > 0 && (
                    <span className="px-2 py-0.5 text-[10px] font-bold rounded-full bg-lavender-100 text-primary">
                      {unreadCount} new
                    </span>
                  )}
                </div>
                {unreadCount > 0 && (
                  <button
                    onClick={handleMarkAllRead}
                    className="text-xs text-primary hover:underline flex items-center gap-1 font-medium"
                  >
                    <CheckCheck className="w-3.5 h-3.5" /> Mark all read
                  </button>
                )}
              </div>

              <div className="divide-y divide-finova-border max-h-80 overflow-y-auto my-2">
                {notifications.length === 0 ? (
                  <div className="py-8 text-center text-xs text-finova-muted">
                    No new notifications right now.
                  </div>
                ) : (
                  notifications.map((n) => (
                    <div
                      key={n.id}
                      className={`py-3 px-2 rounded-lg text-xs transition-colors ${
                        !n.isRead ? 'bg-lavender-50/60' : 'hover:bg-secondary-bg'
                      }`}
                    >
                      <div className="flex items-start justify-between gap-2">
                        <span className="font-semibold text-finova-text">{n.title}</span>
                        {!n.isRead && (
                          <span className="w-2 h-2 rounded-full bg-primary flex-shrink-0 mt-1" />
                        )}
                      </div>
                      <p className="text-finova-muted mt-1 leading-relaxed">{n.message}</p>
                      {n.actionUrl && (
                        <Link
                          to={n.actionUrl}
                          onClick={() => setShowNotifications(false)}
                          className="mt-2 text-primary font-medium inline-flex items-center gap-1 hover:underline text-[11px]"
                        >
                          View details <ExternalLink className="w-3 h-3" />
                        </Link>
                      )}
                    </div>
                  ))
                )}
              </div>
            </div>
          )}
        </div>

        {/* User Profile Menu */}
        <div className="relative">
          <button
            onClick={() => setShowProfileMenu(!showProfileMenu)}
            className="flex items-center gap-3 p-1.5 pl-2.5 rounded-xl border border-finova-border hover:bg-secondary-bg hover:border-lavender-200 transition-all"
          >
            <div className="flex flex-col text-right">
              <span className="text-xs font-bold text-finova-text leading-tight">
                {user?.fullName || 'Alex Morgan'}
              </span>
              <span className="text-[10px] text-finova-muted font-medium">
                {isAdmin ? 'Administrator' : 'Student Account'}
              </span>
            </div>
            <div className="w-8 h-8 rounded-full bg-lavender-200 text-primary-dark font-bold flex items-center justify-center text-xs border border-lavender-300">
              {user?.fullName ? user.fullName.charAt(0).toUpperCase() : 'A'}
            </div>
          </button>

          {showProfileMenu && (
            <div className="absolute right-0 mt-3 w-56 bg-white border border-finova-border rounded-card shadow-xl p-2 z-50">
              <div className="px-3 py-2 border-b border-finova-border">
                <p className="text-xs font-bold text-finova-text truncate">{user?.fullName}</p>
                <p className="text-[11px] text-finova-muted truncate">{user?.email}</p>
              </div>

              <div className="py-1">
                <button
                  onClick={() => {
                    navigate('/profile');
                    setShowProfileMenu(false);
                  }}
                  className="w-full flex items-center gap-2.5 px-3 py-2 text-xs text-finova-text hover:bg-lavender-50 rounded-lg transition-colors font-medium"
                >
                  <Settings className="w-4 h-4 text-finova-muted" /> Profile & Settings
                </button>

                <button
                  onClick={() => {
                    navigate('/settings/connected-apps');
                    setShowProfileMenu(false);
                  }}
                  className="w-full flex items-center gap-2.5 px-3 py-2 text-xs text-finova-text hover:bg-lavender-50 rounded-lg transition-colors font-medium"
                >
                  <Blocks className="w-4 h-4 text-primary" /> Connected Apps
                </button>

                {isAdmin && (
                  <button
                    onClick={() => {
                      navigate('/admin');
                      setShowProfileMenu(false);
                    }}
                    className="w-full flex items-center gap-2.5 px-3 py-2 text-xs text-primary font-semibold hover:bg-lavender-50 rounded-lg transition-colors"
                  >
                    <User className="w-4 h-4 text-primary" /> Admin Portal
                  </button>
                )}
              </div>

              <div className="pt-1 border-t border-finova-border">
                <button
                  onClick={() => {
                    logout();
                    navigate('/login');
                  }}
                  className="w-full flex items-center gap-2.5 px-3 py-2 text-xs text-finova-danger hover:bg-red-50 rounded-lg transition-colors font-semibold"
                >
                  <LogOut className="w-4 h-4" /> Sign Out
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
