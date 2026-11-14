# FINOVA Backend – Render Production Deployment Guide

This document describes how to deploy the FINOVA Spring Boot backend to **Render** as a production Web Service using Docker, and link it with your **Vercel** frontend.

---

## 1. Prerequisites
- A [Render](https://render.com) account.
- Your remote MySQL database connection details (Aiven, Railway, AWS RDS, or Render PostgreSQL/MySQL).
- Your Google Cloud OAuth 2.0 Client ID and Secret.
- Your deployed Vercel frontend URL (e.g. `https://finova.vercel.app`).

---

## 2. Render Web Service Setup

1. In the Render Dashboard, click **New +** → **Web Service**.
2. Connect your GitHub repository (`FINOVA---Smart-Money`).
3. Fill in the deployment details:
   - **Name**: `finova-backend`
   - **Region**: Choose the region closest to your users or database (e.g., *Singapore*, *Oregon*, *Frankfurt*).
   - **Branch**: `main`
   - **Root Directory**: `backend` *(Critical: tells Render to build inside the backend folder)*
   - **Runtime**: **Docker**
   - **Instance Type**: Starter / Standard (at least 512MB RAM, recommended 1GB RAM).

---

## 3. Render Health Check Path

In Render service settings:
- **Health Check Path**: `/api/health` *(or `/actuator/health`)*

Both endpoints are configured in Spring Boot and Spring Security without requiring authentication.

---

## 4. Production Environment Variables (Render)

Under **Environment Variables** in your Render Web Service dashboard, configure:

| Variable Name | Example / Format | Required | Description |
| :--- | :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `prod` | **Yes** *(Default in Docker)* | Activates MySQL production profile with Flyway & HikariCP |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://<external-host>:<port>/<dbname>?useSSL=true&serverTimezone=UTC` | **Yes** | Remote managed MySQL JDBC connection string (Do NOT use localhost) |
| `SPRING_DATASOURCE_USERNAME` | `<db-user>` | **Yes** | Production database username |
| `SPRING_DATASOURCE_PASSWORD` | `<db-password>` | **Yes** | Production database password |
| `DATABASE_URL` | `mysql://<user>:<password>@<host>:<port>/<dbname>` | Alternative | Supported cloud URI format (automatically converted to JDBC) |
| `FINOVA_JWT_SECRET` | *(256-bit base64 / hex key)* | **Yes** | Secret for signing JWT authentication tokens |
| `CORS_ALLOWED_ORIGINS` | `https://<your-vercel-domain>,http://localhost:8080` | **Yes** | Whitelisted frontend origins |
| `GOOGLE_CLIENT_ID` | `17334748765-....apps.googleusercontent.com` | **Yes** | Google OAuth Client ID |
| `GOOGLE_CLIENT_SECRET` | `GOCSPX-...` | **Yes** | Google OAuth Client Secret |
| `GOOGLE_REDIRECT_URI` | `https://<your-vercel-domain>/settings/connected-apps/callback` | **Yes** | Production Google OAuth redirect URI |
| `SPRING_MAIL_HOST` | `smtp.gmail.com` *(or SendGrid/Resend)* | Optional | SMTP Host for Email OTP verification |
| `SPRING_MAIL_PORT` | `587` | Optional | SMTP Port |
| `SPRING_MAIL_USERNAME` | `your-email@gmail.com` | Optional | SMTP Email address |
| `SPRING_MAIL_PASSWORD` | `your-app-password` | Optional | SMTP 16-character App Password |
| `SPRING_MAIL_FROM` | `noreply@yourdomain.com` | Optional | From email address |
| `GEMINI_API_KEY` | `AIzaSy...` | Optional | Powers FINOVA AI mentor chat |
| `UPLOAD_DIR` | `/var/data/uploads` | Optional | Directory for durable uploaded files |

---

## 5. Persistent Disk (Durable File Uploads)

If storing receipts or exported files on the backend:
1. In Render Web Service settings, go to the **Disks** section.
2. Click **Add Disk**:
   - **Name**: `finova-data`
   - **Mount Path**: `/var/data`
   - **Size**: 1 GB (or as needed)
3. The Dockerfile automatically stores uploads at `/var/data/uploads` which persists across container restarts and redeployments.

---

## 6. Linking to Vercel Frontend

Once Render deploys your backend (e.g. `https://finova-backend.onrender.com`):

1. Go to your **Vercel Dashboard** → your FINOVA project → **Settings** → **Environment Variables**.
2. Add:
   - **Key**: `VITE_API_BASE_URL`
   - **Value**: `https://<your-render-app-name>.onrender.com/api`
3. Click **Save** and trigger a **Redeploy** on Vercel.

---

## 7. Google Cloud Console Callback Configuration

In [Google Cloud Console → Credentials](https://console.cloud.google.com/apis/credentials):
1. Open your OAuth 2.0 Web Client ID.
2. Add to **Authorized JavaScript origins**:
   - `https://<your-vercel-domain>`
   - `http://localhost:8080`
3. Add to **Authorized redirect URIs**:
   - `https://<your-vercel-domain>/settings/connected-apps/callback`
   - `http://localhost:8080/settings/connected-apps/callback`
4. Click **Save**.
