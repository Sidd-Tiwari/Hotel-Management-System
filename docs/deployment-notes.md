# Deployment Notes

## Railway MySQL
Provision a MySQL service and copy:
- `MYSQLHOST`
- `MYSQLPORT`
- `MYSQLDATABASE`
- `MYSQLUSER`
- `MYSQLPASSWORD`

## Render backend
Deploy the `backend` directory using the included Dockerfile.

Required environment variables:
```env
PORT=8080
APP_JWT_SECRET=replace-with-long-random-secret
APP_BOOTSTRAP_ADMIN_EMAIL=admin@yourhotel.com
APP_CORS_ALLOWED_ORIGINS=https://your-project.vercel.app
MYSQLHOST=...
MYSQLPORT=...
MYSQLDATABASE=...
MYSQLUSER=...
MYSQLPASSWORD=...
```

Optional mail:
```env
APP_MAIL_MOCK_MODE=false
SPRING_MAIL_HOST=...
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=...
SPRING_MAIL_PASSWORD=...
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_SMTP_STARTTLS_ENABLE=true
```

## Vercel frontend
Deploy the `frontend` directory and set:
```env
VITE_API_URL=https://your-render-backend.onrender.com
```
