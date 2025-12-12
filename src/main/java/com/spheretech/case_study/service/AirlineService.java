package com.spheretech.case_study.service;

import com.spheretech.case_study.dto.request.AirlineRequestDto;
import com.spheretech.case_study.dto.response.AirlineResponseDto;
import com.spheretech.case_study.mapper.AirlineMapper;
import com.spheretech.case_study.model.Airline;
import com.spheretech.case_study.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirlineService {

    private final AirlineRepository airlineRepository;
    private final AirlineMapper airlineMapper;

    public AirlineResponseDto addAirline(AirlineRequestDto request) {

        Airline airline = new Airline();
        airline.setName(request.name());

        Airline savedAirline = airlineRepository.save(airline);

        return airlineMapper.toResponseDto(savedAirline);
    }

    public List<AirlineResponseDto> getAllAirlines(){
        return airlineMapper.toResponseDtoList(airlineRepository.findAll());
    }

    public Airline findAirlineEntityById(Long airlineId) {
        return airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("Airline doesn't exist: " + airlineId));
    }

    public AirlineResponseDto findAirlineById(Long airlineId) {
        Airline airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("Airline doesn't exist: " + airlineId));

        return airlineMapper.toResponseDto(airline);
    }

    public List<AirlineResponseDto> searchAirlines(String airlineName) {
        if (airlineName == null) {
            return getAllAirlines();
        } else {
            return airlineMapper.toResponseDtoList(airlineRepository.findByNameContainingIgnoreCase(airlineName));
        }
    }
}
