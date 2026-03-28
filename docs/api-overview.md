# API Overview

Swagger:
- `/swagger-ui/index.html`

Public:
- `POST /api/auth/request-otp`
- `POST /api/auth/verify-otp`
- `GET /api/public/rooms`
- `GET /api/public/rooms/{id}`
- `GET /api/public/offers`
- `GET /api/public/discounts/validate`
- `GET /api/public/bookings/search-availability`

User:
- `GET /api/auth/me`
- `POST /api/bookings`
- `GET /api/bookings/me`
- `DELETE /api/bookings/{id}`
- `GET /api/payments/me`
- `GET /api/payments/booking/{bookingId}`
- `POST /api/payments/{id}/mock-success`
- `POST /api/payments/{id}/mock-failure`

Admin:
- `GET /api/admin/users`
- `PATCH /api/admin/users/{id}/role`
- `GET /api/admin/rooms`
- `POST /api/admin/rooms`
- `PUT /api/admin/rooms/{id}`
- `DELETE /api/admin/rooms/{id}`
- `GET /api/admin/discounts`
- `POST /api/admin/discounts`
- `PUT /api/admin/discounts/{id}`
- `DELETE /api/admin/discounts/{id}`
- `GET /api/admin/bookings`
- `PATCH /api/admin/bookings/{id}/check-in`
- `PATCH /api/admin/bookings/{id}/check-out`
- `GET /api/admin/payments`
- `GET /api/admin/analytics`
