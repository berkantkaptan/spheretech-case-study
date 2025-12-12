package com.spheretech.case_study.service;

import com.spheretech.case_study.dto.request.FlightRequestDto;
import com.spheretech.case_study.dto.request.FlightSearchRequestDto;
import com.spheretech.case_study.dto.response.FlightResponseDto;
import com.spheretech.case_study.mapper.FlightMapper;
import com.spheretech.case_study.model.Airline;
import com.spheretech.case_study.model.Airport;
import com.spheretech.case_study.model.Flight;
import com.spheretech.case_study.model.Route;
import com.spheretech.case_study.repository.FlightRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;
    private final RouteService routeService;
    private final AirlineService airlineService;
    private final FlightMapper flightMapper;

    @Transactional
    public FlightResponseDto addFlight(FlightRequestDto request) {

        Airline airline = airlineService.findAirlineEntityById(request.airlineId());
        Route route = routeService.findRouteEntityById(request.routeId());

        Flight flight = new Flight();
        flight.setAirline(airline);
        flight.setRoute(route);
        flight.setQuota(request.quota());
        flight.setBasePrice(request.basePrice());
        flight.setDepartureDate(request.departureDate());

        return flightMapper.toResponse(flightRepository.save(flight));
    }

    public FlightResponseDto findFlightById(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight doesn't exist: " + flightId));

        return flightMapper.toResponse(flight);
    }

    public Flight findFlightEntityById(Long flightId) {

        return flightRepository.findById(flightId)
                .orElseThrow(() -> new RuntimeException("Flight doesn't exist: " + flightId));
    }

    public List<FlightResponseDto> searchFlights(FlightSearchRequestDto flightSearchRequestDto) {

        Specification<Flight> spec = (root, query, cb) -> cb.conjunction();

        if (flightSearchRequestDto.airlineName() != null) {
            spec = spec.and(byAirlineName(flightSearchRequestDto.airlineName().trim()));
        }

        if (flightSearchRequestDto.sourceName() != null) {
            spec = spec.and(bySource(flightSearchRequestDto.sourceName().trim()));
        }

        if (flightSearchRequestDto.destinationName() != null) {
            spec = spec.and(byDestination(flightSearchRequestDto.destinationName().trim()));
        }

        if (flightSearchRequestDto.date() != null) {
            spec = spec.and(byDate(flightSearchRequestDto.date() ));
        }

        List<Flight> flights = flightRepository.findAll(spec);

        return flights.stream().map(flightMapper::toResponse).collect(Collectors.toList());
    }

    private Specification<Flight> byAirlineName(String airlineName) {
        return (root, query, criteriaBuilder) -> {
            Join<Flight, Airline> airlineJoin = root.join("airline", JoinType.INNER);

            return criteriaBuilder.like(
                    criteriaBuilder.upper(airlineJoin.get("name")),
                    "%" + airlineName.toUpperCase() + "%"
            );
        };
    }

    private Specification<Flight> bySource(String sourceName) {
        return (root, query, criteriaBuilder) -> {
            Join<Flight, Route> routeJoin = root.join("route", JoinType.INNER);
            Join<Route, Airport> sourceAirportJoin = routeJoin.join("source", JoinType.INNER);

            return criteriaBuilder.like(
                    criteriaBuilder.upper(sourceAirportJoin.get("name")),
                    "%" + sourceName.toUpperCase() + "%"
            );
        };
    }

    private Specification<Flight> byDestination(String destinationName) {
        return (root, query, criteriaBuilder) -> {
            Join<Flight, Route> routeJoin = root.join("route", JoinType.INNER);
            Join<Route, Airport> destAirportJoin = routeJoin.join("destination", JoinType.INNER);

            return criteriaBuilder.like(
                    criteriaBuilder.upper(destAirportJoin.get("name")),
                    "%" + destinationName.toUpperCase() + "%"
            );
        };
    }

    private Specification<Flight> byDate(LocalDateTime date) {
        return (root, query, criteriaBuilder) -> {
            LocalDateTime startDate = date.toLocalDate().atStartOfDay();
            LocalDateTime endDate = date.toLocalDate().plusDays(1).atStartOfDay();

            return criteriaBuilder.between(root.get("departureDate"), startDate, endDate);
        };
    }
}
