# FINOVA – Local Development & Setup Guide

This guide details how to install, configure, build, and run FINOVA on your machine.

---

## 1. Prerequisites
- **Java**: JDK 21 or later (`java -version`)
- **Maven**: Portable Maven 3.9.9 included in `./tools/apache-maven-3.9.9` or system Maven (`mvn -v`)
- **Node.js**: v18+ or v20+ with npm (`node -v`, `npm -v`)
- **MySQL**: MySQL 8.0 server (Optional: an embedded H2 database is also auto-configured if MySQL is offline)
- **Docker & Docker Compose**: Optional for containerized deployment

---

## 2. Database Setup (MySQL 8)

### Option A: Local MySQL Server
1. Start your local MySQL service:
   ```bash
   mysql -u root -p
   ```
2. Create database and apply schemas:
   ```sql
   CREATE DATABASE IF NOT EXISTS finova_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   USE finova_db;
   SOURCE database/schema.sql;
   SOURCE database/seed.sql;
   ```

### Option B: Zero-Config In-Memory Fallback
If MySQL is not running on your host, the Spring Boot backend defaults cleanly to an in-memory database with identical JPA entity bindings and auto-seeds all demo data on startup.

---

## 3. Backend Setup & Run (Spring Boot 3)

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```
2. Copy environment file (optional):
   ```bash
   cp ../.env.example .env
   ```
3. Build the application:
   - On Windows using bundled portable Maven:
     ```cmd
     ..\tools\apache-maven-3.9.9\bin\mvn.cmd clean package -DskipTests
     ```
   - On Linux / macOS:
     ```bash
     mvn clean package -DskipTests
     ```
4. Run the backend service:
   ```cmd
   ..\tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run
   ```
   Or execute the packaged executable JAR:
   ```bash
   java -jar target/finova-backend-1.0.0.jar
   ```

The backend server starts on **`http://localhost:8080`**.
- OpenAPI / Swagger: **`http://localhost:8080/swagger-ui.html`**
- Health Probe: **`http://localhost:8080/actuator/health`**

---

## 4. Frontend Setup & Run (React + Vite)

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the Vite development server:
   ```bash
   npm run dev
   ```
4. Build for production:
   ```bash
   npm run build
   ```

The frontend runs on **`http://localhost:5173`** (or next available port).
API requests to `/api/*` are automatically proxied to `http://localhost:8080`.

---

## 5. Demo Sign-in Credentials

The database automatically seeds test accounts on first launch:

| Role | Email | Password | Access Scope |
|---|---|---|---|
| **Demo Student** | `student@finova.com` | `Student@1234` | Full access to personal finances, AI assistant, budgets, goals |
| **Demo Admin** | `admin@finova.com` | `Admin@1234` | Administrator Command Center, User Management, Ticket Resolutions |

Both accounts can also be instantly auto-filled via the **One-Click Quick Demo Sign In** pills on the `/login` page.
