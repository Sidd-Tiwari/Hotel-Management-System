# Hotel Management System

Production-style hotel management system built for your revised deployment plan:

- **Frontend:** React + TypeScript + Vite
- **Backend:** Spring Boot monolith with clean domain modules
- **Database:** MySQL on Railway
- **Backend hosting:** Render
- **Frontend hosting:** Vercel

## Features

### Guest / User
- OTP-based login and signup
- JWT authentication
- Browse rooms and search availability
- Apply discount codes
- Create bookings
- Mock payment flow
- Booking and payment history
- Cancel reservations

### Admin
- Manage users and roles
- Add, edit, and soft-delete rooms
- Create, edit, and delete discounts
- View bookings and payments
- Check-in / check-out actions
- Analytics dashboard

## Local development

```bash
docker compose up -d
cd backend
./mvnw spring-boot:run
```

Then in another terminal:

```bash
cd frontend
npm install
cp .env.example .env
npm run dev
```

## Core environment variables

### Frontend
```env
VITE_API_URL=http://localhost:8080
```

### Backend
```env
PORT=8080
APP_JWT_SECRET=replace-this-with-a-long-random-secret-at-least-32-characters
APP_BOOTSTRAP_ADMIN_EMAIL=admin@hotelhub.com
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,https://your-frontend.vercel.app

MYSQLHOST=localhost
MYSQLPORT=3306
MYSQLDATABASE=hotel_management
MYSQLUSER=hotel
MYSQLPASSWORD=hotel

APP_MAIL_MOCK_MODE=true
SPRING_MAIL_HOST=localhost
SPRING_MAIL_PORT=1025
```

## Deployment notes

- Deploy `frontend/` to Vercel
- Deploy `backend/` to Render
- Create MySQL on Railway and copy the Railway MySQL variables into Render

Documentation:
- `docs/database-schema.md`
- `docs/api-overview.md`
- `docs/deployment-notes.md`

This package contains source code and deployment config, but not live cloud deployments.
