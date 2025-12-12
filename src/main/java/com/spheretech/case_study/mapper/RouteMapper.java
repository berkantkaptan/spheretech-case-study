package com.spheretech.case_study.mapper;

import com.spheretech.case_study.dto.response.RouteResponseDto;
import com.spheretech.case_study.model.Route;
import org.springframework.stereotype.Component;


@Component
public class RouteMapper {

    public RouteResponseDto toResponse(Route route) {
        RouteResponseDto routeResponseDto = new RouteResponseDto();
        routeResponseDto.setId(route.getId());
        routeResponseDto.setSourceAirport(route.getSource().getName());
        routeResponseDto.setDestinationAirport(route.getDestination().getName());

        return routeResponseDto;
    }
}
