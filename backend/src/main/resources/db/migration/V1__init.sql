CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    role VARCHAR(20) NOT NULL,
    verified BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE otps (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(160) NOT NULL,
    otp_hash VARCHAR(255) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME(6) NOT NULL
);

CREATE TABLE rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_number VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    type VARCHAR(20) NOT NULL,
    price_per_night DECIMAL(10,2) NOT NULL,
    capacity INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    image_url VARCHAR(500),
    description VARCHAR(2000) NOT NULL,
    amenities VARCHAR(1000) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE discounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(40) NOT NULL UNIQUE,
    percentage DECIMAL(5,2) NOT NULL,
    minimum_booking_amount DECIMAL(10,2) NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    description VARCHAR(300),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL
);

CREATE TABLE bookings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    discount_id BIGINT NULL,
    guests INT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    base_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(30) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_room FOREIGN KEY (room_id) REFERENCES rooms(id),
    CONSTRAINT fk_bookings_discount FOREIGN KEY (discount_id) REFERENCES discounts(id)
);

CREATE TABLE payments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    booking_id BIGINT NOT NULL UNIQUE,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    method VARCHAR(50) NOT NULL,
    transaction_ref VARCHAR(120) NOT NULL UNIQUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_otps_email ON otps(email);
CREATE INDEX idx_otps_expires_at ON otps(expires_at);
CREATE INDEX idx_rooms_type ON rooms(type);
CREATE INDEX idx_rooms_price ON rooms(price_per_night);
CREATE INDEX idx_rooms_active ON rooms(active);
CREATE INDEX idx_discounts_active ON discounts(active);
CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_room ON bookings(room_id);
CREATE INDEX idx_bookings_status ON bookings(status);
CREATE INDEX idx_bookings_dates ON bookings(check_in_date, check_out_date);
CREATE INDEX idx_payments_status ON payments(status);

INSERT INTO rooms (room_number, name, type, price_per_night, capacity, active, image_url, description, amenities, created_at, updated_at) VALUES
('101', 'Garden Standard', 'STANDARD', 3200.00, 2, true, 'https://images.unsplash.com/photo-1566665797739-1674de7a421a?w=1200', 'A comfortable standard room with garden-facing windows and work desk.', 'WiFi,AC,TV,Breakfast,Work Desk', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
('201', 'City Deluxe', 'DELUXE', 4800.00, 2, true, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=1200', 'Premium deluxe room with upgraded interiors and city views.', 'WiFi,AC,TV,Mini Bar,Breakfast', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
('301', 'Executive Suite', 'SUITE', 7600.00, 3, true, 'https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=1200', 'Spacious suite ideal for business travel and premium stays.', 'WiFi,AC,TV,Living Area,Breakfast,Work Desk', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
('401', 'Family Comfort', 'FAMILY', 6900.00, 4, true, 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=1200', 'Family room with extra bedding and wider seating area.', 'WiFi,AC,TV,Breakfast,Extra Bedding', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
('501', 'Corporate Executive', 'EXECUTIVE', 8300.00, 2, true, 'https://images.unsplash.com/photo-1445019980597-93fa8acb246c?w=1200', 'Executive room curated for frequent corporate travelers.', 'WiFi,AC,TV,Work Desk,Airport Pickup', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
('601', 'Luxury Panorama Suite', 'SUITE', 11200.00, 4, true, 'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=1200', 'Top-tier suite with panoramic view, lounge seating, and premium amenities.', 'WiFi,AC,TV,Living Area,Jacuzzi,Breakfast', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));

INSERT INTO discounts (code, percentage, minimum_booking_amount, expires_at, active, description, created_at, updated_at) VALUES
('WELCOME10', 10.00, 3000.00, DATE_ADD(UTC_TIMESTAMP(6), INTERVAL 120 DAY), true, 'Welcome offer for new guests', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
('SUMMER15', 15.00, 5000.00, DATE_ADD(UTC_TIMESTAMP(6), INTERVAL 90 DAY), true, 'Seasonal summer campaign', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6)),
('FAMILY20', 20.00, 8000.00, DATE_ADD(UTC_TIMESTAMP(6), INTERVAL 150 DAY), true, 'Family stay special', UTC_TIMESTAMP(6), UTC_TIMESTAMP(6));
