package com.spheretech.case_study.mapper;

import com.spheretech.case_study.dto.response.FlightResponseDto;
import com.spheretech.case_study.model.Flight;
import org.springframework.stereotype.Component;

@Component
public class FlightMapper {

    public FlightResponseDto toResponse(Flight flight){
        FlightResponseDto flightResponseDto = new FlightResponseDto();
        flightResponseDto.setId(flight.getId());
        flightResponseDto.setRoute(flight.getRoute().getSource().getName() + " -> " + flight.getRoute().getDestination().getName());
        flightResponseDto.setQuota(flight.getQuota());
        flightResponseDto.setBasePrice(flight.getBasePrice());
        flightResponseDto.setAirlineName(flight.getAirline().getName());
        flightResponseDto.setDepartureDate(flight.getDepartureDate());

        return flightResponseDto;
    }
}
