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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FlightServiceTest {

    @Mock
    private FlightRepository flightRepository;

    @Mock
    private RouteService routeService;

    @Mock
    private AirlineService airlineService;

    @Mock
    private FlightMapper flightMapper;

    @InjectMocks
    private FlightService flightService;

    private Airport createMockAirport(String name) {
        Airport airport = new Airport();
        airport.setName(name);
        return airport;
    }

    private Route createMockRoute(String sourceName, String destName) {
        Route route = new Route();
        route.setSource(createMockAirport(sourceName));
        route.setDestination(createMockAirport(destName));
        return route;
    }

    private Airline createMockAirline(String name) {
        Airline airline = new Airline();
        airline.setName(name);
        return airline;
    }

    @Test
    void addFlight_shouldSaveAndReturnResponse_onSuccess() {
        FlightRequestDto request = new FlightRequestDto(
                1L, 2L, BigDecimal.valueOf(100), 100, LocalDateTime.now());

        Airline mockAirline = createMockAirline("THY");
        Route mockRoute = createMockRoute("IST", "JFK");
        Flight savedFlight = new Flight();
        savedFlight.setId(5L);

        FlightResponseDto expectedResponse = new FlightResponseDto();
        expectedResponse.setId(5L);

        when(airlineService.findAirlineEntityById(1L)).thenReturn(mockAirline);
        when(routeService.findRouteEntityById(2L)).thenReturn(mockRoute);
        when(flightRepository.save(any(Flight.class))).thenReturn(savedFlight);
        when(flightMapper.toResponse(savedFlight)).thenReturn(expectedResponse);

        FlightResponseDto actualResponse = flightService.addFlight(request);

        assertNotNull(actualResponse);
        assertEquals(5L, actualResponse.getId());

        verify(airlineService, times(1)).findAirlineEntityById(1L);
        verify(routeService, times(1)).findRouteEntityById(2L);
        verify(flightRepository, times(1)).save(any(Flight.class));
        verify(flightMapper, times(1)).toResponse(savedFlight);
    }


    @Test
    void findFlightById_shouldReturnResponseWhenFound() {
        Long existingId = 1L;
        Flight mockFlight = new Flight();
        FlightResponseDto expectedResponse = new FlightResponseDto();
        expectedResponse.setId(existingId);

        when(flightRepository.findById(existingId)).thenReturn(Optional.of(mockFlight));
        when(flightMapper.toResponse(mockFlight)).thenReturn(expectedResponse);

        FlightResponseDto actualResponse = flightService.findFlightById(existingId);

        assertNotNull(actualResponse);
        assertEquals(existingId, actualResponse.getId());
        verify(flightRepository, times(1)).findById(existingId);
    }

    @Test
    void findFlightById_shouldThrowRuntimeExceptionWhenNotFound() {
        Long nonExistentId = 99L;
        when(flightRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            flightService.findFlightById(nonExistentId);
        });

        verify(flightRepository, times(1)).findById(nonExistentId);
        verify(flightMapper, never()).toResponse(any());
    }

    @Test
    void searchFlights_whenAllSearchCriteriaArePresent_shouldCallRepositoryWithSpecAndReturnMappedResult() {
        FlightSearchRequestDto request = new FlightSearchRequestDto(
                "THY", "Istanbul", "New York", LocalDateTime.now());

        Flight mockFlight = new Flight();
        List<Flight> mockEntities = Collections.singletonList(mockFlight);

        FlightResponseDto expectedDto = new FlightResponseDto();
        expectedDto.setAirlineName(request.airlineName());

        when(flightRepository.findAll(any(Specification.class))).thenReturn(mockEntities);
        when(flightMapper.toResponse(mockFlight)).thenReturn(expectedDto);

        List<FlightResponseDto> actualList = flightService.searchFlights(request);

        assertFalse(actualList.isEmpty());
        assertEquals("THY", actualList.get(0).getAirlineName());

        verify(flightRepository, times(1)).findAll(any(Specification.class));
        verify(flightMapper, times(mockEntities.size())).toResponse(mockFlight);
    }

    @Test
    void searchFlights_whenOnlySourceAndDateArePresent_shouldCallRepositoryWithSpec() {
        FlightSearchRequestDto request = new FlightSearchRequestDto(
                null, "Istanbul", null, LocalDateTime.now());

        when(flightRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        flightService.searchFlights(request);

        verify(flightRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void searchFlights_whenNoCriteriaArePresent_shouldCallRepositoryWithSpec() {
        FlightSearchRequestDto request = new FlightSearchRequestDto(
                null, null, null, null);

        when(flightRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        flightService.searchFlights(request);

        verify(flightRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void searchFlights_whenCriteriaAreEmptyStrings_shouldCallRepositoryWithSpec() {
        FlightSearchRequestDto request = new FlightSearchRequestDto(
                " ", "", "   ", null);

        when(flightRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        flightService.searchFlights(request);

        verify(flightRepository, times(1)).findAll(any(Specification.class));
    }
}