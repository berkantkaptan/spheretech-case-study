package com.spheretech.case_study.service;

import com.spheretech.case_study.dto.request.AirportRequestDto;
import com.spheretech.case_study.dto.response.AirportResponseDto;
import com.spheretech.case_study.mapper.AirportMapper;
import com.spheretech.case_study.model.Airport;
import com.spheretech.case_study.repository.AirportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirportService {

    private final AirportRepository airportRepository;
    private final AirportMapper airportMapper;

    public AirportResponseDto addAirport(AirportRequestDto request) {

        Airport airport = new Airport();
        airport.setName(request.name());
        airport.setCity(request.city());
        airport.setCountry(request.country());

        Airport savedAirport = airportRepository.save(airport);

        return airportMapper.toResponseDto(savedAirport);
    }

    public Airport findAirportEntityById(Long airportId) {

        return airportRepository.findById(airportId)
                .orElseThrow(() -> new RuntimeException("Airport doesn't exist: " + airportId));
    }

    public AirportResponseDto findAirportById(Long airportId) {
        Airport airport = airportRepository.findById(airportId)
                .orElseThrow(() -> new RuntimeException("Airport doesn't exist: " + airportId));

        return airportMapper.toResponseDto(airport);
    }

    public List<AirportResponseDto> findAllAirports(){
        return airportMapper.toResponseDtoList(airportRepository.findAll());
    }


    public List<AirportResponseDto> findAirportsByCity(String city) {
        List<Airport> airports = airportRepository.findAirportsByCity(city);

        if (airports.isEmpty()) {
            return List.of();
        }

        return airportMapper.toResponseDtoList(airports);
    }

    public List<AirportResponseDto> findAirportsByCountry(String country) {
        List<Airport> airports = airportRepository.findAirportsByCountry(country);

        if (airports.isEmpty()) {
            return List.of();
        }

        return airportMapper.toResponseDtoList(airports);
    }

    public List<AirportResponseDto> searchAirports(String airportName) {
        if (airportName == null) {
            return findAllAirports();
        } else {
            return airportMapper.toResponseDtoList(airportRepository.findByNameContainingIgnoreCase(airportName));
        }
    }
}
