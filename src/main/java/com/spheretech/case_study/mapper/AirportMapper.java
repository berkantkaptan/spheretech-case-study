package com.spheretech.case_study.mapper;

import com.spheretech.case_study.dto.response.AirportResponseDto;
import com.spheretech.case_study.model.Airport;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AirportMapper {

    AirportResponseDto toResponseDto(Airport airport);

    List<AirportResponseDto> toResponseDtoList(List<Airport> airportList);
}
