# CampusFix — AI Context File
Generated: 2026-10-12

---

## ✅ Features Fully Complete

### Backend (Spring Boot 3.3.4 + Java 21)
- **JWT Authentication** — Register, Login, stateless JWT tokens with 24h expiry
- **Role-Based Access Control** — STUDENT, STAFF, ADMIN, FACULTY via `@PreAuthorize`
- **Complaint Management** — Full CRUD: create, list (paginated + filtered), update, delete, status change, assign to staff
- **Complaint Upvoting** — Toggle upvote per user per complaint
- **Complaint Timeline** — Event log appended on every status change or assignment
- **Feedback System** — Students leave ratings + comments after resolution
- **Announcements** — Admin broadcasts with optional pinning
- **Category Management** — 7 seeded categories (Electrical, IT, Plumbing, Cleaning, Security, Infrastructure, Others)
- **AI Categorization** — Internal NLP keyword-based engine (no external API); optional Gemini API integration via `GEMINI_API_KEY` env var
- **AI Chatbot (RAG)** — `/api/ai/chat` endpoint with campus context
- **Dashboard Stats** — KPIs: total tickets, open, in-progress, resolved, avg resolution time, per-category breakdown
- **Database Auto-Seed** — `DatabaseSeeder` creates 7 users, 7 categories, 5 sample complaints, 2 announcements on every startup
- **H2 In-Memory DB** — Zero-config local development; H2 console at `http://localhost:8080/h2-console`
- **MySQL Profile** — Ready via `application-mysql.yml` + env vars
- **Swagger UI** — `http://localhost:8080/swagger-ui.html`
- **Async Support** — `@EnableAsync` + `AsyncConfig` configured
- **Spring Cache** — Dashboard and category endpoints cached with in-memory `simple` provider
- **Unit Tests** — 6 tests, all passing: `AuthControllerTest` (2), `AIServiceTest` (2), `ComplaintServiceTest` (2)
- **Dockerfile** — Backend containerized
- **JAR Packaged** — `backend/target/campusfix-backend-1.0.0.jar`

### Frontend (React 19 + Vite 8 + Tailwind CSS via CDN)
- **Landing Page** — Hero section, KPI stats banner, latest announcements, recent tickets
- **Login Page** — Form login + 1-click Quick Demo Login buttons for evaluator personas
- **Register Page** — Student/Faculty self-registration
- **Dashboard Page** — KPI cards, category breakdown, assigned complaint queue (staff), my tickets (student)
- **Complaints List Page** — Grid/table toggle, full-text search, status/priority/category filters, pagination, upvote button
- **New Complaint Page** — Live AI auto-categorizer, form with image URL, priority selection
- **Complaint Detail Page** — Vertical timeline, status update modal, assign staff modal, feedback modal
- **Track Ticket Page** — Public (no login) ticket tracker by ticket ID
- **Announcements Page** — Admin post + listing
- **AI Chatbot Widget** — Floating button (bottom-right), RAG chat with markdown rendering
- **Navbar** — Responsive, role-aware links, Quick Demo Login dropdown
- **Footer** — Static
- **ProtectedRoute** — Redirects unauthenticated users
- **ErrorBoundary** — Catches React rendering errors gracefully
- **AuthContext** — Global auth state, `quickLogin()` helper for demo personas
- **Axios API Client** — JWT interceptor, auto-logout on 401, all endpoint groups
- **Production Build** — `frontend/dist/` built successfully (100 modules, 1.65s)
- **Deployment Configs** — `vercel.json`, `netlify.toml`, `nginx.conf`, `Dockerfile`

### Deployment
- `docker-compose.yml` — Orchestrates MySQL 8, Spring Boot, React (Nginx)
- `render.yaml` — Render.com multi-service deployment
- `railway.json` — Railway.app deployment config

---

## 📁 Recently Modified Code Files

### Backend
| File | Status |
|------|--------|
| `backend/src/main/resources/application.yml` | NEW |
| `backend/src/main/resources/application-mysql.yml` | NEW |
| `backend/src/main/resources/logback-spring.xml` | NEW |
| `backend/src/main/java/com/campusfix/dto/*.java` | NEW — 13 DTO files |
| `backend/src/main/java/com/campusfix/exception/*.java` | NEW — 5 files |
| `backend/src/main/java/com/campusfix/security/*.java` | NEW — 4 files |
| `backend/src/main/java/com/campusfix/config/*.java` | NEW — 3 files |
| `backend/src/main/java/com/campusfix/service/*.java` | NEW — 8 files |
| `backend/src/main/java/com/campusfix/controller/*.java` | NEW — 7 files |
| `backend/src/test/java/com/campusfix/**/*Test.java` | NEW — 3 test files |
| `backend/Dockerfile` | NEW |

### Frontend
| File | Status |
|------|--------|
| `frontend/vite.config.js` | UPDATED (added `/api` proxy) |
| `frontend/index.html` | UPDATED (Tailwind CDN, fonts, branding) |
| `frontend/src/App.jsx` | REPLACED (full router setup) |
| `frontend/src/App.css` | REPLACED |
| `frontend/src/index.css` | REPLACED |
| `frontend/src/services/api.js` | NEW |
| `frontend/src/context/AuthContext.jsx` | NEW |
| `frontend/src/components/common/*.jsx` | NEW — 7 component files |
| `frontend/src/components/chat/AIChatbotWidget.jsx` | NEW |
| `frontend/src/pages/*.jsx` | NEW — 8 page files |
| `frontend/Dockerfile`, `nginx.conf`, `vercel.json`, `netlify.toml` | NEW |

---

## 🐛 Bugs Currently Under Watch

1. **`Feedback.java` — `@OneToOne` vs Schema UNIQUE**: The `Feedback` model uses a `@OneToOne` mapping on `complaint_id`. The MySQL schema has a `UNIQUE` constraint on this column. With H2, this works fine. If switching to MySQL, verify Hibernate doesn't generate a conflicting constraint or try to create a duplicate.

2. **`entity` package is empty**: The scaffolding created `com.campusfix.entity` package but all JPA models live in `com.campusfix.model`. Confirm nothing imports from `entity` — it's safe to ignore.

3. **Tailwind via CDN — Production Warning**: Tailwind CSS is loaded via CDN (`<script src="https://cdn.tailwindcss.com">`). This prints a console warning in production: *"Do not use the Tailwind CDN build in production."* For final submission, this is cosmetic — it still works. A proper PostCSS/Vite setup would eliminate this.

4. **No 404 Page**: Unknown routes fall through to a redirect `/` (catch-all route in `App.jsx`). A proper `<Route path="*" element={<NotFoundPage />}` would be more user-friendly.

5. **Email Notifications Not Wired**: `@EnableAsync` and `AsyncConfig` are configured. The `ComplaintService` has placeholder hooks for email events but no actual `JavaMailSender` or SMTP config exists. Email notifications silently do nothing.

6. **MySQL Password Unknown**: The local MySQL 8.0 service is running but root credentials are unknown. H2 is used for all development. To switch: set `SPRING_PROFILES_ACTIVE=mysql` env var and provide `MYSQL_PASSWORD`.

---

## ✅ Step-by-Step Checklist — What to Build Next

### Phase 1: Verify Runtime (Immediate)
- [ ] **Start Backend** — Run `.\mvnw.cmd spring-boot:run` from `backend/` directory; confirm port 8080 startup, H2 seed logs appear
- [ ] **Start Frontend** — Run `npm.cmd run dev` from `frontend/` directory; confirm Vite starts on port 5173
- [ ] **Test Quick Demo Login** — Confirm all 3 personas work end-to-end:
  - `admin@campusfix.edu / password123`
  - `rahul.verma@campusfix.edu / password123`
  - `staff.electrical@campusfix.edu / password123`
- [ ] **Test Complaint Flow** — Student submits complaint → Staff sees it in queue → Status update → Student sees timeline

### Phase 2: Missing Utility Files
- [ ] Create `frontend/src/utils/formatDate.js` — Date formatting helpers (`formatRelative`, `formatFull`, `formatShort`)
- [ ] Create `frontend/src/utils/cn.js` — Tailwind class merger utility
- [ ] Create `frontend/src/utils/constants.js` — Shared enums: STATUS, PRIORITY, ROLES
- [ ] Create `frontend/src/hooks/useComplaints.js` — Custom hook for complaint list with filtering
- [ ] Create `frontend/src/hooks/useDashboard.js` — Custom hook for dashboard data

### Phase 3: Missing Pages
- [ ] Create `frontend/src/pages/NotFoundPage.jsx` — Proper 404 page
- [ ] Create `frontend/src/pages/ProfilePage.jsx` — User profile: edit name, change password
- [ ] Create `frontend/src/pages/AdminUsersPage.jsx` — Admin: list/edit users, change roles
- [ ] Create `frontend/src/pages/AdminCategoriesPage.jsx` — Admin: manage categories and assigned staff

### Phase 4: Polish & Enhancement
- [ ] Add `<Route path="*" element={<NotFoundPage />} />` to `App.jsx`
- [ ] Add `/profile` route to `App.jsx` and Navbar
- [ ] Add `/admin/users` and `/admin/categories` routes to `App.jsx`
- [ ] Wire `AdminUsersPage` to existing `GET /api/admin/users` and `PUT /api/admin/users/{id}/role` endpoints
- [ ] Fix Tailwind CDN → Install Tailwind as npm package with PostCSS for production build

### Phase 5: Testing & Documentation
- [ ] Write `backend/src/test/java/com/campusfix/service/FeedbackServiceTest.java`
- [ ] Write `backend/src/test/java/com/campusfix/controller/ComplaintControllerTest.java` (integration)
- [ ] Create `README.md` in project root with setup instructions, screenshots, tech stack
- [ ] Create `.github/workflows/ci.yml` — GitHub Actions CI: build backend + frontend on push
- [ ] Add `frontend/.env.example` documenting `VITE_API_URL` env var

### Phase 6: Deployment (Optional / Post-Submission)
- [ ] Reset MySQL root password (via mysqld --skip-grant-tables) and test MySQL profile
- [ ] Deploy backend to Render.com (free tier) using `render.yaml`
- [ ] Deploy frontend to Vercel using `vercel.json`
- [ ] Configure environment variables in hosting dashboards
- [ ] Test end-to-end on deployed URLs

---

## 🔑 Key Reference

### Run Commands
```powershell
# Backend (from: campusfix/backend/)
.\mvnw.cmd spring-boot:run        # Start dev server (port 8080)
.\mvnw.cmd test                    # Run 6 unit tests
.\mvnw.cmd package -DskipTests    # Build production JAR

# Frontend (from: campusfix/frontend/)
npm.cmd run dev                    # Start Vite dev server (port 5173)
npm.cmd run build                  # Production build to dist/
npm.cmd run preview                # Preview production build
```

### Demo Credentials (password: `password123` for all)
| Email | Role |
|-------|------|
| `admin@campusfix.edu` | ADMIN |
| `staff.electrical@campusfix.edu` | STAFF |
| `staff.it@campusfix.edu` | STAFF |
| `staff.plumbing@campusfix.edu` | STAFF |
| `prof.sharma@campusfix.edu` | FACULTY |
| `rahul.verma@campusfix.edu` | STUDENT |
| `ananya.sen@campusfix.edu` | STUDENT |

### Key URLs (when running locally)
| URL | Description |
|-----|-------------|
| `http://localhost:5173` | React frontend (Vite dev) |
| `http://localhost:8080/swagger-ui.html` | API documentation |
| `http://localhost:8080/h2-console` | H2 database browser (JDBC: `jdbc:h2:mem:campusfix_db`) |
| `http://localhost:8080/actuator/health` | Backend health check |

### Project Structure
```
campusfix/
├── backend/                    # Spring Boot 3.3.4 app
│   ├── src/main/java/com/campusfix/
│   │   ├── model/              # JPA entities (10 files) — PRE-EXISTING
│   │   ├── repository/         # Spring Data repositories (7 files) — PRE-EXISTING
│   │   ├── dto/                # Request/Response DTOs (13 files)
│   │   ├── service/            # Business logic (8 files)
│   │   ├── controller/         # REST endpoints (7 files)
│   │   ├── security/           # JWT + Spring Security (4 files)
│   │   ├── config/             # App config beans (3 files)
│   │   └── exception/          # Error handling (5 files)
│   ├── src/test/               # Unit + Integration tests (3 files)
│   └── target/campusfix-backend-1.0.0.jar
├── frontend/                   # React 19 + Vite 8 app
│   ├── src/
│   │   ├── pages/              # 8 page components
│   │   ├── components/         # Reusable UI (common/, chat/)
│   │   ├── context/            # AuthContext
│   │   ├── services/           # Axios API client
│   │   ├── hooks/              # ⚠️ EMPTY — needs custom hooks
│   │   └── utils/              # ⚠️ EMPTY — needs utility files
│   └── dist/                   # Production build output
├── database/                   # MySQL DDL + seed SQL
├── docker-compose.yml
├── render.yaml
├── railway.json
└── ai_context.md               # This file
```
