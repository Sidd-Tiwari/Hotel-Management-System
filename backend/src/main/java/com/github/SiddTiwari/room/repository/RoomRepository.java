package com.github.SiddTiwari.room.repository;

import com.github.SiddTiwari.room.domain.Room;
import com.github.SiddTiwari.room.domain.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    @Query("""
            select r from Room r
            where (:type is null or r.type = :type)
              and (:minPrice is null or r.pricePerNight >= :minPrice)
              and (:maxPrice is null or r.pricePerNight <= :maxPrice)
              and (:onlyActive = false or r.active = true)
            order by r.pricePerNight asc
            """)
    List<Room> search(RoomType type, BigDecimal minPrice, BigDecimal maxPrice, boolean onlyActive);
}
