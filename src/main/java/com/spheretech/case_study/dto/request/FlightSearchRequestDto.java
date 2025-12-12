package com.spheretech.case_study.dto.request;

import java.time.LocalDateTime;

public record FlightSearchRequestDto(
        String airlineName,
        String sourceName,
        String destinationName,
        LocalDateTime date
) {
}
