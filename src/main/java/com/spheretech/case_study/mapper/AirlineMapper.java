package com.spheretech.case_study.mapper;

import com.spheretech.case_study.dto.response.AirlineResponseDto;
import com.spheretech.case_study.model.Airline;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AirlineMapper {

    AirlineResponseDto toResponseDto(Airline airline);

    List<AirlineResponseDto> toResponseDtoList(List<Airline> airlineList);
}
