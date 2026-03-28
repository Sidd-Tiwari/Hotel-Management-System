# Database Schema

```mermaid
erDiagram
    USERS ||--o{ BOOKINGS : places
    ROOMS ||--o{ BOOKINGS : assigned_to
    DISCOUNTS ||--o{ BOOKINGS : applied_to
    BOOKINGS ||--|| PAYMENTS : has
    USERS ||--o{ OTPS : verifies
```

Core tables:
- `users`
- `otps`
- `rooms`
- `discounts`
- `bookings`
- `payments`

Highlights:
- OTPs are stored as hashes
- bookings reference users, rooms, and optional discounts
- payments keep a unique one-to-one booking relationship
- indexes support room filtering, booking conflict checks, and payment lookups
