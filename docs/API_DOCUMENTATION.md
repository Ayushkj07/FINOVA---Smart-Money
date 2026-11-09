# FINOVA – REST API Documentation

## 1. Overview
FINOVA exposes RESTful JSON endpoints secured by stateless JWT Bearer tokens with refresh token rotation.
- **Base URL**: `http://localhost:8080/api`
- **Interactive Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

---

## 2. Authentication & Profile Endpoints (`/api/auth`)

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new student account | No |
| `POST` | `/api/auth/login` | Authenticate student or admin and receive JWT + Refresh Token | No |
| `POST` | `/api/auth/refresh` | Exchange valid refresh token for a new JWT | No |
| `GET` | `/api/auth/me` | Fetch currently authenticated student profile | Yes (`STUDENT` / `ADMIN`) |
| `PUT` | `/api/auth/profile` | Update profile details (college, allowance, currency) | Yes |
| `PUT` | `/api/auth/change-password` | Update account password | Yes |
| `POST` | `/api/auth/forgot-password` | Request password reset token via email | No |
| `POST` | `/api/auth/reset-password` | Reset password using verified token | No |
| `POST` | `/api/auth/logout` | Revoke current refresh token | Yes |

---

## 3. Student Dashboard Endpoints (`/api/dashboard`)

| Method | Endpoint | Description | Query Parameters |
|---|---|---|---|
| `GET` | `/api/dashboard/summary` | Summary cards (Balance, Inflow, Outflow, Savings, Debt, Budget) | `timeFilter` (`TODAY`, `THIS_WEEK`, `THIS_MONTH`, `LAST_MONTH`, `THIS_YEAR`) |
| `GET` | `/api/dashboard/analytics` | Recharts data (Income vs Expense, Category Breakdown, Spending Bar) | `timeFilter` |
| `GET` | `/api/dashboard/health-score` | Financial Health Score (0-100, Grade A-D, Breakdown & Suggestions) | None |

---

## 4. Income Management Endpoints (`/api/incomes`)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `GET` | `/api/incomes` | List student incomes (optional date range) | None |
| `GET` | `/api/incomes/{id}` | Get specific income entry | None |
| `POST` | `/api/incomes` | Record new income entry | `{ title, amount, categoryName, incomeDate, paymentMethod, isRecurring, description }` |
| `PUT` | `/api/incomes/{id}` | Update existing income entry | `{ title, amount, categoryName, incomeDate, paymentMethod, isRecurring, description }` |
| `DELETE` | `/api/incomes/{id}` | Delete income entry | None |

---

## 5. Expense Tracking Endpoints (`/api/expenses`)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `GET` | `/api/expenses` | List student expenses (optional date range) | None |
| `GET` | `/api/expenses/{id}` | Get expense details | None |
| `POST` | `/api/expenses` | Log new expense | `{ title, amount, categoryName, expenseDate, paymentMethod, isRecurring, description, receiptUrl }` |
| `PUT` | `/api/expenses/{id}` | Update expense | `{ title, amount, categoryName, expenseDate, paymentMethod, isRecurring, description }` |
| `DELETE` | `/api/expenses/{id}` | Delete expense entry | None |
| `POST` | `/api/expenses/scan-receipt` | OCR Bill Extraction (Gemini AI Vision) | `multipart/form-data` (`file`) |

---

## 6. Smart Budget Planner Endpoints (`/api/budgets`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/budgets` | Fetch historical budgets |
| `GET` | `/api/budgets/current` | Fetch active monthly budget with 70%, 90%, 100% alert status |
| `POST` | `/api/budgets` | Create or update monthly budget with category-level allocations |
| `DELETE` | `/api/budgets/{id}` | Delete budget configuration |

---

## 7. Savings Goals Endpoints (`/api/savings`)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `GET` | `/api/savings` | List active savings vaults | None |
| `POST` | `/api/savings` | Create savings goal | `{ goalName, targetAmount, currentAmount, targetDate, description }` |
| `PUT` | `/api/savings/{id}` | Edit savings goal | `{ goalName, targetAmount, currentAmount, targetDate, description }` |
| `DELETE` | `/api/savings/{id}` | Delete savings vault | None |
| `POST` | `/api/savings/{id}/contributions` | Deposit money into vault | `{ amount, note }` |

---

## 8. Debt & Peer Loans Endpoints (`/api/debts`)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `GET` | `/api/debts` | List borrowed & lent debts | None |
| `POST` | `/api/debts` | Record peer loan | `{ personName, debtType, initialAmount, dueDate, description }` |
| `PUT` | `/api/debts/{id}` | Update debt record | Partial / updated fields |
| `DELETE` | `/api/debts/{id}` | Delete debt record | None |
| `POST` | `/api/debts/{id}/repayments` | Record repayment payment | `{ amount, note, repaymentDate }` |

---

## 9. Subscriptions Endpoints (`/api/subscriptions`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/subscriptions` | List all tracked subscriptions |
| `GET` | `/api/subscriptions/active` | List only active recurring plans |
| `POST` | `/api/subscriptions` | Add subscription with renewal date |
| `PUT` | `/api/subscriptions/{id}` | Update subscription details |
| `PATCH` | `/api/subscriptions/{id}/toggle` | Toggle active / paused status |
| `DELETE` | `/api/subscriptions/{id}` | Delete subscription |

---

## 10. FINOVA AI Assistant Endpoints (`/api/ai`)

| Method | Endpoint | Description | Payload |
|---|---|---|---|
| `POST` | `/api/ai/chat` | Chat with FINOVA AI (Google Gemini 2.5 Flash) | `{ message: string, conversationContext?: string }` |
| `GET` | `/api/ai/insights` | Discovered spending patterns, anomalies & suggestions | None |

---

## 11. Reports & Exports Endpoints (`/api/reports`)

| Method | Endpoint | Description | Format |
|---|---|---|---|
| `GET` | `/api/reports/summary` | Financial aggregations and savings rate | JSON |
| `GET` | `/api/reports/export/csv` | Download complete transaction ledger | CSV |
| `GET` | `/api/reports/export/excel` | Download formatted multi-sheet Excel spreadsheet | `.xlsx` (Apache POI) |
| `GET` | `/api/reports/export/pdf` | Download branded student financial statement | PDF (OpenPDF) |

---

## 12. Advanced Modules (`/api/features`)

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/features/splits` | List shared bill splits |
| `POST` | `/api/features/splits` | Create roommate bill split |
| `PATCH` | `/api/features/splits/{id}/members/{memberId}/paid` | Mark roommate paid |
| `PATCH` | `/api/features/splits/{id}/settle` | Settle entire split |
| `GET` | `/api/features/calendar` | Monthly calendar events (`?month=11&year=2026`) |
| `GET` | `/api/features/challenges` | Active savings challenges & participant progress |
| `GET` | `/api/features/journal` | Money reflection journals |
| `POST` | `/api/features/journal` | Log emotional journal entry |
| `GET` | `/api/features/predictor` | Upcoming month forecasted expenses |

---

## 13. Admin Command Center Endpoints (`/api/admin`)

| Method | Endpoint | Description | Role Required |
|---|---|---|---|
| `GET` | `/api/admin/stats` | Platform metrics (users, volume, health) | `ROLE_ADMIN` |
| `GET` | `/api/admin/users` | List all registered students | `ROLE_ADMIN` |
| `PATCH` | `/api/admin/users/{id}/toggle-status` | Suspend or reactivate student | `ROLE_ADMIN` |
| `GET` | `/api/admin/tickets` | Support ticket queue | `ROLE_ADMIN` |
| `POST` | `/api/admin/tickets/{id}/respond` | Reply and resolve support ticket | `ROLE_ADMIN` |
| `GET` | `/api/admin/audit-logs` | Security and audit events trail | `ROLE_ADMIN` |

---

## 14. Google Integration Suite Endpoints (`/api/integrations/google` & `/api/auth/google`)

| Method | Endpoint | Description | Role Required |
|---|---|---|---|
| `POST` | `/api/auth/google` | Sign In or Register student using Google OAuth 2.0 | Public |
| `GET` | `/api/integrations/google/status` | Get connection status for all Google services | Student / Admin |
| `GET` | `/api/integrations/google/connect` | Generate incremental OAuth authorization URL (`?service=CALENDAR`) | Student / Admin |
| `POST` | `/api/integrations/google/callback` | Exchange authorization code and link service | Student / Admin |
| `POST` | `/api/integrations/google/disconnect` | Disconnect specific service (`?service=CALENDAR`) | Student / Admin |
| `POST` | `/api/integrations/google/tokens/revoke` | Revoke all permissions and delete stored tokens | Student / Admin |
| `POST` | `/api/integrations/google/calendar/sync` | Trigger two-way financial deadline sync to Google Calendar | Student / Admin |
| `GET` | `/api/integrations/google/calendar/events` | List synchronized upcoming financial calendar events | Student / Admin |
| `DELETE` | `/api/integrations/google/calendar/events/{id}` | Delete synchronized Google Calendar event | Student / Admin |
| `POST` | `/api/integrations/google/sheets/export` | Export structured 7-tab financial workbook to Google Sheets | Student / Admin |
| `POST` | `/api/integrations/google/drive/upload` | Upload PDF/Excel financial report to `FINOVA/` in Drive | Student / Admin |
| `GET` | `/api/integrations/google/drive/files` | List all FINOVA financial files stored in Google Drive | Student / Admin |
| `DELETE` | `/api/integrations/google/drive/files/{id}` | Delete a financial report file record | Student / Admin |
| `POST` | `/api/integrations/google/gmail/send` | Send test or custom notification email via Gmail API | Student / Admin |
| `POST` | `/api/integrations/google/gmail/monthly-summary` | Send monthly financial summary email via Gmail | Student / Admin |
| `GET` | `/api/integrations/google/sync-history` | View recent Google service synchronization audit logs | Student / Admin |
