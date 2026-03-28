import { useEffect, useState } from "react";
import toast from "react-hot-toast";
import { api } from "../lib/api";
import { useAuth } from "../state/AuthContext";
import type { Booking, Payment } from "../types";
import { LoadingSpinner } from "../components/LoadingSpinner";

export default function DashboardPage() {
  const { token, user } = useAuth();
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [payments, setPayments] = useState<Payment[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!token) return;
    Promise.all([
      api<Booking[]>("/api/bookings/me", { token }),
      api<Payment[]>("/api/payments/me", { token })
    ])
      .then(([bookingData, paymentData]) => {
        setBookings(bookingData);
        setPayments(paymentData);
      })
      .catch((error: Error) => toast.error(error.message))
      .finally(() => setLoading(false));
  }, [token]);

  const cancelBooking = async (bookingId: number) => {
    if (!token) return;
    try {
      await api(`/api/bookings/${bookingId}`, { method: "DELETE", token });
      toast.success("Booking cancelled.");
      setBookings((current) =>
        current.map((item) =>
          item.id === bookingId ? { ...item, status: "CANCELLED" } : item
        )
      );
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  if (loading) return <LoadingSpinner label="Loading your dashboard..." />;

  return (
    <div className="container page-stack">
      <div className="section-header">
        <div>
          <h1>Welcome back, {user?.name}</h1>
          <p>Track your reservations, payment status, and stay history.</p>
        </div>
      </div>

      <section className="dashboard-grid">
        <div className="card">
          <h2>My bookings</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Room</th>
                  <th>Dates</th>
                  <th>Status</th>
                  <th>Total</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {bookings.map((booking) => (
                  <tr key={booking.id}>
                    <td>{booking.roomName}</td>
                    <td>
                      {booking.checkInDate} → {booking.checkOutDate}
                    </td>
                    <td>{booking.status}</td>
                    <td>₹{booking.totalAmount}</td>
                    <td>
                      {booking.status !== "CANCELLED" && booking.status !== "CHECKED_OUT" && (
                        <button className="secondary-button small" onClick={() => cancelBooking(booking.id)}>
                          Cancel
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            {!bookings.length && <div className="empty-state">No bookings yet.</div>}
          </div>
        </div>

        <div className="card">
          <h2>My payments</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Booking</th>
                  <th>Status</th>
                  <th>Method</th>
                  <th>Ref</th>
                </tr>
              </thead>
              <tbody>
                {payments.map((payment) => (
                  <tr key={payment.id}>
                    <td>#{payment.bookingId}</td>
                    <td>{payment.status}</td>
                    <td>{payment.method}</td>
                    <td>{payment.transactionRef}</td>
                  </tr>
                ))}
              </tbody>
            </table>
            {!payments.length && <div className="empty-state">No payments recorded yet.</div>}
          </div>
        </div>
      </section>
    </div>
  );
}
