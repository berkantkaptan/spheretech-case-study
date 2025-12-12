package com.spheretech.case_study.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FlightRequestDto(
        Long airlineId,
        Long routeId,
        BigDecimal basePrice,
        Integer quota,
        LocalDateTime departureDate
) {
}
