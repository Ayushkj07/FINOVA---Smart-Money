import {
  DashboardSummary,
  DashboardAnalytics,
  HealthScore,
  Income,
  Expense,
  Budget,
  SavingsGoal,
  Debt,
  Subscription,
  AiInsights,
  GoogleIntegrationStatus,
  GoogleCalendarEvent,
  GoogleDriveFile,
  GoogleSyncLog,
  NotificationItem,
  AdminStats,
  UserAdminView,
  SupportTicket,
  User
} from '../types';

export const mockUserStudent: User = {
  id: 2,
  email: 'student@finova.com',
  fullName: 'Ayush Sharma',
  roles: ['ROLE_STUDENT'],
  collegeName: 'Apex Institute of Technology',
  studentStatus: 'ACTIVE_STUDENT',
  currency: 'INR',
  avatarUrl: 'https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150',
  monthlyAllowance: 25000,
  financialGoalSummary: 'Save for MacBook Pro and maintain 30% emergency cushion'
};

export const mockUserAdmin: User = {
  id: 1,
  email: 'admin@finova.com',
  fullName: 'System Administrator',
  roles: ['ROLE_ADMIN'],
  collegeName: 'FINOVA Central Command',
  studentStatus: 'ADMIN',
  currency: 'INR',
  avatarUrl: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150'
};

export const mockGoogleUser: User = {
  id: 777,
  email: 'student.google@finova.com',
  fullName: 'Ayush Sharma (Google Verified)',
  roles: ['ROLE_STUDENT'],
  collegeName: 'Google Developer Student Campus',
  studentStatus: 'ACTIVE_STUDENT',
  currency: 'INR',
  avatarUrl: 'https://lh3.googleusercontent.com/a/default-user'
};

export const mockDashboardSummary: DashboardSummary = {
  totalBalance: 18450.0,
  monthlyIncome: 25000.0,
  monthlyExpenses: 14250.0,
  totalSavings: 12500.0,
  outstandingDebt: 4500.0,
  remainingMonthlyBudget: 5750.0,
  totalMonthlyBudgetLimit: 20000.0,
  budgetUtilizationPercentage: 71.25,
  incomeChangePercentage: 8.5,
  expenseChangePercentage: -4.2,
  savingsChangePercentage: 15.0,
  balanceChangePercentage: 12.3,
  studentName: 'Ayush Sharma',
  currency: 'INR'
};

export const mockDashboardAnalytics: DashboardAnalytics = {
  incomeVsExpenseTrend: [
    { label: 'Jun', income: 20000, expense: 16500, savings: 3500 },
    { label: 'Jul', income: 22000, expense: 15000, savings: 7000 },
    { label: 'Aug', income: 25000, expense: 18000, savings: 7000 },
    { label: 'Sep', income: 23500, expense: 14800, savings: 8700 },
    { label: 'Oct', income: 25000, expense: 15200, savings: 9800 },
    { label: 'Nov', income: 25000, expense: 14250, savings: 10750 },
  ],
  expenseCategoryBreakdown: [
    { name: 'Food & Dining', value: 4800, color: '#8884d8', percentage: 33.7 },
    { name: 'Academics & Books', value: 3200, color: '#82ca9d', percentage: 22.5 },
    { name: 'Commute & Travel', value: 2100, color: '#ffc658', percentage: 14.7 },
    { name: 'Tech & Subscriptions', value: 1650, color: '#ff8042', percentage: 11.6 },
    { name: 'Hostel & Utilities', value: 2500, color: '#8dd1e1', percentage: 17.5 },
  ],
  monthlySpendingTrend: [
    { period: 'Week 1', amount: 3800 },
    { period: 'Week 2', amount: 4200 },
    { period: 'Week 3', amount: 3450 },
    { period: 'Week 4', amount: 2800 },
  ],
  savingsGrowthTrend: [
    { label: 'Jun', income: 3500, expense: 0, savings: 3500 },
    { label: 'Jul', income: 7000, expense: 0, savings: 10500 },
    { label: 'Aug', income: 7000, expense: 0, savings: 17500 },
    { label: 'Sep', income: 8700, expense: 0, savings: 26200 },
    { label: 'Oct', income: 9800, expense: 0, savings: 36000 },
    { label: 'Nov', income: 10750, expense: 0, savings: 46750 },
  ],
  budgetUtilization: 71.25,
  savingsRate: 43.0
};

export const mockHealthScore: HealthScore = {
  overallScore: 88,
  grade: 'A',
  savingsRateScore: 92,
  budgetDisciplineScore: 86,
  spendingConsistencyScore: 89,
  debtManagementScore: 85,
  emergencySavingsScore: 90,
  suggestions: [
    'You are saving 43% of your income — exceptional for a college student!',
    'Your Food & Dining spending has dropped 12% compared to last month.',
    'Keep your peer debt payments timely to maintain an A+ financial standing.'
  ],
  strengths: [
    'Emergency fund covers 3+ months of college living expenses',
    'Low debt-to-income ratio (< 20%)',
    'Zero missed subscription renewal deadlines'
  ],
  educationalDisclaimer: 'FINOVA Financial Health Score is calculated based on peer benchmarks, savings ratios, and budget compliance.'
};

export const mockExpenses: Expense[] = [
  {
    id: 101,
    title: 'Computer Science Core Textbooks',
    amount: 1850.0,
    categoryName: 'Academics & Books',
    date: '2026-11-05',
    expenseDate: '2026-11-05',
    paymentMethod: 'UPI',
    description: 'Algorithms and Operating Systems course reference books',
    isRecurring: false
  },
  {
    id: 102,
    title: 'Campus Cafeteria Meal Plan',
    amount: 650.0,
    categoryName: 'Food & Dining',
    date: '2026-11-06',
    expenseDate: '2026-11-06',
    paymentMethod: 'UPI',
    description: 'Weekly campus dining hall recharge',
    isRecurring: true,
    recurrencePeriod: 'WEEKLY'
  },
  {
    id: 103,
    title: 'Metro Smart Card Recharge',
    amount: 500.0,
    categoryName: 'Commute & Travel',
    date: '2026-11-07',
    expenseDate: '2026-11-07',
    paymentMethod: 'UPI',
    description: 'Monthly student commute metro pass',
    isRecurring: true,
    recurrencePeriod: 'MONTHLY'
  },
  {
    id: 104,
    title: 'Spotify Student Premium',
    amount: 59.0,
    categoryName: 'Tech & Subscriptions',
    date: '2026-11-08',
    expenseDate: '2026-11-08',
    paymentMethod: 'DEBIT_CARD',
    description: 'Student discounted music streaming plan',
    isRecurring: true,
    recurrencePeriod: 'MONTHLY'
  },
  {
    id: 105,
    title: 'Hostel Wi-Fi Contribution',
    amount: 450.0,
    categoryName: 'Hostel & Utilities',
    date: '2026-11-08',
    expenseDate: '2026-11-08',
    paymentMethod: 'UPI',
    description: 'Shared high-speed internet bill for November',
    isRecurring: true,
    recurrencePeriod: 'MONTHLY'
  }
];

export const mockIncomes: Income[] = [
  {
    id: 201,
    title: 'Monthly Parent Allowance',
    amount: 15000.0,
    category: 'ALLOWANCE',
    categoryName: 'Allowance',
    date: '2026-11-01',
    incomeDate: '2026-11-01',
    paymentMethod: 'BANK_TRANSFER',
    description: 'Living allowance for November',
    isRecurring: true,
    recurrencePeriod: 'MONTHLY'
  },
  {
    id: 202,
    title: 'Math & Coding Peer Tutoring',
    amount: 6000.0,
    category: 'TUTORING',
    categoryName: 'Tutoring',
    date: '2026-11-04',
    incomeDate: '2026-11-04',
    paymentMethod: 'UPI',
    description: 'Tutored freshman Data Structures and Discrete Math',
    isRecurring: false
  },
  {
    id: 203,
    title: 'Freelance Web Design Gig',
    amount: 4000.0,
    category: 'FREELANCE',
    categoryName: 'Freelance',
    date: '2026-11-07',
    incomeDate: '2026-11-07',
    paymentMethod: 'BANK_TRANSFER',
    description: 'Landing page design for college tech fest',
    isRecurring: false
  }
];

export const mockBudgets: Budget[] = [
  {
    id: 301,
    name: 'November College Budget',
    month: 11,
    year: 2026,
    totalBudgetLimit: 20000.0,
    totalSpentAmount: 14250.0,
    spentAmount: 14250.0,
    totalRemainingAmount: 5750.0,
    totalUtilizationPercentage: 71.25,
    alertLevel: 'NORMAL',
    categories: [
      { id: 1, categoryName: 'Food & Dining', allocatedAmount: 7000, spentAmount: 4800, remainingAmount: 2200, utilizationPercentage: 68.5, alertLevel: 'NORMAL' },
      { id: 2, categoryName: 'Academics & Books', allocatedAmount: 4000, spentAmount: 3200, remainingAmount: 800, utilizationPercentage: 80.0, alertLevel: 'WARNING' },
      { id: 3, categoryName: 'Commute & Travel', allocatedAmount: 3000, spentAmount: 2100, remainingAmount: 900, utilizationPercentage: 70.0, alertLevel: 'NORMAL' },
      { id: 4, categoryName: 'Tech & Subscriptions', allocatedAmount: 2000, spentAmount: 1650, remainingAmount: 350, utilizationPercentage: 82.5, alertLevel: 'WARNING' },
      { id: 5, categoryName: 'Hostel & Utilities', allocatedAmount: 4000, spentAmount: 2500, remainingAmount: 1500, utilizationPercentage: 62.5, alertLevel: 'NORMAL' }
    ]
  }
];

export const mockSavingsGoals: SavingsGoal[] = [
  {
    id: 401,
    name: 'MacBook Pro M3 Fund',
    goalName: 'MacBook Pro M3 Fund',
    targetAmount: 85000.0,
    currentAmount: 52000.0,
    remainingAmount: 33000.0,
    progressPercentage: 61.2,
    targetDate: '2027-04-15',
    category: 'TECH',
    description: 'Upgrading laptop for senior year machine learning and app development project',
    status: 'IN_PROGRESS',
    contributions: [
      { id: 1, amount: 5000, contributionDate: '2026-11-02', notes: 'Tutoring income savings' },
      { id: 2, amount: 3000, contributionDate: '2026-10-28', notes: 'Freelance gig milestone' }
    ]
  },
  {
    id: 402,
    name: 'Semester Emergency Fund',
    goalName: 'Semester Emergency Fund',
    targetAmount: 25000.0,
    currentAmount: 20000.0,
    remainingAmount: 5000.0,
    progressPercentage: 80.0,
    targetDate: '2026-12-31',
    category: 'EMERGENCY',
    description: '3-month buffer for medical, travel, or device emergency repairs',
    status: 'IN_PROGRESS',
    contributions: [
      { id: 3, amount: 2500, contributionDate: '2026-11-01', notes: 'Monthly budget surplus' }
    ]
  }
];

export const mockDebts: Debt[] = [
  {
    id: 501,
    type: 'BORROWED',
    debtType: 'BORROWED',
    counterpartyName: 'Rohan Verma',
    personName: 'Rohan Verma',
    counterpartyContact: 'rohan.v@campus.edu',
    initialAmount: 3000.0,
    outstandingBalance: 1500.0,
    remainingAmount: 1500.0,
    totalPaid: 1500.0,
    dueDate: '2026-11-25',
    description: 'Shared cost for hackathon registration & hotel stay',
    status: 'ACTIVE',
    repayments: [
      { id: 1, amount: 1500.0, paymentDate: '2026-11-03', notes: 'Part payment via UPI' }
    ]
  },
  {
    id: 502,
    type: 'LENT',
    debtType: 'LENT',
    counterpartyName: 'Priya Patel',
    personName: 'Priya Patel',
    counterpartyContact: 'priya.p@campus.edu',
    initialAmount: 1200.0,
    outstandingBalance: 1200.0,
    remainingAmount: 1200.0,
    totalPaid: 0.0,
    dueDate: '2026-11-20',
    description: 'Lent for lab equipment & stationary purchase',
    status: 'ACTIVE',
    repayments: []
  }
];

export const mockSubscriptions: Subscription[] = [
  {
    id: 601,
    name: 'Spotify Student Premium',
    amount: 59.0,
    billingCycle: 'MONTHLY',
    category: 'Entertainment',
    nextBillingDate: '2026-11-18',
    nextRenewalDate: '2026-11-18',
    isActive: true,
    remindBeforeDays: 2,
    daysUntilRenewal: 9,
    description: 'Monthly student music streaming'
  },
  {
    id: 602,
    name: 'GitHub Copilot Student',
    amount: 0.0,
    billingCycle: 'ANNUAL',
    category: 'Education',
    nextBillingDate: '2027-08-30',
    nextRenewalDate: '2027-08-30',
    isActive: true,
    remindBeforeDays: 7,
    daysUntilRenewal: 294,
    description: 'GitHub Student Developer Pack Benefit'
  },
  {
    id: 603,
    name: 'Amazon Prime Student',
    amount: 125.0,
    billingCycle: 'MONTHLY',
    category: 'Shopping & Delivery',
    nextBillingDate: '2026-11-24',
    nextRenewalDate: '2026-11-24',
    isActive: true,
    remindBeforeDays: 3,
    daysUntilRenewal: 15,
    description: 'Fast delivery for course books and electronics'
  }
];

export const mockAiInsights: AiInsights = {
  monthlySummary: 'Great job Ayush! Your savings rate is at 43%, placing you in the top 5% of student money managers. Your Food & Dining expense is well under your monthly ceiling.',
  insights: [
    {
      type: 'SAVINGS_ALERT',
      title: 'Emergency Fund 80% Funded',
      description: 'You are just ₹5,000 away from your ₹25,000 emergency fund goal. One more tutoring session will close the gap!',
      severity: 'POSITIVE',
      icon: 'PiggyBank',
      actionText: 'View Goal'
    },
    {
      type: 'SUBSCRIPTION_NOTICE',
      title: 'Spotify Student Renews in 9 Days',
      description: '₹59 will be debited on Nov 18. Auto-synced to your Google Calendar.',
      severity: 'INFO',
      icon: 'Calendar',
      actionText: 'Manage'
    },
    {
      type: 'BUDGET_WARNING',
      title: 'Academics Category at 80%',
      description: 'You have ₹800 remaining for course materials this month. Consider checking the campus library before purchasing new textbooks.',
      severity: 'WARNING',
      icon: 'AlertTriangle',
      actionText: 'View Category'
    }
  ],
  topSavingOpportunities: [
    'Switching to campus Wi-Fi exclusively can save ₹450/month',
    'Cooking 2 more meals at the hostel kitchenette can free up ₹1,200/month',
    'Use student metro pass discount on weekends too'
  ],
  financialTips: [
    'Always set aside 20% of freelance income for taxes and emergency savings first',
    'Track peer loans immediately to avoid forgotten repayments',
    'Review your active subscriptions every 3 months'
  ],
  spendingAnomalies: [
    'Book purchases were ₹1,850 higher than last month due to start of semester'
  ],
  predictedNextMonthSpending: 13800.0,
  disclaimer: 'AI Insights are powered by Google Gemini and generated from verified transaction patterns. Educational guidance only.'
};

export const mockGoogleStatus: GoogleIntegrationStatus = {
  hasGoogleAccountLinked: true,
  googleEmail: 'student.google@finova.com',
  googleName: 'Ayush Sharma (Google Verified)',
  profilePicture: 'https://lh3.googleusercontent.com/a/default-user',
  linkedAt: '2026-11-09T10:00:00Z',
  services: [
    {
      serviceName: 'AUTH',
      connectionStatus: 'CONNECTED',
      connected: true,
      scopes: 'openid email profile',
      lastSyncAt: new Date().toISOString(),
      description: 'Google OpenID Connect Sign-In & Student Profile Linking'
    },
    {
      serviceName: 'CALENDAR',
      connectionStatus: 'CONNECTED',
      connected: true,
      scopes: 'https://www.googleapis.com/auth/calendar.events',
      lastSyncAt: new Date().toISOString(),
      description: 'Two-way Financial Reminders, Subscription & Loan Calendar Sync'
    },
    {
      serviceName: 'SHEETS',
      connectionStatus: 'CONNECTED',
      connected: true,
      scopes: 'https://www.googleapis.com/auth/spreadsheets',
      lastSyncAt: new Date().toISOString(),
      description: 'Automated 7-Tab Financial Workbook Export & Sync'
    },
    {
      serviceName: 'DRIVE',
      connectionStatus: 'CONNECTED',
      connected: true,
      scopes: 'https://www.googleapis.com/auth/drive.file',
      lastSyncAt: new Date().toISOString(),
      description: 'Encrypted Financial Statements & Receipts Cloud Storage'
    },
    {
      serviceName: 'GMAIL',
      connectionStatus: 'CONNECTED',
      connected: true,
      scopes: 'https://www.googleapis.com/auth/gmail.send',
      lastSyncAt: new Date().toISOString(),
      description: 'Direct Financial Alerts & Monthly Performance Summaries'
    }
  ]
};

export const mockCalendarEvents: GoogleCalendarEvent[] = [
  {
    id: 1,
    financialRecordId: 601,
    googleEventId: 'evt_spotify_nov_18',
    eventType: 'SUBSCRIPTION',
    eventDate: '2026-11-18',
    syncStatus: 'SYNCED',
    title: 'Spotify Student Premium Renewal',
    description: 'Monthly Spotify subscription auto-renewal of ₹59. Managed by FINOVA.',
    createdAt: '2026-11-09T10:00:00Z'
  },
  {
    id: 2,
    financialRecordId: 501,
    googleEventId: 'evt_repayment_nov_25',
    eventType: 'DEBT',
    eventDate: '2026-11-25',
    syncStatus: 'SYNCED',
    title: 'Repay Rohan Verma (Hackathon Split)',
    description: 'Upcoming repayment of ₹1,500 due to Rohan Verma. Managed by FINOVA.',
    createdAt: '2026-11-09T10:00:00Z'
  }
];

export const mockDriveFiles: GoogleDriveFile[] = [
  {
    id: 1,
    googleFileId: 'drive_stmt_nov_2026',
    fileName: 'FINOVA_Monthly_Statement_Nov_2026.pdf',
    fileType: 'application/pdf',
    folderPath: 'FINOVA/Monthly Statements',
    fileSize: 248500,
    webLink: 'https://drive.google.com',
    uploadedAt: '2026-11-09T12:00:00Z'
  },
  {
    id: 2,
    googleFileId: 'drive_sheets_workbook_2026',
    fileName: 'FINOVA_7Tab_Financial_Master_Workbook.xlsx',
    fileType: 'application/vnd.google-apps.spreadsheet',
    folderPath: 'FINOVA/Financial Reports',
    fileSize: 184200,
    webLink: 'https://sheets.google.com',
    uploadedAt: '2026-11-09T14:30:00Z'
  }
];

export const mockSyncLogs: GoogleSyncLog[] = [
  {
    id: 1,
    serviceName: 'CALENDAR',
    syncStatus: 'SUCCESS',
    errorMessage: null,
    details: 'Synchronized 2 upcoming financial deadlines with Google Calendar.',
    syncedAt: new Date(Date.now() - 3600000).toISOString()
  },
  {
    id: 2,
    serviceName: 'SHEETS',
    syncStatus: 'SUCCESS',
    errorMessage: null,
    details: 'Exported 7 financial sheets: Summary, Income, Expenses, Budgets, Savings, Debt, Subscriptions.',
    syncedAt: new Date(Date.now() - 7200000).toISOString()
  },
  {
    id: 3,
    serviceName: 'AUTH',
    syncStatus: 'SUCCESS',
    errorMessage: null,
    details: 'Google Account linked and verified.',
    syncedAt: new Date(Date.now() - 86400000).toISOString()
  }
];

export const mockNotifications: NotificationItem[] = [
  {
    id: 1,
    title: 'Google Integration Connected',
    message: 'Your Google Calendar, Sheets, and Drive are connected and synchronizing.',
    type: 'SYSTEM',
    isRead: false,
    createdAt: new Date().toISOString()
  },
  {
    id: 2,
    title: 'Budget Milestone Achieved',
    message: 'You have stayed within budget for 3 consecutive weeks this month!',
    type: 'GOAL_MILESTONE',
    isRead: false,
    createdAt: new Date(Date.now() - 86400000).toISOString()
  }
];

export const mockAdminStats: AdminStats = {
  totalUsers: 1420,
  totalStudents: 1290,
  activeUsers: 980,
  totalTransactionsRecorded: 34850,
  totalPlatformVolume: 8750000.0,
  systemStatus: 'OPERATIONAL'
};

export const mockAdminUsers: UserAdminView[] = [
  {
    id: 1,
    email: 'admin@finova.com',
    fullName: 'System Administrator',
    collegeName: 'FINOVA Central Command',
    roles: ['ROLE_ADMIN'],
    currency: 'INR',
    isActive: true,
    isVerified: true,
    createdAt: '2026-01-01T00:00:00Z'
  },
  {
    id: 2,
    email: 'student@finova.com',
    fullName: 'Ayush Sharma',
    collegeName: 'Apex Institute of Technology',
    roles: ['ROLE_STUDENT'],
    currency: 'INR',
    isActive: true,
    isVerified: true,
    createdAt: '2026-09-15T10:00:00Z'
  },
  {
    id: 777,
    email: 'student.google@finova.com',
    fullName: 'Ayush Sharma (Google Verified)',
    collegeName: 'Google Developer Student Campus',
    roles: ['ROLE_STUDENT'],
    currency: 'INR',
    isActive: true,
    isVerified: true,
    createdAt: '2026-11-09T10:00:00Z'
  }
];

export const mockAdminTickets: SupportTicket[] = [
  {
    id: 1,
    userEmail: 'student@finova.com',
    subject: 'Google Calendar sync setup assistance',
    message: 'Can I select a custom Google Calendar for loan reminders?',
    status: 'RESOLVED',
    adminResponse: 'Yes, reminders are synced directly to your primary FINOVA calendar.',
    createdAt: '2026-11-08T14:00:00Z'
  }
];
