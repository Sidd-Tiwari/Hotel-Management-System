import { FormEvent, useEffect, useMemo, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import toast from "react-hot-toast";
import { api } from "../lib/api";
import { useAuth } from "../state/AuthContext";
import type { Booking, Room } from "../types";
import { LoadingSpinner } from "../components/LoadingSpinner";

export default function BookingPage() {
  const { roomId } = useParams();
  const navigate = useNavigate();
  const { token } = useAuth();
  const [room, setRoom] = useState<Room | null>(null);
  const [booking, setBooking] = useState<Booking | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({
    checkInDate: "",
    checkOutDate: "",
    guests: 1,
    discountCode: ""
  });

  useEffect(() => {
    if (!roomId) return;
    api<Room>(`/api/public/rooms/${roomId}`)
      .then(setRoom)
      .catch((error: Error) => toast.error(error.message));
  }, [roomId]);

  const nights = useMemo(() => {
    if (!form.checkInDate || !form.checkOutDate) return 0;
    const from = new Date(form.checkInDate).getTime();
    const to = new Date(form.checkOutDate).getTime();
    return Math.max(0, Math.round((to - from) / 86400000));
  }, [form.checkInDate, form.checkOutDate]);

  const estimatedTotal = room ? nights * room.pricePerNight : 0;

  const createBooking = async (event: FormEvent) => {
    event.preventDefault();
    if (!token || !room) return;

    setSubmitting(true);
    try {
      const created = await api<Booking>("/api/bookings", {
        method: "POST",
        token,
        body: {
          roomId: room.id,
          checkInDate: form.checkInDate,
          checkOutDate: form.checkOutDate,
          guests: form.guests,
          discountCode: form.discountCode || undefined
        }
      });
      setBooking(created);
      toast.success("Booking created. Complete mock payment to confirm.");
    } catch (error) {
      toast.error((error as Error).message);
    } finally {
      setSubmitting(false);
    }
  };

  const completeMockPayment = async () => {
    if (!booking?.paymentId || !token) return;
    setSubmitting(true);
    try {
      await api(`/api/payments/${booking.paymentId}/mock-success`, {
        method: "POST",
        token
      });
      toast.success("Payment successful and booking confirmed.");
      navigate("/dashboard");
    } catch (error) {
      toast.error((error as Error).message);
    } finally {
      setSubmitting(false);
    }
  };

  if (!room) return <LoadingSpinner label="Preparing booking page..." />;

  return (
    <div className="container page-stack">
      <div className="details-grid">
        <div className="card">
          <h1>Book {room.name}</h1>
          <p>Choose your stay dates, add guests, and optionally apply a coupon code.</p>
          <form className="form-stack" onSubmit={createBooking}>
            <label>
              Check-in date
              <input
                type="date"
                required
                value={form.checkInDate}
                onChange={(e) => setForm((current) => ({ ...current, checkInDate: e.target.value }))}
              />
            </label>
            <label>
              Check-out date
              <input
                type="date"
                required
                value={form.checkOutDate}
                onChange={(e) => setForm((current) => ({ ...current, checkOutDate: e.target.value }))}
              />
            </label>
            <label>
              Guests
              <input
                type="number"
                min={1}
                max={room.capacity}
                value={form.guests}
                onChange={(e) => setForm((current) => ({ ...current, guests: Number(e.target.value) }))}
              />
            </label>
            <label>
              Discount code
              <input
                type="text"
                placeholder="WELCOME10"
                value={form.discountCode}
                onChange={(e) => setForm((current) => ({ ...current, discountCode: e.target.value.toUpperCase() }))}
              />
            </label>
            <button className="primary-button" type="submit" disabled={submitting}>
              {submitting ? "Processing..." : "Create booking"}
            </button>
          </form>
        </div>

        <div className="card summary-card">
          <h3>Stay summary</h3>
          <div className="summary-row">
            <span>Room</span>
            <strong>{room.name}</strong>
          </div>
          <div className="summary-row">
            <span>Nightly rate</span>
            <strong>₹{room.pricePerNight}</strong>
          </div>
          <div className="summary-row">
            <span>Estimated nights</span>
            <strong>{nights}</strong>
          </div>
          <div className="summary-row">
            <span>Estimated total</span>
            <strong>₹{estimatedTotal}</strong>
          </div>

          {booking && (
            <>
              <hr />
              <div className="summary-row">
                <span>Booking status</span>
                <strong>{booking.status}</strong>
              </div>
              <div className="summary-row">
                <span>Payable amount</span>
                <strong>₹{booking.totalAmount}</strong>
              </div>
              <button className="primary-button full-width" onClick={completeMockPayment} disabled={submitting}>
                Complete mock payment
              </button>
            </>
          )}
        </div>
      </div>
    </div>
  );
}
