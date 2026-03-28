package com.github.SiddTiwari.booking.service;

import com.github.SiddTiwari.booking.domain.Booking;
import com.github.SiddTiwari.booking.domain.BookingStatus;
import com.github.SiddTiwari.booking.repository.BookingRepository;
import com.github.SiddTiwari.booking.web.dto.*;
import com.github.SiddTiwari.payment.domain.Payment;
import com.github.SiddTiwari.payment.service.PaymentService;
import com.github.SiddTiwari.room.domain.Discount;
import com.github.SiddTiwari.room.domain.Room;
import com.github.SiddTiwari.room.domain.RoomType;
import com.github.SiddTiwari.room.service.DiscountService;
import com.github.SiddTiwari.room.service.RoomService;
import com.github.SiddTiwari.user.domain.AppUser;
import com.github.SiddTiwari.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomService roomService;
    private final DiscountService discountService;
    private final PaymentService paymentService;

    private static final Set<BookingStatus> BLOCKING_STATUSES = EnumSet.of(
            BookingStatus.PENDING_PAYMENT,
            BookingStatus.CONFIRMED,
            BookingStatus.CHECKED_IN
    );

    public AvailabilitySearchResponse searchAvailability(
            java.time.LocalDate checkIn,
            java.time.LocalDate checkOut,
            String type,
            BigDecimal minPrice,
            BigDecimal maxPrice
    ) {
        RoomType roomType = normalizeType(type);
        List<AvailabilitySearchResponse.RoomSummary> rooms = roomService.search(roomType, minPrice, maxPrice, true).stream()
                .map(room -> new AvailabilitySearchResponse.RoomSummary(
                        room.id(),
                        room.roomNumber(),
                        room.name(),
                        room.type().name(),
                        room.pricePerNight(),
                        room.capacity(),
                        room.active(),
                        room.imageUrl(),
                        room.description(),
                        room.amenities()
                ))
                .toList();

        if (checkIn == null || checkOut == null) {
            return new AvailabilitySearchResponse(rooms);
        }
        List<Long> roomIds = rooms.stream().map(AvailabilitySearchResponse.RoomSummary::id).toList();
        if (roomIds.isEmpty()) {
            return new AvailabilitySearchResponse(List.of());
        }
        List<Long> blocked = bookingRepository.findConflictingRoomIds(roomIds, BLOCKING_STATUSES, checkIn, checkOut);
        List<AvailabilitySearchResponse.RoomSummary> available = rooms.stream().filter(room -> !blocked.contains(room.id())).toList();
        return new AvailabilitySearchResponse(available);
    }

    @Transactional
    public BookingResponse create(Long userId, CreateBookingRequest request) {
        if (!request.checkOutDate().isAfter(request.checkInDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-out date must be after check-in date");
        }

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Room room = roomService.getEntity(request.roomId());

        if (!room.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected room is not available");
        }
        if (request.guests() > room.getCapacity()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Guest count exceeds room capacity");
        }
        boolean conflict = bookingRepository.existsConflict(room.getId(), BLOCKING_STATUSES, request.checkInDate(), request.checkOutDate());
        if (conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room is not available for the selected dates");
        }

        long nights = request.checkOutDate().toEpochDay() - request.checkInDate().toEpochDay();
        BigDecimal baseAmount = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));
        BigDecimal discountAmount = BigDecimal.ZERO;
        Discount discount = null;

        if (request.discountCode() != null && !request.discountCode().isBlank()) {
            var validation = discountService.validate(request.discountCode().trim().toUpperCase(), baseAmount);
            if (!validation.valid()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, validation.message());
            }
            discountAmount = validation.discountAmount();
            discount = discountService.getEntityByCode(validation.code());
        }

        BigDecimal totalAmount = baseAmount.subtract(discountAmount);

        Booking booking = bookingRepository.save(Booking.builder()
                .user(user)
                .room(room)
                .discount(discount)
                .guests(request.guests())
                .checkInDate(request.checkInDate())
                .checkOutDate(request.checkOutDate())
                .baseAmount(baseAmount)
                .discountAmount(discountAmount)
                .totalAmount(totalAmount)
                .status(BookingStatus.PENDING_PAYMENT)
                .build());

        Payment payment = paymentService.createPendingPayment(booking, totalAmount);
        booking.setPayment(payment);

        return toResponse(bookingRepository.findDetailedById(booking.getId()).orElse(booking));
    }

    public List<BookingResponse> myBookings(Long userId) {
        return bookingRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::toResponse).toList();
    }

    @Transactional
    public BookingResponse cancel(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findDetailedById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        if (!Objects.equals(booking.getUser().getId(), userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can cancel only your own bookings");
        }
        if (booking.getStatus() == BookingStatus.CHECKED_IN || booking.getStatus() == BookingStatus.CHECKED_OUT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Checked-in or completed booking cannot be cancelled");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    public List<BookingResponse> allBookings() {
        return bookingRepository.findAllDetailed().stream().map(this::toResponse).toList();
    }

    @Transactional
    public BookingResponse checkIn(Long bookingId) {
        Booking booking = findBooking(bookingId);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only confirmed bookings can be checked in");
        }
        booking.setStatus(BookingStatus.CHECKED_IN);
        return toResponse(bookingRepository.save(booking));
    }

    @Transactional
    public BookingResponse checkOut(Long bookingId) {
        Booking booking = findBooking(bookingId);
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only checked-in bookings can be checked out");
        }
        booking.setStatus(BookingStatus.CHECKED_OUT);
        return toResponse(bookingRepository.save(booking));
    }

    public AnalyticsResponse analytics() {
        List<Booking> all = bookingRepository.findAllDetailed();
        long totalBookings = all.size();
        BigDecimal totalRevenue = all.stream()
                .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.CHECKED_OUT || booking.getStatus() == BookingStatus.CHECKED_IN)
                .map(Booking::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long pendingBookings = all.stream().filter(booking -> booking.getStatus() == BookingStatus.PENDING_PAYMENT).count();

        OffsetDateTime from = OffsetDateTime.now().minusMonths(5).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        List<Booking> recent = bookingRepository.findAllByCreatedAtAfterOrderByCreatedAtAsc(from);

        Map<YearMonth, List<Booking>> grouped = recent.stream().collect(Collectors.groupingBy(booking -> YearMonth.from(booking.getCreatedAt())));

        List<AnalyticsPoint> monthly = new ArrayList<>();
        YearMonth current = YearMonth.now().minusMonths(5);
        for (int i = 0; i < 6; i++) {
            List<Booking> monthData = grouped.getOrDefault(current, List.of());
            BigDecimal revenue = monthData.stream()
                    .filter(booking -> booking.getStatus() == BookingStatus.CONFIRMED || booking.getStatus() == BookingStatus.CHECKED_OUT || booking.getStatus() == BookingStatus.CHECKED_IN)
                    .map(Booking::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            monthly.add(new AnalyticsPoint(current.toString(), monthData.size(), revenue));
            current = current.plusMonths(1);
        }

        return new AnalyticsResponse(totalBookings, totalRevenue, pendingBookings, monthly);
    }

    private Booking findBooking(Long id) {
        return bookingRepository.findDetailedById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
    }

    private RoomType normalizeType(String type) {
        if (type == null || type.isBlank()) return null;
        try {
            return RoomType.valueOf(type.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported room type");
        }
    }

    private BookingResponse toResponse(Booking booking) {
        return new BookingResponse(
                booking.getId(),
                booking.getRoom().getId(),
                booking.getRoom().getName(),
                booking.getRoom().getType().name(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getGuests(),
                booking.getBaseAmount(),
                booking.getDiscountAmount(),
                booking.getTotalAmount(),
                booking.getDiscount() != null ? booking.getDiscount().getCode() : null,
                booking.getStatus(),
                booking.getPayment() != null ? booking.getPayment().getId() : null,
                booking.getPayment() != null ? booking.getPayment().getTransactionRef() : null,
                booking.getCreatedAt()
        );
    }
}
