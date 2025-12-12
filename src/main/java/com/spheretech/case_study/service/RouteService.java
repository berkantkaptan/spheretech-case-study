package com.spheretech.case_study.service;

import com.spheretech.case_study.dto.request.RouteRequestDto;
import com.spheretech.case_study.dto.response.RouteResponseDto;
import com.spheretech.case_study.mapper.RouteMapper;
import com.spheretech.case_study.model.Airport;
import com.spheretech.case_study.model.Route;
import com.spheretech.case_study.repository.RouteRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final AirportService airportService;
    private final RouteMapper routeMapper;

    @Transactional
    public RouteResponseDto addRoute(RouteRequestDto request) {

        Airport source = airportService.findAirportEntityById(request.sourceAirportId());

        Airport destination = airportService.findAirportEntityById(request.destinationAirportId());


        if (source.getId().equals(destination.getId())) {
            throw new IllegalArgumentException("Source and destination cannot be same.");
        }

        if (routeRepository.existsBySourceIdAndDestinationId(source.getId(), destination.getId())) {
            throw new IllegalArgumentException("Route already exists");
        }

        Route route = new Route();
        route.setSource(source);
        route.setDestination(destination);

        return routeMapper.toResponse(routeRepository.save(route));
    }

    public Route findRouteEntityById(Long routeId){
        return routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route doesn't exist:" + routeId));
    }

    public RouteResponseDto findRouteById(Long routeId){
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route doesn't exist:" + routeId));

        return routeMapper.toResponse(route);
    }

    public List<RouteResponseDto> findAllRoutes() {

        return routeRepository.findAll().stream().map(routeMapper::toResponse).collect(Collectors.toList());
    }

    public List<RouteResponseDto> searchRoutes(String sourceName, String destinationName) {

        Specification<Route> spec = (root, query, cb) -> cb.conjunction();

        if (sourceName != null && !sourceName.isBlank()) {
            spec = spec.and(hasSource(sourceName));
        }

        if (destinationName != null && !destinationName.isBlank()) {
            spec = spec.and(hasDestination(destinationName));
        }


        return routeRepository.findAll(spec).stream().map(routeMapper::toResponse).collect(Collectors.toList());
    }

    private Specification<Route> hasSource(String sourceName) {
        return (root, query, criteriaBuilder) -> {
            root.fetch("source", JoinType.INNER);
            Join<Route, Airport> sourceAirportJoin = root.join("source", JoinType.INNER);

            return criteriaBuilder.like(
                    criteriaBuilder.upper(sourceAirportJoin.get("name")),
                    "%" + sourceName.toUpperCase() + "%"
            );
        };
    }

   private Specification<Route> hasDestination(String destinationName) {
        return (root, query, criteriaBuilder) -> {
            root.fetch("destination", JoinType.INNER);
            Join<Route, Airport> destAirportJoin = root.join("destination", JoinType.INNER);

            return criteriaBuilder.like(
                    criteriaBuilder.upper(destAirportJoin.get("name")),
                    "%" + destinationName.toUpperCase() + "%"
            );
        };
    }

}
