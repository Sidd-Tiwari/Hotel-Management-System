package com.github.SiddTiwari.room.service;

import com.github.SiddTiwari.room.domain.Discount;
import com.github.SiddTiwari.room.repository.DiscountRepository;
import com.github.SiddTiwari.room.web.dto.DiscountRequest;
import com.github.SiddTiwari.room.web.dto.DiscountResponse;
import com.github.SiddTiwari.room.web.dto.DiscountValidationResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;

    public List<DiscountResponse> publicOffers() {
        return discountRepository.findAllByActiveTrueOrderByExpiresAtAsc().stream()
                .filter(discount -> discount.getExpiresAt().isAfter(OffsetDateTime.now()))
                .map(this::toResponse)
                .toList();
    }

    public Discount getEntityByCode(String code) {
        return discountRepository.findByCodeIgnoreCase(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount code not found"));
    }

    public List<DiscountResponse> all() {
        return discountRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(Discount::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public DiscountValidationResponse validate(String code, BigDecimal amount) {
        Discount discount = getEntityByCode(code);
        boolean active = discount.isActive() && discount.getExpiresAt().isAfter(OffsetDateTime.now());
        if (!active) {
            return new DiscountValidationResponse(false, discount.getCode(), BigDecimal.ZERO, BigDecimal.ZERO, "Discount has expired or is inactive");
        }
        if (amount.compareTo(discount.getMinimumBookingAmount()) < 0) {
            return new DiscountValidationResponse(false, discount.getCode(), discount.getPercentage(), BigDecimal.ZERO, "Minimum booking amount not met");
        }
        BigDecimal discountAmount = amount.multiply(discount.getPercentage()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return new DiscountValidationResponse(true, discount.getCode(), discount.getPercentage(), discountAmount, "Discount applied");
    }

    @Transactional
    public DiscountResponse create(DiscountRequest request) {
        Discount discount = Discount.builder()
                .code(request.code().trim().toUpperCase())
                .percentage(request.percentage())
                .minimumBookingAmount(request.minimumBookingAmount())
                .expiresAt(request.expiresAt())
                .active(request.active())
                .description(request.description())
                .build();
        return toResponse(discountRepository.save(discount));
    }

    @Transactional
    public DiscountResponse update(Long id, DiscountRequest request) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Discount not found"));
        discount.setCode(request.code().trim().toUpperCase());
        discount.setPercentage(request.percentage());
        discount.setMinimumBookingAmount(request.minimumBookingAmount());
        discount.setExpiresAt(request.expiresAt());
        discount.setActive(request.active());
        discount.setDescription(request.description());
        return toResponse(discountRepository.save(discount));
    }

    @Transactional
    public void delete(Long id) {
        discountRepository.deleteById(id);
    }

    private DiscountResponse toResponse(Discount discount) {
        return new DiscountResponse(discount.getId(), discount.getCode(), discount.getPercentage(), discount.getMinimumBookingAmount(), discount.getExpiresAt(), discount.isActive(), discount.getDescription());
    }
}
