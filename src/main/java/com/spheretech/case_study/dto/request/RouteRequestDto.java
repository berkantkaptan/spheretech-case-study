package com.spheretech.case_study.dto.request;

public record RouteRequestDto(
        Long sourceAirportId,
        Long destinationAirportId
) {
}
