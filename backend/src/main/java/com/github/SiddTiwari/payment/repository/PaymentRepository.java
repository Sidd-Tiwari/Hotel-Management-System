package com.github.SiddTiwari.payment.repository;

import com.github.SiddTiwari.payment.domain.Payment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @EntityGraph(attributePaths = {"booking", "booking.user", "booking.room"})
    Optional<Payment> findByBookingId(Long bookingId);

    @EntityGraph(attributePaths = {"booking", "booking.user", "booking.room"})
    @Query("""
            select p from Payment p
            join p.booking b
            join b.user u
            where lower(u.email) = lower(:userEmail)
            order by p.createdAt desc
            """)
    List<Payment> findAllByUserEmailOrderByCreatedAtDesc(String userEmail);

    @EntityGraph(attributePaths = {"booking", "booking.user", "booking.room"})
    @Query("select p from Payment p order by p.createdAt desc")
    List<Payment> findAllDetailed();

    @EntityGraph(attributePaths = {"booking", "booking.user", "booking.room"})
    @Query("select p from Payment p where p.id = :id")
    Optional<Payment> findDetailedById(Long id);
}
