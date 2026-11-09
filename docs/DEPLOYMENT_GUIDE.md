# FINOVA – Official Domain Production Deployment Guide

This guide provides the complete, step-by-step instructions for deploying FINOVA to production using your **official custom domain** (e.g., `finova.com`, `app.finova.com`, or any domain purchased from Cloudflare, GoDaddy, Namecheap, etc.).

---

## 1. Pre-Deployment Architecture

In production, FINOVA is structured for high availability, security, and live real-time synchronization:

```
[ Visitor Browser (https://yourdomain.com) ]
                 │
                 ▼
  [ Nginx Reverse Proxy / Cloudflare (Port 443 HTTPS + Let's Encrypt SSL) ]
                 │
     ┌───────────┴───────────────┐
     ▼                           ▼
[ Frontend SPA (Static / React) ] [ Spring Boot API (Port 8080) ]
                                            │
                                ┌───────────┴───────────┐
                                ▼                       ▼
                       [ MySQL 8.0 Database ]  [ Google APIs / Gemini ]
```

---

## 2. Domain & DNS Configuration

Before deploying, configure your DNS records in your domain registrar (Cloudflare, GoDaddy, Namecheap, Route 53):

| Type | Name / Host | Value / Target | TTL | Description |
| :--- | :--- | :--- | :--- | :--- |
| **A** | `@` (or root) | `YOUR_SERVER_PUBLIC_IP` | Auto / 300s | Points root domain (`yourdomain.com`) to your server |
| **A** | `www` (or CNAME) | `YOUR_SERVER_PUBLIC_IP` | Auto / 300s | Points `www.yourdomain.com` to your server |
| **A** | `api` (optional) | `YOUR_SERVER_PUBLIC_IP` | Auto / 300s | Optional dedicated API subdomain |

> **Tip**: If you use **Cloudflare**, keep the proxy status **Proxied (Orange Cloud)** for free DDoS protection, automated SSL edge certificates, and global CDN caching.

---

## 3. Google Cloud Console Configuration (OAuth & APIs)

For real Google Sign-In, Google Calendar, Sheets, and Drive to work under your official domain:

1. Go to the [Google Cloud Console](https://console.cloud.google.com/apis/credentials).
2. Select your FINOVA project and open your **OAuth 2.0 Client ID** (Web application).
3. Under **Authorized JavaScript Origins**, add:
   - `https://yourdomain.com`
   - `https://www.yourdomain.com`
4. Under **Authorized Redirect URIs**, add:
   - `https://yourdomain.com/settings/connected-apps/callback`
   - `https://www.yourdomain.com/settings/connected-apps/callback`
5. Click **Save**.

---

## 4. Production Environment Configuration (`.env`)

Create your production `.env` file on your server:

```bash
# Server Port & Domain
SERVER_PORT=8080
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com

# MySQL 8 Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/finova_db?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
SPRING_DATASOURCE_USERNAME=finova_user
SPRING_DATASOURCE_PASSWORD=YourStrongDatabasePassword123!
SPRING_PROFILES_ACTIVE=mysql

# Security & JWT Token Signing
JWT_SECRET=c2VjdXJlX2Zpbm92YV9zZWNyZXRfa2V5X2Zvcl9qd3Rfc2lnbmluZ18yMDI2X2ZpbnRlY2hfcGxhdGZvcm0=
JWT_EXPIRATION_MS=86400000
JWT_REFRESH_EXPIRATION_MS=604800000

# Google Gemini AI Integration
GEMINI_API_KEY=AIzaSyYourActualGoogleGeminiApiKey
GEMINI_MODEL=gemini-1.5-flash

# Google Integration Suite (OAuth 2.0, Calendar, Sheets, Drive, Gmail)
GOOGLE_CLIENT_ID=your-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-your-client-secret
GOOGLE_REDIRECT_URI=https://yourdomain.com/settings/connected-apps/callback
GOOGLE_TOKEN_ENCRYPTION_KEY=c2VjdXJlX2dvb2dsZV90b2tlbl9lbmNyeXB0aW9uX2tleV8yMDI2X2Zpbm92YQ==
```

---

## 5. Deployment Methods

### Option A: Cloud VPS with Docker Compose (Recommended)

This method deploys MySQL 8, the Spring Boot backend, and Nginx with automatic Let's Encrypt SSL on any Linux VPS (Ubuntu 22.04 / 24.04 on DigitalOcean, AWS EC2, Hetzner, Linode).

#### Step 1: Install Docker & Docker Compose on your server
```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y curl git ufw
curl -fsSL https://get.docker.com | sh
sudo usermod -aG docker $USER
```

#### Step 2: Configure Firewall
```bash
sudo ufw allow 22/tcp
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw enable
```

#### Step 3: Clone Repository & Configure Environment
```bash
git clone https://github.com/your-repo/finova.git /opt/finova
cd /opt/finova
cp .env.example .env
nano .env   # Update with your domain and database credentials
```

#### Step 4: Launch FINOVA with Docker Compose
```bash
docker compose up -d --build
```

#### Step 5: Setup Let's Encrypt Free SSL (HTTPS) with Certbot
```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d yourdomain.com -d www.yourdomain.com
```

Certbot will automatically install the SSL certificates and configure auto-renewal via cron.

---

### Option B: Unified Single-JAR Deployment (Simplest)

Because FINOVA includes `WebMvcSpaConfig.java` to serve the built React frontend directly from the Spring Boot JAR, you can run the entire platform as a single executable without configuring a separate frontend web server:

```bash
# 1. Build the production package
cd /opt/finova
npm --prefix frontend run build
cp -r frontend/dist/* backend/src/main/resources/static/
mvn clean package -DskipTests -f backend/pom.xml

# 2. Run as a background service via systemd
java -jar backend/target/finova-backend-1.0.0.jar --spring.profiles.active=mysql
```

---

### Option C: Cloud PaaS (Railway / Render / AWS)

If you prefer serverless cloud hosting without managing Linux servers:

1. **Database**: Provision a managed MySQL instance on **Railway**, **Aiven**, or **AWS RDS**.
2. **Backend**: Deploy the `backend/` directory to **Render Web Service** or **Railway**. Set `PORT=8080` and link the environment variables from `.env`.
3. **Frontend**: Deploy the `frontend/` directory to **Vercel** or **Netlify**. Add a custom domain in the dashboard, and configure the rewrite rule in `vercel.json` or `netlify.toml`:
   ```json
   {
     "rewrites": [
       { "source": "/api/(.*)", "destination": "https://api.yourdomain.com/api/$1" },
       { "source": "/(.*)", "destination": "/index.html" }
     ]
   }
   ```

---

## 6. Post-Deployment Verification Checklist

Once deployed with your domain, test the following:

- [ ] **HTTPS Redirection**: Verify `http://yourdomain.com` redirects automatically to `https://yourdomain.com`.
- [ ] **SSL Certificate**: Verify the browser padlock icon shows a valid certificate.
- [ ] **Authentication**: Register a new student account and log in.
- [ ] **Persistence**: Log out, restart the server, log back in, and ensure all balances and transactions remain intact.
- [ ] **Real-Time Stream**: Open two browser windows at `https://yourdomain.com/expenses` and `https://yourdomain.com/dashboard`. Add an expense in one window; verify the second window updates instantly without refreshing.
- [ ] **Google OAuth**: Test Google Sign-In and connecting Google Calendar/Sheets with your official redirect URI.
