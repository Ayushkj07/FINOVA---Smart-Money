import React, { useState, useEffect } from 'react';
import {
  Calendar,
  FileSpreadsheet,
  HardDrive,
  Mail,
  MapPin,
  CheckCircle2,
  AlertCircle,
  RefreshCw,
  ExternalLink,
  ShieldCheck,
  Unlink,
  Download,
  UploadCloud,
  Send,
  Lock,
  Clock,
  Sparkles,
  ChevronRight,
  Info,
  Trash2
} from 'lucide-react';
import { googleApi } from '../services/api';
import {
  GoogleIntegrationStatus,
  GoogleCalendarEvent,
  GoogleDriveFile,
  GoogleSyncLog,
  GoogleServiceStatus
} from '../types';

export const ConnectedAppsPage: React.FC = () => {
  const [status, setStatus] = useState<GoogleIntegrationStatus | null>(null);
  const [calendarEvents, setCalendarEvents] = useState<GoogleCalendarEvent[]>([]);
  const [driveFiles, setDriveFiles] = useState<GoogleDriveFile[]>([]);
  const [syncHistory, setSyncHistory] = useState<GoogleSyncLog[]>([]);

  const [loading, setLoading] = useState<boolean>(true);
  const [actionLoading, setActionLoading] = useState<string | null>(null);
  const [toastMessage, setToastMessage] = useState<{ text: string; type: 'success' | 'error' } | null>(null);
  const [showEventsModal, setShowEventsModal] = useState<boolean>(false);
  const [lastExportedSheetUrl, setLastExportedSheetUrl] = useState<string | null>(null);

  useEffect(() => {
    loadAllData();
  }, []);

  const showToast = (text: string, type: 'success' | 'error' = 'success') => {
    setToastMessage({ text, type });
    setTimeout(() => setToastMessage(null), 4000);
  };

  const loadAllData = async () => {
    try {
      setLoading(true);
      const [statusRes, calRes, driveRes, historyRes] = await Promise.allSettled([
        googleApi.getStatus(),
        googleApi.getCalendarEvents(),
        googleApi.getDriveFiles(),
        googleApi.getSyncHistory()
      ]);

      if (statusRes.status === 'fulfilled') setStatus(statusRes.value.data);
      if (calRes.status === 'fulfilled') setCalendarEvents(calRes.value.data);
      if (driveRes.status === 'fulfilled') setDriveFiles(driveRes.value.data);
      if (historyRes.status === 'fulfilled') setSyncHistory(historyRes.value.data);
    } catch (err) {
      console.error('Failed to load Google Integration data:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleConnectService = async (serviceName: string) => {
    try {
      setActionLoading(`connect-${serviceName}`);
      const res = await googleApi.getConnectUrl(serviceName);
      if (res.data?.authUrl) {
        window.location.href = res.data.authUrl;
      }
    } catch (err: any) {
      showToast(err.response?.data?.message || `Failed to initiate Google ${serviceName} connection`, 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleDisconnectService = async (serviceName: string) => {
    if (!window.confirm(`Are you sure you want to disconnect Google ${serviceName}?`)) return;
    try {
      setActionLoading(`disconnect-${serviceName}`);
      await googleApi.disconnect(serviceName);
      showToast(`Disconnected Google ${serviceName}.`);
      await loadAllData();
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Failed to disconnect service', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleRevokeAll = async () => {
    if (!window.confirm('Revoke all Google OAuth permissions? This will disconnect all services and remove stored access tokens.')) return;
    try {
      setActionLoading('revoke-all');
      await googleApi.revokeAll();
      showToast('All Google permissions successfully revoked.');
      await loadAllData();
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Failed to revoke permissions', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleSyncCalendar = async () => {
    try {
      setActionLoading('calendar-sync');
      const res = await googleApi.syncCalendar();
      showToast(res.data.message || `Synchronized ${res.data.syncedCount} events to Google Calendar!`);
      const eventsRes = await googleApi.getCalendarEvents();
      setCalendarEvents(eventsRes.data);
      const histRes = await googleApi.getSyncHistory();
      setSyncHistory(histRes.data);
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Calendar sync failed', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleExportSheets = async () => {
    try {
      setActionLoading('sheets-export');
      const res = await googleApi.exportSheets();
      setLastExportedSheetUrl(res.data.spreadsheetUrl);
      showToast('7-tab workbook exported to Google Sheets!');
      const histRes = await googleApi.getSyncHistory();
      setSyncHistory(histRes.data);
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Sheets export failed', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleUploadDrive = async (format: 'PDF' | 'EXCEL') => {
    try {
      setActionLoading(`drive-${format}`);
      const res = await googleApi.uploadDrive('MONTHLY_STATEMENT', format);
      showToast(`Statement successfully uploaded to Google Drive folder: ${res.data.folderPath}`);
      const driveRes = await googleApi.getDriveFiles();
      setDriveFiles(driveRes.data);
      const histRes = await googleApi.getSyncHistory();
      setSyncHistory(histRes.data);
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Drive backup failed', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const handleDeleteDriveFile = async (id: number) => {
    if (!window.confirm('Delete this file record?')) return;
    try {
      await googleApi.deleteDriveFile(id);
      setDriveFiles((prev) => prev.filter((f) => f.id !== id));
      showToast('File removed.');
    } catch (err: any) {
      showToast('Failed to delete file', 'error');
    }
  };

  const handleSendMonthlySummary = async () => {
    try {
      setActionLoading('gmail-summary');
      await googleApi.sendMonthlySummary();
      showToast('Monthly financial summary successfully dispatched via Gmail!');
      const histRes = await googleApi.getSyncHistory();
      setSyncHistory(histRes.data);
    } catch (err: any) {
      showToast(err.response?.data?.message || 'Gmail dispatch failed', 'error');
    } finally {
      setActionLoading(null);
    }
  };

  const getServiceStatus = (serviceName: string): GoogleServiceStatus | undefined => {
    return status?.services?.find((s) => s.serviceName.toUpperCase() === serviceName.toUpperCase());
  };

  return (
    <div className="space-y-8 max-w-7xl mx-auto pb-12">
      {/* Toast Notification Banner */}
      {toastMessage && (
        <div
          className={`fixed top-6 right-6 z-50 flex items-center gap-3 px-5 py-3.5 rounded-2xl shadow-lg border transition-all duration-200 ${
            toastMessage.type === 'success'
              ? 'bg-sage-50 border-sage-200 text-sage-800'
              : 'bg-red-50 border-red-200 text-red-800'
          }`}
        >
          {toastMessage.type === 'success' ? (
            <CheckCircle2 className="w-5 h-5 text-finova-success flex-shrink-0" />
          ) : (
            <AlertCircle className="w-5 h-5 text-finova-danger flex-shrink-0" />
          )}
          <span className="text-xs font-semibold">{toastMessage.text}</span>
        </div>
      )}

      {/* Header Section */}
      <div className="bg-white rounded-3xl p-8 border border-finova-border shadow-sm flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <div className="flex items-center gap-2 mb-2">
            <span className="text-xs font-bold px-2.5 py-1 rounded-lg bg-lavender-100 text-primary uppercase tracking-wider">
              Google Workspace Suite
            </span>
            <span className="text-xs font-bold px-2.5 py-1 rounded-lg bg-sage-100 text-sage-700">
              OAuth 2.0 & Cloud APIs
            </span>
          </div>
          <h1 className="text-2xl font-extrabold text-finova-text tracking-tight">Connected Google Applications</h1>
          <p className="text-xs text-finova-muted max-w-2xl mt-1">
            Seamlessly link your Google account for automated financial event calendar scheduling, multi-tab spreadsheet reports, secure Drive backups, and timely Gmail financial alerts.
          </p>
        </div>

        <div className="flex items-center gap-3 flex-wrap">
          <button
            onClick={loadAllData}
            disabled={loading}
            className="flex items-center gap-2 px-4 py-2.5 rounded-2xl border border-finova-border bg-secondary-bg hover:bg-slate-100 text-finova-text text-xs font-semibold transition-all duration-150"
          >
            <RefreshCw className={`w-3.5 h-3.5 ${loading ? 'animate-spin' : ''}`} />
            <span>Refresh Status</span>
          </button>

          {status?.hasGoogleAccountLinked && (
            <button
              onClick={handleRevokeAll}
              disabled={actionLoading === 'revoke-all'}
              className="flex items-center gap-2 px-4 py-2.5 rounded-2xl border border-red-200 bg-red-50 hover:bg-red-100 text-finova-danger text-xs font-semibold transition-all duration-150"
            >
              <Unlink className="w-3.5 h-3.5" />
              <span>Revoke All Google Access</span>
            </button>
          )}
        </div>
      </div>

      {/* Google Account Profile Card */}
      <div className="bg-white rounded-3xl p-6 border border-finova-border shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
          <div className="flex items-center gap-4">
            {status?.profilePicture ? (
              <img
                src={status.profilePicture}
                alt="Google Profile"
                className="w-14 h-14 rounded-2xl object-cover border-2 border-lavender-200"
              />
            ) : (
              <div className="w-14 h-14 rounded-2xl bg-lavender-100 text-primary flex items-center justify-center font-bold text-xl border border-lavender-200">
                G
              </div>
            )}
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-sm font-bold text-finova-text">
                  {status?.hasGoogleAccountLinked ? status.googleEmail : 'No Google Account Linked'}
                </h3>
                {status?.hasGoogleAccountLinked ? (
                  <span className="flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-md bg-sage-100 text-sage-700">
                    <CheckCircle2 className="w-3 h-3 text-finova-success" /> Linked
                  </span>
                ) : (
                  <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-slate-100 text-finova-muted">
                    Disconnected
                  </span>
                )}
              </div>
              <p className="text-xs text-finova-muted mt-0.5">
                {status?.hasGoogleAccountLinked
                  ? `Google OpenID Connect identity linked. Incremental permissions requested per service.`
                  : 'Connect your Google account to enable two-way sync, sheets exports, and reminders.'}
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3">
            {!status?.hasGoogleAccountLinked ? (
              <button
                onClick={() => handleConnectService('AUTH')}
                disabled={actionLoading === 'connect-AUTH'}
                className="flex items-center gap-2 px-5 py-2.5 rounded-2xl bg-primary hover:bg-primary-dark text-white text-xs font-semibold shadow-sm transition-all duration-150"
              >
                <Lock className="w-3.5 h-3.5" />
                <span>Sign In / Link Google</span>
              </button>
            ) : (
              <div className="flex items-center gap-2 text-xs text-sage-700 bg-sage-50 px-3 py-1.5 rounded-xl border border-sage-200">
                <ShieldCheck className="w-4 h-4 text-finova-success" />
                <span className="font-semibold">AES-256-GCM Encrypted Token Storage</span>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Service Integration Cards Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {/* 1. Google Calendar */}
        <div className="bg-white rounded-3xl p-6 border border-finova-border shadow-sm flex flex-col justify-between hover:shadow-md transition-all duration-200">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-lavender-50 text-primary flex items-center justify-center border border-lavender-100">
                <Calendar className="w-6 h-6" />
              </div>
              {getServiceStatus('CALENDAR')?.connected ? (
                <span className="flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-md bg-sage-100 text-sage-700">
                  <CheckCircle2 className="w-3 h-3 text-finova-success" /> Connected
                </span>
              ) : (
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-slate-100 text-finova-muted">
                  Not Connected
                </span>
              )}
            </div>

            <h3 className="text-sm font-bold text-finova-text">Google Calendar</h3>
            <p className="text-xs text-finova-muted mt-1 leading-relaxed">
              Synchronize subscription renewals, loan repayments, savings deadlines, and monthly budget reviews directly to your Google Calendar.
            </p>

            <div className="mt-4 p-3 bg-secondary-bg rounded-2xl border border-finova-border/50 text-[11px] text-finova-muted space-y-1">
              <div className="flex items-center justify-between">
                <span>Duplicate Prevention:</span>
                <span className="font-semibold text-sage-700">Google Event ID Indexed</span>
              </div>
              <div className="flex items-center justify-between">
                <span>Synced Active Events:</span>
                <span className="font-semibold text-finova-text">{calendarEvents.length} events</span>
              </div>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-finova-border space-y-2">
            <div className="flex items-center gap-2">
              {!getServiceStatus('CALENDAR')?.connected ? (
                <button
                  onClick={() => handleConnectService('CALENDAR')}
                  disabled={actionLoading === 'connect-CALENDAR'}
                  className="flex-1 flex items-center justify-center gap-1.5 py-2.5 px-3 rounded-2xl bg-primary hover:bg-primary-dark text-white text-xs font-semibold shadow-sm transition-all"
                >
                  <span>Connect Calendar</span>
                </button>
              ) : (
                <button
                  onClick={handleSyncCalendar}
                  disabled={actionLoading === 'calendar-sync'}
                  className="flex-1 flex items-center justify-center gap-1.5 py-2.5 px-3 rounded-2xl bg-primary hover:bg-primary-dark text-white text-xs font-semibold shadow-sm transition-all"
                >
                  <RefreshCw className={`w-3.5 h-3.5 ${actionLoading === 'calendar-sync' ? 'animate-spin' : ''}`} />
                  <span>Sync Now</span>
                </button>
              )}

              {calendarEvents.length > 0 && (
                <button
                  onClick={() => setShowEventsModal(true)}
                  className="py-2.5 px-3 rounded-2xl border border-finova-border bg-secondary-bg hover:bg-slate-100 text-finova-text text-xs font-semibold"
                  title="View Synced Events"
                >
                  View ({calendarEvents.length})
                </button>
              )}
            </div>

            {getServiceStatus('CALENDAR')?.connected && (
              <button
                onClick={() => handleDisconnectService('CALENDAR')}
                className="w-full text-center text-[10px] font-semibold text-finova-danger hover:underline py-1"
              >
                Disconnect Calendar
              </button>
            )}
          </div>
        </div>

        {/* 2. Google Sheets */}
        <div className="bg-white rounded-3xl p-6 border border-finova-border shadow-sm flex flex-col justify-between hover:shadow-md transition-all duration-200">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-sage-50 text-sage-700 flex items-center justify-center border border-sage-100">
                <FileSpreadsheet className="w-6 h-6 text-finova-success" />
              </div>
              {getServiceStatus('SHEETS')?.connected ? (
                <span className="flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-md bg-sage-100 text-sage-700">
                  <CheckCircle2 className="w-3 h-3 text-finova-success" /> Connected
                </span>
              ) : (
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-slate-100 text-finova-muted">
                  Not Connected
                </span>
              )}
            </div>

            <h3 className="text-sm font-bold text-finova-text">Google Sheets</h3>
            <p className="text-xs text-finova-muted mt-1 leading-relaxed">
              Export complete student financial records into a structured 7-tab spreadsheet with summary metrics, income, expenses, budgets, savings, debts, and subscriptions.
            </p>

            <div className="mt-4 p-3 bg-secondary-bg rounded-2xl border border-finova-border/50 text-[11px] text-finova-muted space-y-1">
              <div className="flex items-center justify-between">
                <span>Workbook Structure:</span>
                <span className="font-semibold text-finova-text">7 Automated Tabs</span>
              </div>
              <div className="flex items-center justify-between">
                <span>Headers & Formats:</span>
                <span className="font-semibold text-sage-700">INR Currency & ISO Dates</span>
              </div>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-finova-border space-y-2">
            <div className="flex items-center gap-2">
              <button
                onClick={handleExportSheets}
                disabled={actionLoading === 'sheets-export'}
                className="flex-1 flex items-center justify-center gap-1.5 py-2.5 px-3 rounded-2xl bg-primary hover:bg-primary-dark text-white text-xs font-semibold shadow-sm transition-all"
              >
                <Download className={`w-3.5 h-3.5 ${actionLoading === 'sheets-export' ? 'animate-spin' : ''}`} />
                <span>Export 7-Tab Sheet</span>
              </button>

              {lastExportedSheetUrl && (
                <a
                  href={lastExportedSheetUrl}
                  target="_blank"
                  rel="noreferrer"
                  className="py-2.5 px-3 rounded-2xl border border-finova-border bg-secondary-bg hover:bg-slate-100 text-primary text-xs font-semibold flex items-center gap-1"
                >
                  <ExternalLink className="w-3.5 h-3.5" />
                  <span>Open</span>
                </a>
              )}
            </div>

            {getServiceStatus('SHEETS')?.connected && (
              <button
                onClick={() => handleDisconnectService('SHEETS')}
                className="w-full text-center text-[10px] font-semibold text-finova-danger hover:underline py-1"
              >
                Disconnect Sheets
              </button>
            )}
          </div>
        </div>

        {/* 3. Google Drive */}
        <div className="bg-white rounded-3xl p-6 border border-finova-border shadow-sm flex flex-col justify-between hover:shadow-md transition-all duration-200">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-amber-50 text-amber-600 flex items-center justify-center border border-amber-100">
                <HardDrive className="w-6 h-6" />
              </div>
              {getServiceStatus('DRIVE')?.connected ? (
                <span className="flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-md bg-sage-100 text-sage-700">
                  <CheckCircle2 className="w-3 h-3 text-finova-success" /> Connected
                </span>
              ) : (
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-slate-100 text-finova-muted">
                  Not Connected
                </span>
              )}
            </div>

            <h3 className="text-sm font-bold text-finova-text">Google Drive Storage</h3>
            <p className="text-xs text-finova-muted mt-1 leading-relaxed">
              Upload PDF statements and Excel reports to dedicated folders under <code className="text-primary font-bold">FINOVA/</code>. Uses least-privilege app scope.
            </p>

            <div className="mt-4 p-3 bg-secondary-bg rounded-2xl border border-finova-border/50 text-[11px] text-finova-muted space-y-1">
              <div className="flex items-center justify-between">
                <span>Cloud Folder:</span>
                <span className="font-semibold text-finova-text">FINOVA/Monthly Statements</span>
              </div>
              <div className="flex items-center justify-between">
                <span>Stored Backups:</span>
                <span className="font-semibold text-finova-text">{driveFiles.length} files</span>
              </div>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-finova-border space-y-2">
            <div className="flex items-center gap-2">
              <button
                onClick={() => handleUploadDrive('PDF')}
                disabled={actionLoading === 'drive-PDF'}
                className="flex-1 flex items-center justify-center gap-1.5 py-2.5 px-3 rounded-2xl bg-primary hover:bg-primary-dark text-white text-xs font-semibold shadow-sm transition-all"
              >
                <UploadCloud className={`w-3.5 h-3.5 ${actionLoading === 'drive-PDF' ? 'animate-spin' : ''}`} />
                <span>Upload Statement (PDF)</span>
              </button>

              <button
                onClick={() => handleUploadDrive('EXCEL')}
                disabled={actionLoading === 'drive-EXCEL'}
                className="py-2.5 px-3 rounded-2xl border border-finova-border bg-secondary-bg hover:bg-slate-100 text-finova-text text-xs font-semibold"
                title="Upload Excel Report"
              >
                Excel
              </button>
            </div>

            {getServiceStatus('DRIVE')?.connected && (
              <button
                onClick={() => handleDisconnectService('DRIVE')}
                className="w-full text-center text-[10px] font-semibold text-finova-danger hover:underline py-1"
              >
                Disconnect Drive
              </button>
            )}
          </div>
        </div>

        {/* 4. Gmail Notifications */}
        <div className="bg-white rounded-3xl p-6 border border-finova-border shadow-sm flex flex-col justify-between hover:shadow-md transition-all duration-200">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-red-50 text-red-500 flex items-center justify-center border border-red-100">
                <Mail className="w-6 h-6" />
              </div>
              {getServiceStatus('GMAIL')?.connected ? (
                <span className="flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-md bg-sage-100 text-sage-700">
                  <CheckCircle2 className="w-3 h-3 text-finova-success" /> Connected
                </span>
              ) : (
                <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-slate-100 text-finova-muted">
                  Not Connected
                </span>
              )}
            </div>

            <h3 className="text-sm font-bold text-finova-text">Gmail Notifications</h3>
            <p className="text-xs text-finova-muted mt-1 leading-relaxed">
              Receive formatted monthly performance digests and budget warnings. Employs send-only permission; never inspects incoming inbox messages.
            </p>

            <div className="mt-4 p-3 bg-secondary-bg rounded-2xl border border-finova-border/50 text-[11px] text-finova-muted space-y-1">
              <div className="flex items-center justify-between">
                <span>Privacy Assurance:</span>
                <span className="font-semibold text-sage-700">Send-Only API (No Inbox Read)</span>
              </div>
              <div className="flex items-center justify-between">
                <span>Fallback:</span>
                <span className="font-semibold text-finova-text">Transactional In-App Dispatch</span>
              </div>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-finova-border space-y-2">
            <button
              onClick={handleSendMonthlySummary}
              disabled={actionLoading === 'gmail-summary'}
              className="w-full flex items-center justify-center gap-1.5 py-2.5 px-3 rounded-2xl bg-primary hover:bg-primary-dark text-white text-xs font-semibold shadow-sm transition-all"
            >
              <Send className={`w-3.5 h-3.5 ${actionLoading === 'gmail-summary' ? 'animate-spin' : ''}`} />
              <span>Dispatch Monthly Summary Email</span>
            </button>

            {getServiceStatus('GMAIL')?.connected && (
              <button
                onClick={() => handleDisconnectService('GMAIL')}
                className="w-full text-center text-[10px] font-semibold text-finova-danger hover:underline py-1"
              >
                Disconnect Gmail
              </button>
            )}
          </div>
        </div>

        {/* 5. Google Maps Geolocation */}
        <div className="bg-white rounded-3xl p-6 border border-finova-border shadow-sm flex flex-col justify-between hover:shadow-md transition-all duration-200">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center border border-emerald-100">
                <MapPin className="w-6 h-6" />
              </div>
              <span className="flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-md bg-sage-100 text-sage-700">
                <CheckCircle2 className="w-3 h-3 text-finova-success" /> Active
              </span>
            </div>

            <h3 className="text-sm font-bold text-finova-text">Google Maps Geolocation</h3>
            <p className="text-xs text-finova-muted mt-1 leading-relaxed">
              Interactive merchant & campus location tagging. Visualizes frequent student spending hotspots, university canteens, and local study cafes.
            </p>

            <div className="mt-4 p-3 bg-secondary-bg rounded-2xl border border-finova-border/50 text-[11px] text-finova-muted space-y-1">
              <div className="flex items-center justify-between">
                <span>Campus Geocoding:</span>
                <span className="font-semibold text-sage-700">Auto-Tagged per Expense</span>
              </div>
              <div className="flex items-center justify-between">
                <span>Map Provider:</span>
                <span className="font-semibold text-finova-text">Google Maps Platform</span>
              </div>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-finova-border space-y-2">
            <button
              onClick={() => showToast('Campus transaction map active in Expenses & Analytics view!')}
              className="w-full flex items-center justify-center gap-1.5 py-2.5 px-3 rounded-2xl border border-finova-border bg-secondary-bg hover:bg-slate-100 text-finova-text text-xs font-semibold transition-all"
            >
              <span>Explore Campus Hotspots</span>
            </button>
          </div>
        </div>

        {/* 6. Security & Privacy Overview Card */}
        <div className="bg-gradient-to-br from-lavender-50 to-white rounded-3xl p-6 border border-lavender-200 shadow-sm flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-4">
              <div className="w-12 h-12 rounded-2xl bg-primary text-white flex items-center justify-center shadow-sm">
                <ShieldCheck className="w-6 h-6" />
              </div>
              <span className="text-[10px] font-bold px-2 py-0.5 rounded-md bg-primary text-white">
                Fintech Standard
              </span>
            </div>

            <h3 className="text-sm font-bold text-finova-text">Privacy & Incremental Scopes</h3>
            <p className="text-xs text-finova-muted mt-1 leading-relaxed">
              FINOVA follows strict Google API Services User Data Policy:
            </p>

            <ul className="mt-3 space-y-1.5 text-[11px] text-finova-text">
              <li className="flex items-center gap-1.5">
                <CheckCircle2 className="w-3.5 h-3.5 text-finova-success flex-shrink-0" />
                <span>Tokens encrypted at rest with AES-256-GCM.</span>
              </li>
              <li className="flex items-center gap-1.5">
                <CheckCircle2 className="w-3.5 h-3.5 text-finova-success flex-shrink-0" />
                <span>Zero token leakage into browser localStorage.</span>
              </li>
              <li className="flex items-center gap-1.5">
                <CheckCircle2 className="w-3.5 h-3.5 text-finova-success flex-shrink-0" />
                <span>Incremental scopes requested only when you connect.</span>
              </li>
              <li className="flex items-center gap-1.5">
                <CheckCircle2 className="w-3.5 h-3.5 text-finova-success flex-shrink-0" />
                <span>Instant token revocation and permission removal.</span>
              </li>
            </ul>
          </div>

          <div className="mt-6 pt-4 border-t border-lavender-200">
            <span className="text-[10px] text-finova-muted font-medium">
              Verified compliant with Google Limited Use Requirements.
            </span>
          </div>
        </div>
      </div>

      {/* Uploaded Google Drive Files Table */}
      {driveFiles.length > 0 && (
        <div className="bg-white rounded-3xl p-6 border border-finova-border shadow-sm">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-bold text-finova-text">FINOVA Cloud Reports in Google Drive</h3>
              <p className="text-xs text-finova-muted">Statements and financial backups saved to your Google Drive account.</p>
            </div>
            <span className="text-xs font-bold px-2.5 py-1 rounded-lg bg-secondary-bg text-finova-muted">
              {driveFiles.length} file{driveFiles.length === 1 ? '' : 's'}
            </span>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-finova-border text-[11px] font-bold text-finova-muted uppercase tracking-wider">
                  <th className="py-3 px-4">File Name</th>
                  <th className="py-3 px-4">Folder Path</th>
                  <th className="py-3 px-4">Size</th>
                  <th className="py-3 px-4">Uploaded</th>
                  <th className="py-3 px-4 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-finova-border text-xs text-finova-text">
                {driveFiles.map((file) => (
                  <tr key={file.id} className="hover:bg-secondary-bg/50 transition-colors">
                    <td className="py-3 px-4 font-semibold text-primary flex items-center gap-2">
                      <HardDrive className="w-3.5 h-3.5 text-finova-muted" />
                      <span>{file.fileName}</span>
                    </td>
                    <td className="py-3 px-4 text-finova-muted">{file.folderPath}</td>
                    <td className="py-3 px-4 text-finova-muted">
                      {file.fileSize ? `${(file.fileSize / 1024).toFixed(1)} KB` : 'N/A'}
                    </td>
                    <td className="py-3 px-4 text-finova-muted">
                      {new Date(file.uploadedAt).toLocaleDateString()}
                    </td>
                    <td className="py-3 px-4 text-right space-x-2">
                      <a
                        href={file.webLink}
                        target="_blank"
                        rel="noreferrer"
                        className="inline-flex items-center gap-1 px-2.5 py-1 rounded-lg bg-lavender-50 hover:bg-lavender-100 text-primary text-[11px] font-semibold"
                      >
                        <ExternalLink className="w-3 h-3" />
                        <span>View</span>
                      </a>
                      <button
                        onClick={() => handleDeleteDriveFile(file.id)}
                        className="p-1 rounded-lg hover:bg-red-50 text-finova-danger text-[11px]"
                        title="Delete file"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Sync Activity History Table */}
      <div className="bg-white rounded-3xl p-6 border border-finova-border shadow-sm">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-sm font-bold text-finova-text">Recent Google Sync Activity Logs</h3>
            <p className="text-xs text-finova-muted">Audit trail of Google Workspace synchronization operations.</p>
          </div>
          <span className="text-xs font-bold px-2.5 py-1 rounded-lg bg-secondary-bg text-finova-muted">
            {syncHistory.length} entries
          </span>
        </div>

        {syncHistory.length === 0 ? (
          <div className="text-center py-8 text-finova-muted text-xs">
            No synchronization activity recorded yet. Connect a service and trigger a sync to view audit logs.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-finova-border text-[11px] font-bold text-finova-muted uppercase tracking-wider">
                  <th className="py-3 px-4">Timestamp</th>
                  <th className="py-3 px-4">Service</th>
                  <th className="py-3 px-4">Status</th>
                  <th className="py-3 px-4">Operation Details</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-finova-border text-xs text-finova-text">
                {syncHistory.slice(0, 10).map((log) => (
                  <tr key={log.id} className="hover:bg-secondary-bg/50 transition-colors">
                    <td className="py-3 px-4 text-finova-muted flex items-center gap-1.5 whitespace-nowrap">
                      <Clock className="w-3.5 h-3.5 text-finova-muted" />
                      <span>{new Date(log.syncedAt).toLocaleString()}</span>
                    </td>
                    <td className="py-3 px-4">
                      <span className="font-semibold px-2 py-0.5 rounded-md bg-secondary-bg text-finova-text text-[11px]">
                        {log.serviceName}
                      </span>
                    </td>
                    <td className="py-3 px-4">
                      <span
                        className={`inline-flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-md ${
                          log.syncStatus === 'SUCCESS'
                            ? 'bg-sage-100 text-sage-700'
                            : 'bg-red-100 text-red-700'
                        }`}
                      >
                        {log.syncStatus}
                      </span>
                    </td>
                    <td className="py-3 px-4 text-finova-muted">{log.details}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Modal: Synced Calendar Events */}
      {showEventsModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm p-4">
          <div className="bg-white rounded-3xl p-6 max-w-xl w-full border border-finova-border shadow-xl space-y-4 max-h-[85vh] flex flex-col">
            <div className="flex items-center justify-between pb-3 border-b border-finova-border">
              <div>
                <h3 className="text-sm font-bold text-finova-text">Google Calendar Synced Deadlines</h3>
                <p className="text-xs text-finova-muted">Events mapped directly into your Google Calendar.</p>
              </div>
              <button
                onClick={() => setShowEventsModal(false)}
                className="text-finova-muted hover:text-finova-text font-bold text-lg"
              >
                &times;
              </button>
            </div>

            <div className="overflow-y-auto space-y-2 pr-1 flex-1">
              {calendarEvents.map((ev) => (
                <div
                  key={ev.id}
                  className="p-3.5 rounded-2xl border border-finova-border bg-secondary-bg/60 flex items-center justify-between"
                >
                  <div className="space-y-0.5">
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded bg-lavender-100 text-primary">
                      {ev.eventType}
                    </span>
                    <h4 className="text-xs font-bold text-finova-text mt-1">{ev.title}</h4>
                    <p className="text-[11px] text-finova-muted">{ev.description}</p>
                  </div>
                  <div className="text-right flex-shrink-0 pl-3">
                    <div className="text-xs font-bold text-primary">{ev.eventDate}</div>
                    <span className="text-[9px] font-semibold text-sage-700 bg-sage-100 px-1.5 py-0.5 rounded">
                      {ev.syncStatus}
                    </span>
                  </div>
                </div>
              ))}
            </div>

            <div className="pt-3 border-t border-finova-border flex justify-end">
              <button
                onClick={() => setShowEventsModal(false)}
                className="px-4 py-2 rounded-xl bg-primary text-white text-xs font-semibold"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
