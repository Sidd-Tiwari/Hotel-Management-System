export type UserRole = "ADMIN" | "USER";

export type AuthUser = {
  userId: number;
  name: string;
  email: string;
  role: UserRole;
};

export type AuthResponse = AuthUser & {
  token: string;
};

export type Room = {
  id: number;
  roomNumber: string;
  name: string;
  type: string;
  pricePerNight: number;
  capacity: number;
  active: boolean;
  imageUrl?: string;
  description: string;
  amenities: string;
};

export type Discount = {
  id: number;
  code: string;
  percentage: number;
  minimumBookingAmount: number;
  expiresAt: string;
  active: boolean;
  description?: string;
};

export type Booking = {
  id: number;
  roomId: number;
  roomName: string;
  roomType: string;
  checkInDate: string;
  checkOutDate: string;
  guests: number;
  baseAmount: number;
  discountAmount: number;
  totalAmount: number;
  discountCode?: string;
  status: string;
  paymentId?: number;
  paymentReference?: string;
  createdAt: string;
};

export type Payment = {
  id: number;
  bookingId: number;
  amount: number;
  status: string;
  method: string;
  transactionRef: string;
  createdAt: string;
};

export type AnalyticsPoint = {
  label: string;
  bookings: number;
  revenue: number;
};

export type Analytics = {
  totalBookings: number;
  totalRevenue: number;
  pendingBookings: number;
  monthly: AnalyticsPoint[];
};
