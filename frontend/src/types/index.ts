export type RoleType = 'ROLE_STUDENT' | 'ROLE_ADMIN';

export type PaymentMethod = 'CASH' | 'UPI' | 'DEBIT_CARD' | 'CREDIT_CARD' | 'BANK_TRANSFER';

export type RecurrencePeriod = 'NONE' | 'DAILY' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';

export type DebtType = 'BORROWED' | 'LENT';

export type DebtStatus = 'ACTIVE' | 'PAID' | 'OVERDUE';

export type BillingCycle = 'WEEKLY' | 'MONTHLY' | 'ANNUAL';

export type SplitType = 'EQUAL' | 'EXACT' | 'PERCENTAGE';

export type SentimentType = 'PROUD' | 'NEUTRAL' | 'REGRETFUL' | 'MOTIVATED';

export interface User {
  id: number;
  email: string;
  fullName: string;
  roles: string[];
  collegeName?: string;
  studentStatus?: string;
  currency: string;
  avatarUrl?: string;
  phoneNumber?: string;
  monthlyAllowance?: number;
  financialGoalSummary?: string;
  timezone?: string;
  notificationEmailEnabled?: boolean;
  notificationInAppEnabled?: boolean;
}

export interface AuthResponse {
  token: string;
  tokenType: string;
  refreshToken: string;
  id: number;
  email: string;
  fullName: string;
  roles: string[];
  collegeName?: string;
  currency: string;
  studentStatus?: string;
}

export interface GoogleAuthChallengeResponse {
  challengeRequired: boolean;
  challengeId: string;
  email: string;
  maskedEmail: string;
  expiresInSeconds: number;
  resendCooldownSeconds: number;
  message: string;
}

export interface DashboardSummary {
  totalBalance: number;
  monthlyIncome: number;
  monthlyExpenses: number;
  totalSavings: number;
  outstandingDebt: number;
  remainingMonthlyBudget: number;
  totalMonthlyBudgetLimit: number;
  budgetUtilizationPercentage: number;
  incomeChangePercentage: number;
  expenseChangePercentage: number;
  savingsChangePercentage: number;
  balanceChangePercentage: number;
  studentName: string;
  currency: string;
}

export interface ChartPoint {
  label: string;
  income: number;
  expense: number;
  savings: number;
}

export interface CategoryPieSlice {
  name: string;
  value: number;
  color: string;
  percentage: number;
}

export interface SpendingBarPoint {
  period: string;
  amount: number;
}

export interface DashboardAnalytics {
  incomeVsExpenseTrend: ChartPoint[];
  expenseCategoryBreakdown: CategoryPieSlice[];
  monthlySpendingTrend: SpendingBarPoint[];
  savingsGrowthTrend: ChartPoint[];
  budgetUtilization: number;
  savingsRate: number;
}

export interface HealthScore {
  overallScore: number;
  grade: string;
  savingsRateScore: number;
  budgetDisciplineScore: number;
  spendingConsistencyScore: number;
  debtManagementScore: number;
  emergencySavingsScore: number;
  suggestions: string[];
  strengths?: string[];
  educationalDisclaimer?: string;
}

export interface Income {
  id: number;
  title: string;
  amount: number;
  category: string;
  categoryName?: string;
  date?: string;
  incomeDate?: string;
  paymentMethod: PaymentMethod;
  description?: string;
  isRecurring: boolean;
  recurrencePeriod?: RecurrencePeriod;
  createdAt?: string;
}

export interface Expense {
  id: number;
  title: string;
  amount: number;
  categoryId?: number;
  categoryName: string;
  date?: string;
  expenseDate?: string;
  paymentMethod: PaymentMethod;
  description?: string;
  receiptUrl?: string;
  isRecurring: boolean;
  recurrencePeriod?: RecurrencePeriod;
  createdAt?: string;
}

export interface BudgetCategory {
  id?: number;
  categoryName: string;
  limitAmount?: number;
  allocatedAmount?: number;
  spentAmount: number;
  remainingAmount?: number;
  utilizationPercentage?: number;
  alertLevel?: 'NORMAL' | 'WARNING' | 'HIGH_WARNING' | 'EXCEEDED';
}

export interface Budget {
  id: number;
  name?: string;
  month: number;
  year: number;
  totalBudgetLimit: number;
  totalSpentAmount?: number;
  spentAmount?: number;
  totalRemainingAmount?: number;
  totalUtilizationPercentage?: number;
  alertLevel?: string;
  categories: BudgetCategory[];
}

export interface SavingsContribution {
  id: number;
  amount: number;
  contributionDate: string;
  notes?: string;
}

export interface SavingsGoal {
  id: number;
  name?: string;
  goalName?: string;
  targetAmount: number;
  currentAmount: number;
  remainingAmount?: number;
  progressPercentage?: number;
  targetDate: string;
  estimatedCompletionDate?: string;
  category?: string;
  description?: string;
  status?: string;
  contributions?: SavingsContribution[];
}

export interface DebtRepayment {
  id: number;
  amount: number;
  paymentDate: string;
  notes?: string;
}

export interface Debt {
  id: number;
  type?: DebtType;
  debtType?: DebtType;
  counterpartyName?: string;
  personName?: string;
  counterpartyContact?: string;
  initialAmount: number;
  outstandingBalance?: number;
  remainingAmount?: number;
  totalPaid?: number;
  interestRate?: number;
  dueDate: string;
  description?: string;
  status: DebtStatus;
  repayments?: DebtRepayment[];
}

export interface Subscription {
  id: number;
  name: string;
  amount: number;
  billingCycle: BillingCycle;
  category: string;
  nextRenewalDate?: string;
  nextBillingDate?: string;
  isActive: boolean;
  reminderDaysBefore?: number;
  remindBeforeDays?: number;
  description?: string;
  daysUntilRenewal?: number;
}

export interface AiChatMessage {
  id: string;
  sender: 'user' | 'assistant';
  content: string;
  timestamp: string;
  suggestions?: string[];
  disclaimer?: string;
}

export interface AiInsightCard {
  type: string;
  title: string;
  description: string;
  severity: 'INFO' | 'POSITIVE' | 'WARNING' | 'CRITICAL';
  icon: string;
  actionText: string;
}

export interface AiInsights {
  monthlySummary?: string;
  insights?: AiInsightCard[];
  topSavingOpportunities?: string[];
  financialTips?: string[];
  spendingAnomalies?: string[];
  budgetOptimizationSuggestions?: string[];
  predictedNextMonthSpending?: number;
  disclaimer: string;
}

export interface SplitMember {
  id: number;
  name?: string;
  memberName?: string;
  email?: string;
  owedAmount?: number;
  allocatedAmount?: number;
  paidAmount?: number;
  isPaid?: boolean;
  hasPaid?: boolean;
}

export interface ExpenseSplit {
  id: number;
  title: string;
  totalAmount: number;
  splitType: SplitType;
  isSettled: boolean;
  members: SplitMember[];
  createdAt?: string;
}

export interface FinancialJournal {
  id: number;
  title: string;
  entryDate: string;
  notes?: string;
  reflectionText?: string;
  sentiment: SentimentType;
  category?: string;
  tags?: string;
  createdAt?: string;
}

export interface ChallengeParticipant {
  id: number;
  challengeId: number;
  challengeTitle: string;
  challengeDescription: string;
  targetSavings: number;
  durationDays: number;
  badgeName: string;
  badgeIcon: string;
  startDate: string;
  endDate: string;
  currentProgress: number;
  progressPercentage: number;
  status: string;
}

export interface NotificationItem {
  id: number;
  title: string;
  message: string;
  type: 'BUDGET_ALERT' | 'BILL_REMINDER' | 'GOAL_MILESTONE' | 'AI_TIP' | 'SYSTEM';
  isRead: boolean;
  actionUrl?: string;
  createdAt: string;
}

export interface AdminStats {
  totalUsers?: number;
  totalStudents?: number;
  activeUsers?: number;
  newRegistrationsThisMonth?: number;
  totalTransactionsRecorded?: number;
  totalPlatformVolume: number;
  totalAiQueries?: number;
  systemStatus?: string;
  databaseHealth?: string;
  openSupportTickets?: number;
  openTicketsCount?: number;
  activeBudgetsCount?: number;
}

export interface UserAdminView {
  id: number;
  email: string;
  fullName: string;
  collegeName?: string;
  studentStatus?: string;
  currency: string;
  isActive: boolean;
  isVerified: boolean;
  roles: string[];
  createdAt: string;
}

export interface SupportTicket {
  id: number;
  userId?: number;
  userEmail?: string;
  studentEmail?: string;
  userName?: string;
  subject: string;
  message: string;
  status: string;
  priority?: string;
  adminResponse?: string;
  createdAt?: string;
  updatedAt?: string;
}

// --- GOOGLE INTEGRATION SUITE TYPES ---
export interface GoogleServiceStatus {
  serviceName: 'AUTH' | 'CALENDAR' | 'SHEETS' | 'DRIVE' | 'GMAIL' | 'MAPS' | string;
  connectionStatus: 'CONNECTED' | 'DISCONNECTED' | 'ERROR';
  connected: boolean;
  scopes: string;
  lastSyncAt: string | null;
  description: string;
}

export interface GoogleIntegrationStatus {
  hasGoogleAccountLinked: boolean;
  googleEmail: string | null;
  googleName: string | null;
  profilePicture: string | null;
  linkedAt: string | null;
  services: GoogleServiceStatus[];
}

export interface GoogleConnectResponse {
  authUrl: string;
  service: string;
  state: string;
  scope?: string;
}

export interface GoogleCalendarSyncResult {
  syncedCount: number;
  updatedCount: number;
  deletedCount: number;
  errorsCount: number;
  message: string;
  details: string[];
}

export interface GoogleCalendarEvent {
  id: number;
  financialRecordId: number;
  googleEventId: string;
  eventType: string;
  eventDate: string;
  syncStatus: string;
  title: string;
  description: string;
  createdAt: string;
}

export interface GoogleSheetsExportResponse {
  spreadsheetId: string;
  spreadsheetUrl: string;
  title: string;
  sheetsExportedCount: number;
  message: string;
}

export interface GoogleDriveFile {
  id: number;
  googleFileId: string;
  fileName: string;
  fileType: string;
  folderPath: string;
  fileSize: number;
  webLink: string;
  uploadedAt: string;
}

export interface GoogleDriveUploadResponse {
  fileId: string;
  fileName: string;
  fileType: string;
  folderPath: string;
  webLink: string;
  fileSize: number;
  message: string;
}

export interface GoogleSyncLog {
  id: number;
  serviceName: string;
  syncStatus: string;
  errorMessage: string | null;
  details: string;
  syncedAt: string;
}
