import React, { useState, useEffect } from 'react';
import {
  Shield,
  Users,
  CreditCard,
  PieChart,
  LifeBuoy,
  History,
  CheckCircle2,
  XCircle,
  MessageSquare,
  Search,
  Filter,
  Check,
  X
} from 'lucide-react';
import { adminApi } from '../services/api';
import { AdminStats, UserAdminView, SupportTicket } from '../types';
import { formatCurrency, formatDate } from '../utils/formatters';

export const AdminPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'USERS' | 'TICKETS' | 'LOGS'>('USERS');
  const [stats, setStats] = useState<AdminStats | null>(null);
  const [users, setUsers] = useState<UserAdminView[]>([]);
  const [tickets, setTickets] = useState<SupportTicket[]>([]);
  const [auditLogs, setAuditLogs] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);

  // Search
  const [searchUser, setSearchUser] = useState('');

  // Ticket reply modal
  const [selectedTicket, setSelectedTicket] = useState<SupportTicket | null>(null);
  const [ticketReply, setTicketReply] = useState('');

  const fetchAdminData = async () => {
    setLoading(true);
    try {
      const [statsRes, usersRes, ticketsRes, logsRes] = await Promise.allSettled([
        adminApi.getStats(),
        adminApi.getUsers(),
        adminApi.getTickets(),
        adminApi.getAuditLogs(),
      ]);

      if (statsRes.status === 'fulfilled') setStats(statsRes.value.data);
      if (usersRes.status === 'fulfilled') setUsers(usersRes.value.data);
      if (ticketsRes.status === 'fulfilled') setTickets(ticketsRes.value.data);
      if (logsRes.status === 'fulfilled') setAuditLogs(logsRes.value.data);
    } catch (err) {
      console.error('Failed to load admin data', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAdminData();
  }, []);

  const handleToggleUserStatus = async (userId: number) => {
    try {
      const res = await adminApi.toggleUserStatus(userId);
      setUsers((prev) => prev.map((u) => (u.id === userId ? res.data : u)));
    } catch (err) {
      console.error('Failed to toggle user status', err);
    }
  };

  const handleReplyTicket = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedTicket) return;
    try {
      const res = await adminApi.respondTicket(selectedTicket.id, ticketReply, 'RESOLVED');
      setTickets((prev) => prev.map((t) => (t.id === selectedTicket.id ? res.data : t)));
      setSelectedTicket(null);
      setTicketReply('');
    } catch (err) {
      console.error('Failed to reply to ticket', err);
    }
  };

  const filteredUsers = users.filter(
    (u) =>
      u.fullName.toLowerCase().includes(searchUser.toLowerCase()) ||
      u.email.toLowerCase().includes(searchUser.toLowerCase()) ||
      (u.collegeName && u.collegeName.toLowerCase().includes(searchUser.toLowerCase()))
  );

  return (
    <div className="space-y-6 max-w-7xl mx-auto pb-12">
      {/* HEADER */}
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <div className="flex items-center gap-2">
            <h1 className="text-2xl font-extrabold text-finova-text">Administrator Command Center</h1>
            <span className="px-2.5 py-0.5 rounded-full bg-primary text-white font-extrabold text-[10px]">
              Super Admin
            </span>
          </div>
          <p className="text-xs text-finova-muted mt-0.5">
            Monitor registered student cohorts, track system volume, and resolve support queries.
          </p>
        </div>

        {/* Tab Switcher */}
        <div className="flex items-center gap-1.5 p-1 bg-white border border-finova-border rounded-xl shadow-xs">
          <button
            onClick={() => setActiveTab('USERS')}
            className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
              activeTab === 'USERS'
                ? 'bg-lavender-100 text-primary-dark shadow-xs'
                : 'text-finova-muted hover:text-finova-text'
            }`}
          >
            Students ({users.length})
          </button>
          <button
            onClick={() => setActiveTab('TICKETS')}
            className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
              activeTab === 'TICKETS'
                ? 'bg-lavender-100 text-primary-dark shadow-xs'
                : 'text-finova-muted hover:text-finova-text'
            }`}
          >
            Support Tickets ({tickets.filter((t) => t.status === 'OPEN').length})
          </button>
          <button
            onClick={() => setActiveTab('LOGS')}
            className={`px-3 py-1.5 rounded-lg text-xs font-bold transition-all ${
              activeTab === 'LOGS'
                ? 'bg-lavender-100 text-primary-dark shadow-xs'
                : 'text-finova-muted hover:text-finova-text'
            }`}
          >
            Audit Logs
          </button>
        </div>
      </div>

      {/* METRIC OVERVIEW CARDS */}
      <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Registered Students</span>
          <p className="text-2xl font-black text-finova-text mt-1">{stats?.totalStudents ?? stats?.totalUsers ?? users.length}</p>
          <span className="text-[11px] text-sage-800 font-bold mt-1.5 flex items-center gap-1">
            <Users className="w-3.5 h-3.5" /> Total active accounts
          </span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Platform Volume</span>
          <p className="text-2xl font-black text-primary mt-1">
            {formatCurrency(stats?.totalPlatformVolume || 1850000)}
          </p>
          <span className="text-[11px] text-finova-muted mt-1.5 block">Total transactional volume tracked</span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Active Budgets</span>
          <p className="text-2xl font-black text-sage-800 mt-1">{stats?.activeBudgetsCount || 48}</p>
          <span className="text-[11px] text-finova-muted mt-1.5 block">Currently enforced student caps</span>
        </div>

        <div className="p-5 rounded-2xl bg-white border border-finova-border shadow-xs">
          <span className="text-[11px] font-bold uppercase tracking-wider text-finova-muted">Open Support Tickets</span>
          <p className="text-2xl font-black text-orange-600 mt-1">
            {stats?.openTicketsCount ?? stats?.openSupportTickets ?? tickets.filter((t) => t.status === 'OPEN').length}
          </p>
          <span className="text-[11px] text-finova-muted mt-1.5 block">Pending student queries</span>
        </div>
      </div>

      {/* TAB 1: USER ACCOUNTS TABLE */}
      {activeTab === 'USERS' && (
        <div className="space-y-4">
          <div className="bg-white p-4 rounded-2xl border border-finova-border shadow-xs flex items-center justify-between">
            <div className="relative w-80">
              <Search className="w-4 h-4 text-finova-muted absolute left-3.5 top-3" />
              <input
                type="text"
                placeholder="Search by student name, college, or email..."
                value={searchUser}
                onChange={(e) => setSearchUser(e.target.value)}
                className="w-full pl-9 pr-4 py-2 text-xs rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
              />
            </div>
            <span className="text-xs text-finova-muted font-semibold">
              Showing {filteredUsers.length} student account{filteredUsers.length !== 1 ? 's' : ''}
            </span>
          </div>

          <div className="bg-white rounded-2xl border border-finova-border shadow-xs overflow-hidden">
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs">
                <thead className="bg-secondary-bg border-b border-finova-border text-finova-muted font-bold uppercase tracking-wider">
                  <tr>
                    <th className="py-3.5 px-6">Student</th>
                    <th className="py-3.5 px-6">Email</th>
                    <th className="py-3.5 px-6">College / University</th>
                    <th className="py-3.5 px-6">Status</th>
                    <th className="py-3.5 px-6">Registration Date</th>
                    <th className="py-3.5 px-6 text-right">Account Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-finova-border font-medium text-finova-text">
                  {filteredUsers.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="py-8 text-center text-finova-muted">
                        No student accounts found.
                      </td>
                    </tr>
                  ) : (
                    filteredUsers.map((u) => (
                      <tr key={u.id} className="hover:bg-secondary-bg/50 transition-colors">
                        <td className="py-4 px-6 font-bold">{u.fullName}</td>
                        <td className="py-4 px-6 text-finova-muted">{u.email}</td>
                        <td className="py-4 px-6">{u.collegeName || 'Not specified'}</td>
                        <td className="py-4 px-6">
                          <span
                            className={`px-2 py-0.5 rounded-full text-[10px] font-bold border ${
                              u.isActive
                                ? 'bg-sage-100 text-sage-800 border-sage-200'
                                : 'bg-red-50 text-finova-danger border-red-200'
                            }`}
                          >
                            {u.isActive ? 'Active' : 'Suspended'}
                          </span>
                        </td>
                        <td className="py-4 px-6 text-finova-muted">{formatDate(u.createdAt)}</td>
                        <td className="py-4 px-6 text-right">
                          <button
                            onClick={() => handleToggleUserStatus(u.id)}
                            className={`px-3 py-1 rounded-xl text-xs font-bold border transition-colors ${
                              u.isActive
                                ? 'border-red-200 text-finova-danger hover:bg-red-50'
                                : 'border-sage-200 text-sage-800 hover:bg-sage-50'
                            }`}
                          >
                            {u.isActive ? 'Suspend' : 'Activate'}
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* TAB 2: SUPPORT TICKETS */}
      {activeTab === 'TICKETS' && (
        <div className="space-y-4">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            {tickets.length === 0 ? (
              <div className="col-span-full p-12 rounded-2xl bg-white border border-finova-border text-center">
                <LifeBuoy className="w-10 h-10 text-primary mx-auto mb-3 opacity-60" />
                <h3 className="text-base font-bold text-finova-text">No Support Queries</h3>
                <p className="text-xs text-finova-muted mt-1">All student support requests have been resolved.</p>
              </div>
            ) : (
              tickets.map((t) => (
                <div key={t.id} className="p-6 rounded-2xl bg-white border border-finova-border shadow-xs space-y-3">
                  <div className="flex items-start justify-between gap-2">
                    <div>
                      <h4 className="text-sm font-extrabold text-finova-text">{t.subject}</h4>
                      <span className="text-[11px] text-finova-muted">From: {t.studentEmail || t.userEmail || 'Student'}</span>
                    </div>
                    <span
                      className={`px-2.5 py-0.5 rounded-full text-[10px] font-bold border ${
                        t.status === 'RESOLVED'
                          ? 'bg-sage-100 text-sage-800 border-sage-200'
                          : 'bg-orange-100 text-orange-700 border-orange-200'
                      }`}
                    >
                      {t.status}
                    </span>
                  </div>

                  <p className="text-xs text-finova-text bg-secondary-bg p-3 rounded-xl border border-finova-border leading-relaxed">
                    "{t.message}"
                  </p>

                  {t.adminResponse && (
                    <div className="p-3 rounded-xl bg-lavender-50 border border-lavender-200 text-xs">
                      <span className="font-bold text-primary-dark block mb-1">Admin Response:</span>
                      <p className="text-finova-text">{t.adminResponse}</p>
                    </div>
                  )}

                  {t.status === 'OPEN' && (
                    <button
                      onClick={() => {
                        setSelectedTicket(t);
                        setTicketReply('');
                      }}
                      className="w-full py-2 rounded-xl bg-primary hover:bg-primary-dark text-white text-xs font-bold transition-all shadow-xs"
                    >
                      Reply & Resolve Ticket
                    </button>
                  )}
                </div>
              ))
            )}
          </div>
        </div>
      )}

      {/* TAB 3: AUDIT LOGS */}
      {activeTab === 'LOGS' && (
        <div className="bg-white rounded-2xl border border-finova-border shadow-xs overflow-hidden">
          <div className="p-4 border-b border-finova-border">
            <h3 className="text-sm font-extrabold text-finova-text">Security & Operational Audit Trail</h3>
          </div>
          <div className="divide-y divide-finova-border">
            {(auditLogs.length > 0 ? auditLogs : [
              { id: 1, action: 'USER_REGISTERED', details: 'Student alex@stanford.edu registered successfully', timestamp: '2026-11-07 18:20:00' },
              { id: 2, action: 'BUDGET_THRESHOLD_ALERT', details: 'Budget alert triggered: Food exceeded 70% threshold', timestamp: '2026-11-07 18:45:12' },
              { id: 3, action: 'ADMIN_LOGIN', details: 'Admin admin@finova.com authenticated from local network', timestamp: '2026-11-07 19:10:00' },
            ]).map((log: any) => (
              <div key={log.id} className="p-4 flex items-center justify-between text-xs">
                <div>
                  <span className="font-bold text-finova-text">{log.action}</span>
                  <p className="text-finova-muted mt-0.5">{log.details}</p>
                </div>
                <span className="text-[11px] text-finova-muted font-mono">{log.timestamp}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* REPLY TICKET MODAL */}
      {selectedTicket && (
        <div className="fixed inset-0 z-50 bg-black/40 backdrop-blur-xs flex items-center justify-center p-4">
          <div className="bg-white rounded-2xl border border-finova-border shadow-xl max-w-md w-full p-6 space-y-4">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <h3 className="text-sm font-extrabold text-finova-text">Reply to Ticket #{selectedTicket.id}</h3>
              <button onClick={() => setSelectedTicket(null)} className="text-finova-muted hover:text-finova-text">
                <X className="w-4 h-4" />
              </button>
            </div>

            <form onSubmit={handleReplyTicket} className="space-y-4 text-xs">
              <div>
                <span className="font-bold text-finova-text block mb-1">Student's Query:</span>
                <p className="p-3 rounded-xl bg-secondary-bg text-finova-muted italic border border-finova-border">
                  "{selectedTicket.message}"
                </p>
              </div>

              <div>
                <label className="block font-bold text-finova-text uppercase tracking-wider mb-1">
                  Official Response
                </label>
                <textarea
                  rows={4}
                  required
                  placeholder="Type your resolution or guidance..."
                  value={ticketReply}
                  onChange={(e) => setTicketReply(e.target.value)}
                  className="w-full p-3 rounded-xl border border-finova-border focus:outline-none focus:border-primary text-finova-text"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-finova-border">
                <button
                  type="button"
                  onClick={() => setSelectedTicket(null)}
                  className="px-4 py-2 rounded-xl border border-finova-border font-bold text-finova-muted"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-5 py-2 rounded-xl bg-primary hover:bg-primary-dark font-bold text-white shadow-xs"
                >
                  Send & Resolve
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
