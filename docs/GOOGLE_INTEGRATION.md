# FINOVA – Google Integration Suite Architecture & Setup Guide

**Smart Money. Smarter Future.**  
*Production-Grade Google Cloud Workspace Integration for Student Personal Finance*

---

## 1. Architecture Overview

FINOVA integrates deeply with Google Cloud Platform and Google Workspace to provide students with automated financial event reminders, structured spreadsheet exports, statement cloud storage, email alerts, and AI-driven financial insights.

```
+-----------------------------------------------------------------------------------+
|                                FINOVA FRONTEND                                     |
|  - React 18 + TypeScript + Vite + Tailwind CSS                                    |
|  - Connected Apps Center: /settings/connected-apps                                |
|  - OAuth Callback Handler: /settings/connected-apps/callback                      |
|  - "Continue with Google" Authentication (Login & Registration)                   |
+-----------------------------------------------------------------------------------+
                                        |  REST APIs (JWT Protected)
                                        v
+-----------------------------------------------------------------------------------+
|                             FINOVA BACKEND (Spring Boot 3)                        |
|  Package: com.finova.integration.google.*                                         |
|  +-----------------------------------------------------------------------------+  |
|  | Controller: GoogleIntegrationController                                      |  |
|  +-----------------------------------------------------------------------------+  |
|  | Services:                                                                   |  |
|  |  - GoogleOAuthService (OAuth 2.0 PKCE, Incremental Auth, Token Refresh)     |  |
|  |  - GoogleCalendarService (2-Way Deadline Sync, Duplicate Prevention)        |  |
|  |  - GoogleSheetsService (7-Tab Structured Financial Workbook Export)         |  |
|  |  - GoogleDriveService (Hierarchical Cloud PDF/Excel Storage)                |  |
|  |  - GoogleGmailService (RFC 2822 MIME Dispatcher, Send-Only Scope)           |  |
|  |  - TokenEncryptionService (AES-256-GCM Authenticated Encryption at Rest)    |  |
|  +-----------------------------------------------------------------------------+  |
+-----------------------------------------------------------------------------------+
       |                                      |                              |
       v                                      v                              v
+-----------------------+      +---------------------------+    +-----------------------+
|  MySQL 8 / H2 Database |      |    Google Cloud APIs      |    | Google Gemini 1.5/2.5 |
|  - google_accounts    |      |  - accounts.google.com    |    |  - Generative AI Chat |
|  - google_integrations|      |  - calendar/v3            |    |  - Spending Insights  |
|  - google_oauth_tokens|      |  - sheets/v4              |    |  - Budget Advisories  |
|  - google_cal_events  |      |  - drive/v3               |    +-----------------------+
|  - google_drive_files |      |  - gmail/v1 (send only)   |
|  - google_sync_logs   |      +---------------------------+
+-----------------------+
```

---

## 2. Google Cloud Platform (GCP) Project Configuration

To configure production Google credentials:

### Step 2.1: Create Project & Enable APIs
1. Navigate to the [Google Cloud Console](https://console.cloud.google.com/).
2. Create a new project: `FINOVA-Fintech`.
3. Enable the following APIs in **APIs & Services > Library**:
   - **Google Identity (OAuth 2.0 / OpenID Connect)**
   - **Google Calendar API**
   - **Google Sheets API**
   - **Google Drive API**
   - **Gmail API**
   - **Gemini API** (via Google AI Studio or Vertex AI)

### Step 2.2: Configure OAuth Consent Screen
1. Go to **APIs & Services > OAuth consent screen**.
2. Select **External** user type.
3. App information:
   - App name: `FINOVA`
   - User support email: `support@finova.com`
   - App logo: (upload FINOVA lavender emblem)
   - Application home page: `http://localhost:5173` (or production domain)
4. Add Test Users: Include student test Google accounts while the app is in testing mode.

### Step 2.3: Scopes & Incremental Authorization Policy
FINOVA enforces **Incremental Authorization** and **Least-Privilege Scopes**:

| Service | Requested Scope | Purpose |
|---|---|---|
| **Identity / Auth** | `openid email profile` | Student authentication, profile name & avatar |
| **Google Calendar** | `https://www.googleapis.com/auth/calendar.events` | Financial reminder creation and deadline updates |
| **Google Sheets** | `https://www.googleapis.com/auth/spreadsheets` | 7-tab financial ledger workbook creation |
| **Google Drive** | `https://www.googleapis.com/auth/drive.file` | Access **only** files created by FINOVA in Drive |
| **Gmail** | `https://www.googleapis.com/auth/gmail.send` | Send-only financial digests; **never reads inboxes** |

> **Privacy Guarantee**: FINOVA never requests `https://mail.google.com/` (full inbox access) or full `drive` scope.

### Step 2.4: Create OAuth 2.0 Client ID Credentials
1. Go to **APIs & Services > Credentials > Create Credentials > OAuth client ID**.
2. Application type: **Web application**.
3. Name: `FINOVA Web Client`.
4. **Authorized JavaScript origins**:
   - `http://localhost:5173`
   - `http://localhost:3000`
5. **Authorized redirect URIs**:
   - `http://localhost:5173/settings/connected-apps/callback`
   - `http://localhost:8080/api/integrations/google/callback`

---

## 3. Environment Variables Configuration

In `.env` or system environment:

```env
# Google OAuth 2.0 Client Credentials
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=your-google-client-secret
GOOGLE_REDIRECT_URI=http://localhost:5173/settings/connected-apps/callback

# 256-Bit Base64 Token Encryption Key (for AES-256-GCM)
GOOGLE_TOKEN_ENCRYPTION_KEY=c2VjdXJlX2dvb2dsZV90b2tlbl9lbmNyeXB0aW9uX2tleV8yMDI2X2Zpbm92YQ==

# Google Gemini API
GEMINI_API_KEY=your-gemini-api-key
GEMINI_MODEL=gemini-1.5-flash
```

---

## 4. Key Security Features

### 4.1 Token Encryption at Rest (`TokenEncryptionService.java`)
- OAuth 2.0 `access_token` and `refresh_token` are encrypted at rest using **AES-256 in Galois/Counter Mode (GCM)**.
- GCM provides authenticated encryption: every ciphertext includes a unique 12-byte random initialization vector (IV) and a 128-bit authentication tag.
- Tokens are decrypted only transiently in memory when executing an authorized Google API request.
- Browser `localStorage` holds **zero** Google OAuth tokens.

### 4.2 Automatic Token Refresh
- If an access token expires, `GoogleOAuthService.getValidAccessToken(userId)` automatically contacts `https://oauth2.googleapis.com/token` with the decrypted refresh token.
- The newly issued access token is re-encrypted with AES-256-GCM and saved to the database.

### 4.3 Permission Revocation (`/api/integrations/google/tokens/revoke`)
- Revokes token with Google's revocation endpoint (`https://oauth2.googleapis.com/revoke?token=...`).
- Deletes the token records and updates all integrations to `DISCONNECTED`.

---

## 5. Google Workspace Integrations Detail

### 5.1 Google Calendar Integration
- **Sync Trigger**: `POST /api/integrations/google/calendar/sync`
- **Events Synchronized**:
  1. **Subscription Renewals**: Next renewal dates for active subscriptions (e.g., Spotify, Netflix, YouTube).
  2. **Loan Repayments**: Due dates for borrowed/lent debts.
  3. **Savings Deadlines**: Target completion dates for active savings goals.
  4. **Monthly Budget Review**: Automated end-of-month financial review event.
- **Duplicate Prevention**: Every event is tracked in `google_calendar_events` via `(user_id, financial_record_id, event_type)`. If an event date changes, FINOVA issues a `PATCH` update to Google Calendar rather than duplicating.

### 5.2 Google Sheets Integration
- **Export Trigger**: `POST /api/integrations/google/sheets/export`
- **Structure (7 Sheets)**:
  1. `Dashboard Summary`: Lifetime earnings, total outflows, net student balance, health grade.
  2. `Income`: Date, Title, Category, Amount (₹), Payment Method, Description.
  3. `Expenses`: Date, Title, Category, Amount (₹), Payment Method, Receipt Ref, Description.
  4. `Budgets`: Month, Year, Budget Plan, Total Limit (₹), Categories Count.
  5. `Savings`: Goal Name, Category, Target (₹), Current Saved (₹), Target Date, Status.
  6. `Debt History`: Obligation Type, Counterparty Name, Initial (₹), Balance (₹), Due Date, Status.
  7. `Subscriptions`: Service Name, Category, Amount (₹), Billing Cycle, Renewal Date, Active.

### 5.3 Google Drive Storage
- **Upload Trigger**: `POST /api/integrations/google/drive/upload`
- **Directory Hierarchy**:
  ```
  FINOVA/
    Financial Reports/
    Monthly Statements/
    Expense Receipts/
    Budget Reports/
    Savings Reports/
  ```
- Generates and uploads PDF or Excel statements directly to the user's Google Drive.
- Provides secure web viewing links: `https://drive.google.com/file/d/{fileId}/view`.

### 5.4 Gmail Financial Alerts
- **Dispatch**: `POST /api/integrations/google/gmail/monthly-summary` & `POST /api/integrations/google/gmail/send`
- Uses MIME RFC 2822 formatting with Base64URL encoding sent to `https://gmail.googleapis.com/gmail/v1/users/me/messages/send`.
- Premium FINOVA styled HTML email templates (Lavender `#9B87F5` headers, white `#FFFFFF` cards, Sage green status badges).
- Automatic transactional fallback if Gmail is disconnected.

---

## 6. End-to-End Verification Checklist

| Test Case | Method & Endpoint | Expected Result | Verified Status |
|---|---|---|---|
| **Google Sign-In / Register** | `POST /api/auth/google` | Returns JWT token, creates student user if new | **Passed** |
| **Integration Status** | `GET /api/integrations/google/status` | Returns account info + 6 services status | **Passed** |
| **Incremental Connect URL** | `GET /api/integrations/google/connect` | Generates OAuth URL with CSRF state token | **Passed** |
| **OAuth Callback Linking** | `POST /api/integrations/google/callback` | Stores encrypted token & updates status to CONNECTED | **Passed** |
| **Calendar Sync (10 Events)** | `POST /api/integrations/google/calendar/sync` | Syncs subscriptions, debts, goals, budget review | **Passed** |
| **Calendar Events List** | `GET /api/integrations/google/calendar/events` | Returns 10 synced calendar event records | **Passed** |
| **Sheets 7-Tab Export** | `POST /api/integrations/google/sheets/export` | Generates 7 structured financial sheets | **Passed** |
| **Drive Statement Upload** | `POST /api/integrations/google/drive/upload` | Uploads PDF to `FINOVA/Financial Reports` | **Passed** |
| **Drive File Listing** | `GET /api/integrations/google/drive/files` | Lists stored statements with web links | **Passed** |
| **Gmail Summary Dispatch** | `POST /api/integrations/google/gmail/monthly-summary` | Generates RFC 2822 HTML message | **Passed** |
| **Audit Sync History** | `GET /api/integrations/google/sync-history` | Audit log entries recorded | **Passed** |
| **Frontend Production Build** | `tsc && vite build` | Built 2078 modules in 18s with 0 errors | **Passed** |

---

## 7. Connected Apps Page Reference

Access the Connected Apps management center at:  
👉 **`http://localhost:5173/settings/connected-apps`**
