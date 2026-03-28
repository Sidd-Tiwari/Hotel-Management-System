import { FormEvent, useEffect, useState } from "react";
import toast from "react-hot-toast";
import {
  ResponsiveContainer,
  BarChart,
  CartesianGrid,
  XAxis,
  YAxis,
  Tooltip,
  Bar,
  LineChart,
  Line
} from "recharts";
import { api } from "../lib/api";
import { useAuth } from "../state/AuthContext";
import type { Analytics, Booking, Discount, Payment, Room } from "../types";
import { LoadingSpinner } from "../components/LoadingSpinner";

type AdminUser = {
  id: number;
  name: string;
  email: string;
  role: "ADMIN" | "USER";
  verified: boolean;
  createdAt: string;
};

const initialRoomForm = {
  roomNumber: "",
  name: "",
  type: "STANDARD",
  pricePerNight: 0,
  capacity: 1,
  active: true,
  imageUrl: "",
  description: "",
  amenities: ""
};

const initialDiscountForm = {
  code: "",
  percentage: 10,
  minimumBookingAmount: 0,
  expiresAt: "",
  active: true,
  description: ""
};

export default function AdminDashboardPage() {
  const { token } = useAuth();
  const [users, setUsers] = useState<AdminUser[]>([]);
  const [rooms, setRooms] = useState<Room[]>([]);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [payments, setPayments] = useState<Payment[]>([]);
  const [discounts, setDiscounts] = useState<Discount[]>([]);
  const [analytics, setAnalytics] = useState<Analytics | null>(null);
  const [loading, setLoading] = useState(true);

  const [roomForm, setRoomForm] = useState(initialRoomForm);
  const [discountForm, setDiscountForm] = useState(initialDiscountForm);
  const [editingRoomId, setEditingRoomId] = useState<number | null>(null);
  const [editingDiscountId, setEditingDiscountId] = useState<number | null>(null);

  const loadAll = async () => {
    if (!token) return;
    setLoading(true);
    try {
      const [userData, roomData, bookingData, paymentData, discountData, analyticsData] =
        await Promise.all([
          api<AdminUser[]>("/api/admin/users", { token }),
          api<Room[]>("/api/admin/rooms", { token }),
          api<Booking[]>("/api/admin/bookings", { token }),
          api<Payment[]>("/api/admin/payments", { token }),
          api<Discount[]>("/api/admin/discounts", { token }),
          api<Analytics>("/api/admin/analytics", { token })
        ]);
      setUsers(userData);
      setRooms(roomData);
      setBookings(bookingData);
      setPayments(paymentData);
      setDiscounts(discountData);
      setAnalytics(analyticsData);
    } catch (error) {
      toast.error((error as Error).message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAll();
  }, [token]);

  const resetRoomForm = () => {
    setRoomForm(initialRoomForm);
    setEditingRoomId(null);
  };

  const resetDiscountForm = () => {
    setDiscountForm(initialDiscountForm);
    setEditingDiscountId(null);
  };

  const saveRoom = async (event: FormEvent) => {
    event.preventDefault();
    if (!token) return;
    try {
      if (editingRoomId) {
        await api<Room>(`/api/admin/rooms/${editingRoomId}`, {
          method: "PUT",
          token,
          body: roomForm
        });
        toast.success("Room updated.");
      } else {
        await api<Room>("/api/admin/rooms", {
          method: "POST",
          token,
          body: roomForm
        });
        toast.success("Room created.");
      }
      resetRoomForm();
      loadAll();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const saveDiscount = async (event: FormEvent) => {
    event.preventDefault();
    if (!token) return;
    try {
      const body = {
        ...discountForm,
        expiresAt: new Date(discountForm.expiresAt).toISOString()
      };

      if (editingDiscountId) {
        await api<Discount>(`/api/admin/discounts/${editingDiscountId}`, {
          method: "PUT",
          token,
          body
        });
        toast.success("Discount updated.");
      } else {
        await api<Discount>("/api/admin/discounts", {
          method: "POST",
          token,
          body
        });
        toast.success("Discount created.");
      }
      resetDiscountForm();
      loadAll();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const editRoom = (room: Room) => {
    setEditingRoomId(room.id);
    setRoomForm({
      roomNumber: room.roomNumber,
      name: room.name,
      type: room.type,
      pricePerNight: room.pricePerNight,
      capacity: room.capacity,
      active: room.active,
      imageUrl: room.imageUrl || "",
      description: room.description,
      amenities: room.amenities
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const deleteRoom = async (roomId: number) => {
    if (!token) return;
    try {
      await api(`/api/admin/rooms/${roomId}`, { method: "DELETE", token });
      toast.success("Room deactivated.");
      loadAll();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const editDiscount = (discount: Discount) => {
    setEditingDiscountId(discount.id);
    setDiscountForm({
      code: discount.code,
      percentage: discount.percentage,
      minimumBookingAmount: discount.minimumBookingAmount,
      expiresAt: discount.expiresAt.slice(0, 16),
      active: discount.active,
      description: discount.description || ""
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const deleteDiscount = async (discountId: number) => {
    if (!token) return;
    try {
      await api(`/api/admin/discounts/${discountId}`, {
        method: "DELETE",
        token
      });
      toast.success("Discount deleted.");
      loadAll();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const updateUserRole = async (userId: number, role: "ADMIN" | "USER") => {
    if (!token) return;
    try {
      await api(`/api/admin/users/${userId}/role`, {
        method: "PATCH",
        token,
        body: { role }
      });
      toast.success("User role updated.");
      loadAll();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const checkIn = async (bookingId: number) => {
    if (!token) return;
    try {
      await api(`/api/admin/bookings/${bookingId}/check-in`, {
        method: "PATCH",
        token
      });
      toast.success("Guest checked in.");
      loadAll();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  const checkOut = async (bookingId: number) => {
    if (!token) return;
    try {
      await api(`/api/admin/bookings/${bookingId}/check-out`, {
        method: "PATCH",
        token
      });
      toast.success("Guest checked out.");
      loadAll();
    } catch (error) {
      toast.error((error as Error).message);
    }
  };

  if (loading) return <LoadingSpinner label="Loading admin workspace..." />;

  return (
    <div className="container page-stack">
      <div className="section-header">
        <div>
          <h1>Admin dashboard</h1>
          <p>Manage users, rooms, discounts, bookings, payments, and hotel performance.</p>
        </div>
      </div>

      {analytics && (
        <section className="stats-grid">
          <div className="card stat-card">
            <span>Total bookings</span>
            <strong>{analytics.totalBookings}</strong>
          </div>
          <div className="card stat-card">
            <span>Total revenue</span>
            <strong>₹{analytics.totalRevenue}</strong>
          </div>
          <div className="card stat-card">
            <span>Pending bookings</span>
            <strong>{analytics.pendingBookings}</strong>
          </div>
        </section>
      )}

      {analytics && (
        <section className="dashboard-grid">
          <div className="card chart-card">
            <h2>Booking trend</h2>
            <div style={{ width: "100%", height: 320 }}>
              <ResponsiveContainer>
                <BarChart data={analytics.monthly}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="label" />
                  <YAxis />
                  <Tooltip />
                  <Bar dataKey="bookings" />
                </BarChart>
              </ResponsiveContainer>
            </div>
          </div>

          <div className="card chart-card">
            <h2>Revenue trend</h2>
            <div style={{ width: "100%", height: 320 }}>
              <ResponsiveContainer>
                <LineChart data={analytics.monthly}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="label" />
                  <YAxis />
                  <Tooltip />
                  <Line type="monotone" dataKey="revenue" />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        </section>
      )}

      <section className="dashboard-grid">
        <div className="card">
          <div className="row-between">
            <h2>{editingRoomId ? "Edit room" : "Add room"}</h2>
            {editingRoomId && (
              <button className="secondary-button small" type="button" onClick={resetRoomForm}>
                Cancel edit
              </button>
            )}
          </div>
          <form className="form-stack" onSubmit={saveRoom}>
            <input placeholder="Room number" value={roomForm.roomNumber} onChange={(e) => setRoomForm((c) => ({ ...c, roomNumber: e.target.value }))} />
            <input placeholder="Room name" value={roomForm.name} onChange={(e) => setRoomForm((c) => ({ ...c, name: e.target.value }))} />
            <select value={roomForm.type} onChange={(e) => setRoomForm((c) => ({ ...c, type: e.target.value }))}>
              <option value="STANDARD">Standard</option>
              <option value="DELUXE">Deluxe</option>
              <option value="SUITE">Suite</option>
              <option value="FAMILY">Family</option>
              <option value="EXECUTIVE">Executive</option>
            </select>
            <input type="number" placeholder="Price" value={roomForm.pricePerNight} onChange={(e) => setRoomForm((c) => ({ ...c, pricePerNight: Number(e.target.value) }))} />
            <input type="number" placeholder="Capacity" value={roomForm.capacity} onChange={(e) => setRoomForm((c) => ({ ...c, capacity: Number(e.target.value) }))} />
            <input placeholder="Image URL" value={roomForm.imageUrl} onChange={(e) => setRoomForm((c) => ({ ...c, imageUrl: e.target.value }))} />
            <textarea placeholder="Description" value={roomForm.description} onChange={(e) => setRoomForm((c) => ({ ...c, description: e.target.value }))} />
            <input placeholder="Amenities comma separated" value={roomForm.amenities} onChange={(e) => setRoomForm((c) => ({ ...c, amenities: e.target.value }))} />
            <label className="checkbox">
              <input type="checkbox" checked={roomForm.active} onChange={(e) => setRoomForm((c) => ({ ...c, active: e.target.checked }))} />
              Active room
            </label>
            <button className="primary-button" type="submit">
              {editingRoomId ? "Update room" : "Save room"}
            </button>
          </form>
        </div>

        <div className="card">
          <div className="row-between">
            <h2>{editingDiscountId ? "Edit discount" : "Create discount"}</h2>
            {editingDiscountId && (
              <button className="secondary-button small" type="button" onClick={resetDiscountForm}>
                Cancel edit
              </button>
            )}
          </div>
          <form className="form-stack" onSubmit={saveDiscount}>
            <input placeholder="Code" value={discountForm.code} onChange={(e) => setDiscountForm((c) => ({ ...c, code: e.target.value.toUpperCase() }))} />
            <input type="number" placeholder="Percentage" value={discountForm.percentage} onChange={(e) => setDiscountForm((c) => ({ ...c, percentage: Number(e.target.value) }))} />
            <input type="number" placeholder="Minimum booking amount" value={discountForm.minimumBookingAmount} onChange={(e) => setDiscountForm((c) => ({ ...c, minimumBookingAmount: Number(e.target.value) }))} />
            <input type="datetime-local" value={discountForm.expiresAt} onChange={(e) => setDiscountForm((c) => ({ ...c, expiresAt: e.target.value }))} />
            <textarea placeholder="Description" value={discountForm.description} onChange={(e) => setDiscountForm((c) => ({ ...c, description: e.target.value }))} />
            <label className="checkbox">
              <input type="checkbox" checked={discountForm.active} onChange={(e) => setDiscountForm((c) => ({ ...c, active: e.target.checked }))} />
              Active discount
            </label>
            <button className="primary-button" type="submit">
              {editingDiscountId ? "Update discount" : "Save discount"}
            </button>
          </form>
        </div>
      </section>

      <section className="dashboard-grid">
        <div className="card">
          <h2>Users</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Verified</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {users.map((user) => (
                  <tr key={user.id}>
                    <td>{user.name}</td>
                    <td>{user.email}</td>
                    <td>{user.role}</td>
                    <td>{String(user.verified)}</td>
                    <td>
                      {user.role === "USER" ? (
                        <button className="secondary-button small" onClick={() => updateUserRole(user.id, "ADMIN")}>
                          Make admin
                        </button>
                      ) : (
                        <button className="secondary-button small" onClick={() => updateUserRole(user.id, "USER")}>
                          Make user
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="card">
          <h2>Rooms</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Type</th>
                  <th>Price</th>
                  <th>Active</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {rooms.map((room) => (
                  <tr key={room.id}>
                    <td>{room.name}</td>
                    <td>{room.type}</td>
                    <td>₹{room.pricePerNight}</td>
                    <td>{String(room.active)}</td>
                    <td>
                      <div className="inline-actions">
                        <button className="secondary-button small" onClick={() => editRoom(room)}>
                          Edit
                        </button>
                        <button className="ghost-button small" onClick={() => deleteRoom(room.id)}>
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <section className="dashboard-grid">
        <div className="card">
          <h2>Bookings</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Room</th>
                  <th>Status</th>
                  <th>Total</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {bookings.map((booking) => (
                  <tr key={booking.id}>
                    <td>#{booking.id}</td>
                    <td>{booking.roomName}</td>
                    <td>{booking.status}</td>
                    <td>₹{booking.totalAmount}</td>
                    <td>
                      {booking.status === "CONFIRMED" && (
                        <button className="secondary-button small" onClick={() => checkIn(booking.id)}>
                          Check-in
                        </button>
                      )}
                      {booking.status === "CHECKED_IN" && (
                        <button className="secondary-button small" onClick={() => checkOut(booking.id)}>
                          Check-out
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="card">
          <h2>Payments & offers</h2>
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Type</th>
                  <th>Status</th>
                  <th>Amount / Value</th>
                  <th>Ref / Code</th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {payments.map((payment) => (
                  <tr key={payment.id}>
                    <td>Payment #{payment.id}</td>
                    <td>{payment.status}</td>
                    <td>₹{payment.amount}</td>
                    <td>{payment.transactionRef}</td>
                    <td />
                  </tr>
                ))}
                {discounts.map((discount) => (
                  <tr key={`discount-${discount.id}`}>
                    <td>Discount</td>
                    <td>{discount.active ? "ACTIVE" : "INACTIVE"}</td>
                    <td>{discount.percentage}%</td>
                    <td>{discount.code}</td>
                    <td>
                      <div className="inline-actions">
                        <button className="secondary-button small" onClick={() => editDiscount(discount)}>
                          Edit
                        </button>
                        <button className="ghost-button small" onClick={() => deleteDiscount(discount.id)}>
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </div>
  );
}
