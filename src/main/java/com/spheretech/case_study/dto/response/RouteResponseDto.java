package com.spheretech.case_study.dto.response;

import lombok.Data;

@Data
public class RouteResponseDto {

    private Long id;

    private String sourceAirport;

    private String destinationAirport;
}
