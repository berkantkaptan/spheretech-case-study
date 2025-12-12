package com.spheretech.case_study.service;

import com.spheretech.case_study.dto.request.RouteRequestDto;
import com.spheretech.case_study.dto.response.RouteResponseDto;
import com.spheretech.case_study.mapper.RouteMapper;
import com.spheretech.case_study.model.Airport;
import com.spheretech.case_study.model.Route;
import com.spheretech.case_study.repository.RouteRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceTest {

    @Mock
    private RouteRepository routeRepository;

    @Mock
    private AirportService airportService;

    @Mock
    private RouteMapper routeMapper;

    @InjectMocks
    private RouteService routeService;

    private Airport createMockAirport(Long id, String name) {
        Airport airport = new Airport();
        airport.setId(id);
        airport.setName(name);
        return airport;
    }

    private Route createMockRoute(Long id, Airport source, Airport destination) {
        Route route = new Route();
        route.setId(id);
        route.setSource(source);
        route.setDestination(destination);
        return route;
    }


    @Test
    void addRoute_shouldSaveAndReturnResponse_onSuccess() {
        Long sourceId = 1L;
        Long destId = 2L;
        RouteRequestDto request = new RouteRequestDto(sourceId, destId);

        Airport source = createMockAirport(sourceId, "IST");
        Airport destination = createMockAirport(destId, "JFK");
        Route savedRoute = createMockRoute(10L, source, destination);

        RouteResponseDto expectedResponse = new RouteResponseDto();
        expectedResponse.setId(10L);
        expectedResponse.setSourceAirport(source.getName());
        expectedResponse.setDestinationAirport(destination.getName());

        when(airportService.findAirportEntityById(sourceId)).thenReturn(source);
        when(airportService.findAirportEntityById(destId)).thenReturn(destination);
        when(routeRepository.existsBySourceIdAndDestinationId(sourceId, destId)).thenReturn(false);
        when(routeRepository.save(any(Route.class))).thenReturn(savedRoute);
        when(routeMapper.toResponse(savedRoute)).thenReturn(expectedResponse);

        RouteResponseDto actualResponse = routeService.addRoute(request);

        assertNotNull(actualResponse);
        assertEquals(10L, actualResponse.getId());
        verify(routeRepository, times(1)).save(any(Route.class));
    }

    @Test
    void addRoute_shouldThrowException_whenSourceAndDestinationAreSame() {
        Long airportId = 1L;
        RouteRequestDto request = new RouteRequestDto(airportId, airportId);
        Airport airport = createMockAirport(airportId, "IST");

        when(airportService.findAirportEntityById(airportId)).thenReturn(airport);

        assertThrows(IllegalArgumentException.class, () -> {
            routeService.addRoute(request);
        });

        verify(routeRepository, never()).existsBySourceIdAndDestinationId(anyLong(), anyLong());
        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void addRoute_shouldThrowException_whenRouteAlreadyExists() {
        Long sourceId = 1L;
        Long destId = 2L;
        RouteRequestDto request = new RouteRequestDto(sourceId, destId);

        Airport source = createMockAirport(sourceId, "IST");
        Airport destination = createMockAirport(destId, "JFK");

        when(airportService.findAirportEntityById(sourceId)).thenReturn(source);
        when(airportService.findAirportEntityById(destId)).thenReturn(destination);
        when(routeRepository.existsBySourceIdAndDestinationId(sourceId, destId)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            routeService.addRoute(request);
        });

        verify(routeRepository, never()).save(any(Route.class));
    }

    @Test
    void findRouteEntityById_shouldReturnEntityWhenFound() {
        Long existingId = 1L;
        Route mockRoute = createMockRoute(existingId, createMockAirport(10L, "A"), createMockAirport(20L, "B"));

        when(routeRepository.findById(existingId)).thenReturn(Optional.of(mockRoute));

        Route actualRoute = routeService.findRouteEntityById(existingId);

        assertNotNull(actualRoute);
        assertEquals(existingId, actualRoute.getId());
        verify(routeRepository, times(1)).findById(existingId);
    }

    @Test
    void findRouteEntityById_shouldThrowRuntimeExceptionWhenNotFound() {
        Long nonExistentId = 99L;
        when(routeRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            routeService.findRouteEntityById(nonExistentId);
        });

        verify(routeRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void findRouteById_shouldReturnResponseWhenFound() {
        Long existingId = 1L;
        Route mockRoute = createMockRoute(existingId, createMockAirport(10L, "A"), createMockAirport(20L, "B"));
        RouteResponseDto expectedResponse = new RouteResponseDto();
        expectedResponse.setId(existingId);

        when(routeRepository.findById(existingId)).thenReturn(Optional.of(mockRoute));
        when(routeMapper.toResponse(mockRoute)).thenReturn(expectedResponse);

        RouteResponseDto actualResponse = routeService.findRouteById(existingId);

        assertNotNull(actualResponse);
        verify(routeRepository, times(1)).findById(existingId);
        verify(routeMapper, times(1)).toResponse(mockRoute);
    }

    @Test
    void findAllRoutes_shouldReturnAllRoutes() {
        List<Route> mockEntities = Collections.singletonList(createMockRoute(1L, createMockAirport(1L, "A"), createMockAirport(2L, "B")));

        when(routeRepository.findAll()).thenReturn(mockEntities);
        when(routeMapper.toResponse(any(Route.class))).thenReturn(new RouteResponseDto());

        List<RouteResponseDto> actualResponses = routeService.findAllRoutes();

        assertNotNull(actualResponses);
        assertEquals(1, actualResponses.size());

        verify(routeRepository, times(1)).findAll();
        verify(routeMapper, times(mockEntities.size())).toResponse(any(Route.class));
    }

    @Test
    void searchRoutes_whenBothNamesPresent_shouldCallRepositoryWithSpec() {
        String sourceName = "Istanbul";
        String destinationName = "New York";

        routeService.searchRoutes(sourceName, destinationName);

        verify(routeRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void searchRoutes_whenOnlySourceNamePresent_shouldCallRepositoryWithSpec() {
        String sourceName = "Istanbul";
        String destinationName = null;

        when(routeRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        routeService.searchRoutes(sourceName, destinationName);

        verify(routeRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void searchRoutes_whenBothNamesAreBlank_shouldCallRepositoryWithSpec() {
        String sourceName = " ";
        String destinationName = "";

        when(routeRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        routeService.searchRoutes(sourceName, destinationName);

        verify(routeRepository, times(1)).findAll(any(Specification.class));
    }
}