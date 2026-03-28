package com.github.SiddTiwari.room.repository;

import com.github.SiddTiwari.room.domain.Discount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiscountRepository extends JpaRepository<Discount, Long> {
    Optional<Discount> findByCodeIgnoreCase(String code);
    List<Discount> findAllByActiveTrueOrderByExpiresAtAsc();
}
