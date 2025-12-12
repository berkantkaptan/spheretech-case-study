package com.spheretech.case_study.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class FlightResponseDto {

    private Long id;

    private String airlineName;

    private String route;

    private BigDecimal basePrice;

    private Integer quota;

    private LocalDateTime departureDate;
}
