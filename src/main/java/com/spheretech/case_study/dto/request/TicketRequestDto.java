package com.spheretech.case_study.dto.request;

public record TicketRequestDto(
        Long flightId,
        String creditCard
) {
}
