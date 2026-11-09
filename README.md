# FINOVA – Smart Money. Smarter Future.

> **AI-Powered Student Personal Finance Management Platform**
> Built with React 18, TypeScript, Tailwind CSS, Spring Boot 3, Java 21, MySQL 8, and Google Gemini AI.

---

## 1. Project Overview
**FINOVA** is a full-stack fintech platform engineered specifically for college students to track personal spending, pocket money, income streams, monthly budgets, savings goals, peer borrowings, and recurring subscriptions.

Unlike generic corporate finance applications, FINOVA is tailored around the student financial lifestyle—campus canteens, shared flat bills, pocket money stipends, student discounts, and friendly peer debts.

### Main Objectives
- **Understand Cash Flows**: Real-time visibility into allowance and spending breakdowns.
- **Prevent Overspending**: 3-tier budget alert thresholds (70% caution, 90% high warning, 100% exceeded).
- **Target Savings Milestones**: Dedicated vaults for laptops, college trips, certifications, and emergency funds.
- **Manage Peer Debts**: Track borrowed and lent money with partial repayments and zero awkwardness.
- **AI Financial Coaching**: Powered by Google Gemini to analyze habits and provide non-judgmental guidance.
- **Objective Health Score**: 0–100 financial health grading with actionable improvement steps.

---

## 2. Technology Stack

### Frontend
- **Framework**: React 18 + Vite + TypeScript
- **Styling**: Tailwind CSS (Premium light theme: `#FFFFFF`, `#FAFAFC`, `#9B87F5` lavender, `#A8C9B5` sage)
- **Icons**: Lucide React
- **Visualizations**: Recharts (Area, Bar, Pie, Line, Gauges)
- **Routing & Forms**: React Router 6, React Hook Form, Zod
- **Networking**: Axios with automatic JWT refresh interceptors

### Backend
- **Language**: Java 21 / 27
- **Framework**: Spring Boot 3.3.5
- **Security**: Spring Security 6, Stateless JWT Authentication, BCrypt Hashing, Role-Based Access Control
- **Persistence**: Spring Data JPA, Hibernate, MySQL 8 (with automatic in-memory fallback)
- **Reporting**: Apache POI (Excel `.xlsx`), OpenPDF (Formatted Financial Statements), OpenCSV
- **AI Integration**: Google Gemini 2.5 Flash API with rule-engine fallback
- **Documentation**: Springdoc OpenAPI / Swagger UI

---

## 3. Architecture & Project Structure

```
FINOVA/
├── frontend/                  # React + TypeScript + Vite SPA
│   ├── src/
│   │   ├── api/ & services/   # Axios API services with JWT refresh interceptors
│   │   ├── components/        # Common Navbar, Sidebar, modals, metric cards
│   │   ├── contexts/          # AuthContext (login, register, profile)
│   │   ├── layouts/           # AppLayout with dynamic header
│   │   ├── pages/             # 16 complete financial modules & admin pages
│   │   ├── styles/            # Tailwind CSS design system
│   │   ├── types/             # Full TypeScript interfaces & DTOs
│   │   └── utils/             # Currency formatters, date utilities
│   ├── package.json
│   └── vite.config.ts
│
├── backend/                   # Spring Boot 3 REST Application
│   ├── src/main/java/com/finova/
│   │   ├── config/            # Security, CORS, Swagger, Data Initializer
│   │   ├── controller/        # 13 REST Controllers
│   │   ├── dto/               # Request/Response DTOs & clean builders
│   │   ├── entity/            # 24 Normalized JPA Entities
│   │   ├── repository/        # 22 Spring Data JPA Repositories
│   │   ├── security/          # JwtTokenProvider, Filter, UserPrincipal
│   │   └── service/           # Business logic, POI Excel, OpenPDF, Gemini AI
│   ├── src/main/resources/    # application.yml, database migration
│   └── src/test/              # JUnit 5 Calculation & Architecture Tests
│
├── database/
│   ├── schema.sql             # 24 MySQL 8 tables with DECIMAL(12,2) math
│   └── seed.sql               # Seed data for demo students, categories, budgets
│
├── docs/
│   ├── DATABASE.md            # ERD diagrams, table definitions, indexing
│   ├── API_DOCUMENTATION.md   # Complete REST endpoint catalog
│   └── SETUP_GUIDE.md         # Local development step-by-step instructions
│
├── docker-compose.yml         # Containerized MySQL 8, Backend, and Frontend
├── .env.example               # Environment variables template
└── README.md                  # Master documentation
```

---

## 4. Key Features Walkthrough

1. **Conversion Landing Page**:
   Hero showcase, student feature highlights, 3-step workflow, demo student testimonials, and interactive FAQ.
2. **Student Dashboard**:
   6 dynamic summary cards with delta comparisons, 5 Recharts visualizations, date range filters (Today, This Week, This Month, Last Month, This Year), and Financial Health Score banner.
3. **Income Tracking**:
   Manage pocket money, stipends, freelancing, and scholarships with recurring inflow toggles.
4. **Expense Management & Receipt Scanner**:
   Categorized outflows with payment method tags (UPI, Cash, Debit, Credit) and AI bill extraction.
5. **Smart Budget Planner**:
   Total monthly budget with category allocations and proactive 70%, 90%, and 100% threshold alert badges.
6. **Savings Vaults**:
   Target-based vaults (MacBook, Vacation, Emergency buffer) with visual progress bars and instant deposit shortcuts.
7. **Debt & Peer Loans**:
   Track borrowed and lent money with partial repayments, due date countdowns, and net balance calculation.
8. **Subscription Radar**:
   Track Netflix, Spotify, Prime, and student memberships with days-until-renewal alerts and active toggles.
9. **FINOVA AI Assistant**:
   Chat with Google Gemini 2.5 Flash for personalized spending advice, anomaly alerts, and educational coaching.
10. **Financial Analytics & Export**:
    Visual spending trends and direct one-click downloads for CSV, Excel (`.xlsx`), and PDF statements.
11. **Advanced Suite**:
    - Roommate Bill Splitter (Equal/Exact splits with member payment tracking).
    - Financial Calendar (Highlighting renewals, debt dues, and allowance dates).
    - Savings Challenges (Gamified milestones with badges).
    - Money Reflection Journal (Daily emotion and impulse tracking).
    - Future Expense Predictor (Next-month outflow forecast).
12. **Google Integration Suite (`/settings/connected-apps`)**:
    - **Google OAuth 2.0**: Incremental authorization with AES-256-GCM token encryption at rest.
    - **Google Calendar**: 2-way sync of loan due dates, subscription renewals, savings deadlines, and budget reviews with stored event ID duplicate prevention.
    - **Google Sheets**: Automated 7-tab student financial workbook export.
    - **Google Drive**: Hierarchical PDF and Excel statement cloud backups under `FINOVA/`.
    - **Gmail Alerts**: Send-only RFC 2822 financial performance digests.
    - **Google Maps**: Campus and merchant geolocation hotspot visualization.
13. **Administrator Command Center**:
    System volume metrics, student management table with suspension controls, support tickets queue, and security audit logs.

---

## 5. Quick Start Guide

### 1. Build and Run Backend
```bash
cd backend
# Using bundled portable Maven (Windows):
..\tools\apache-maven-3.9.9\bin\mvn.cmd clean package -DskipTests
..\tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run
```
The backend starts at `http://localhost:8080`.

### 2. Build and Run Frontend
```bash
cd frontend
npm install
npm run dev
```
The frontend starts at `http://localhost:5173`.

---

## 6. Pre-Configured Test Accounts

| Role | Email | Password |
|---|---|---|
| **Demo Student** | `student@finova.com` | `Student@1234` |
| **Demo Admin** | `admin@finova.com` | `Admin@1234` |

*Both credentials can also be autofilled with one click using the demo buttons on the `/login` page.*
