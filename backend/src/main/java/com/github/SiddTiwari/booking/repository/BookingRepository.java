package com.github.SiddTiwari.booking.repository;

import com.github.SiddTiwari.booking.domain.Booking;
import com.github.SiddTiwari.booking.domain.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"user", "room", "discount", "payment"})
    List<Booking> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"user", "room", "discount", "payment"})
    @Query("select b from Booking b order by b.createdAt desc")
    List<Booking> findAllDetailed();

    @EntityGraph(attributePaths = {"user", "room", "discount", "payment"})
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findDetailedById(Long id);

    @Query("""
            select b.room.id from Booking b
            where b.room.id in :roomIds
              and b.status in :statuses
              and b.checkInDate < :checkOut
              and b.checkOutDate > :checkIn
            """)
    List<Long> findConflictingRoomIds(List<Long> roomIds, Collection<BookingStatus> statuses, LocalDate checkIn, LocalDate checkOut);

    @Query("""
            select count(b) > 0 from Booking b
            where b.room.id = :roomId
              and b.status in :statuses
              and b.checkInDate < :checkOut
              and b.checkOutDate > :checkIn
            """)
    boolean existsConflict(Long roomId, Collection<BookingStatus> statuses, LocalDate checkIn, LocalDate checkOut);

    List<Booking> findAllByCreatedAtAfterOrderByCreatedAtAsc(OffsetDateTime from);
}
