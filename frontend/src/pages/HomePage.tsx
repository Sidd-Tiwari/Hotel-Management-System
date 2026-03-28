import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import toast from "react-hot-toast";
import { api } from "../lib/api";
import type { Discount, Room } from "../types";
import { RoomCard } from "../components/RoomCard";
import { LoadingSpinner } from "../components/LoadingSpinner";

export default function HomePage() {
  const [rooms, setRooms] = useState<Room[]>([]);
  const [offers, setOffers] = useState<Discount[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([api<Room[]>("/api/public/rooms"), api<Discount[]>("/api/public/offers")])
      .then(([roomData, offerData]) => {
        setRooms(roomData.slice(0, 3));
        setOffers(offerData);
      })
      .catch((error: Error) => toast.error(error.message))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner label="Loading hotel highlights..." />;

  return (
    <div className="container page-stack">
      <section className="hero">
        <div>
          <span className="badge soft">Smart hospitality experience</span>
          <h1>Book premium stays with a fast, secure, and elegant hotel workflow.</h1>
          <p>
            Browse beautiful rooms, verify access by OTP, apply offers, complete payment,
            and manage reservations from responsive guest and admin dashboards.
          </p>
          <div className="hero-actions">
            <Link className="primary-button" to="/rooms">
              Explore rooms
            </Link>
            <Link className="secondary-button" to="/auth">
              Login / Signup
            </Link>
          </div>
        </div>
        <div className="hero-card">
          <h3>Why this system works</h3>
          <ul className="hero-list">
            <li>Responsive experience for mobile, tablet, and desktop</li>
            <li>Room discovery by budget, category, and availability dates</li>
            <li>Discounts, bookings, payments, and stay history in one flow</li>
            <li>Admin controls for rooms, offers, and hotel performance metrics</li>
          </ul>
        </div>
      </section>

      <section>
        <div className="section-header">
          <div>
            <h2>Featured rooms</h2>
            <p>Well-designed stays for business travel, families, and premium guests.</p>
          </div>
          <Link to="/rooms" className="text-link">
            View all rooms
          </Link>
        </div>
        <div className="room-grid">
          {rooms.map((room) => (
            <RoomCard key={room.id} room={room} />
          ))}
        </div>
      </section>

      <section className="offer-strip">
        <div className="section-header">
          <div>
            <h2>Current offers</h2>
            <p>Apply these codes directly during booking.</p>
          </div>
        </div>
        <div className="offer-grid">
          {offers.length ? (
            offers.map((offer) => (
              <div className="offer-card" key={offer.id}>
                <span className="badge">{offer.code}</span>
                <h3>{offer.percentage}% OFF</h3>
                <p>{offer.description || "Special booking promotion"}</p>
                <small>Minimum booking ₹{offer.minimumBookingAmount}</small>
              </div>
            ))
          ) : (
            <div className="empty-state">No live offers right now.</div>
          )}
        </div>
      </section>
    </div>
  );
}
