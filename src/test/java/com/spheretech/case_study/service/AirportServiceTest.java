package com.spheretech.case_study.service;

import com.spheretech.case_study.dto.request.AirportRequestDto;
import com.spheretech.case_study.dto.response.AirportResponseDto;
import com.spheretech.case_study.mapper.AirportMapper;
import com.spheretech.case_study.model.Airport;
import com.spheretech.case_study.repository.AirportRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AirportServiceTest {

    @Mock
    private AirportRepository airportRepository;

    @Mock
    private AirportMapper airportMapper;

    @InjectMocks
    private AirportService airportService;

    @Test
    void addAirport_shouldSaveAndReturnResponse() {
        String airportName = "Istanbul Airport";
        String city = "Istanbul";
        String country = "Turkey";

        AirportRequestDto requestDto = new AirportRequestDto(airportName, city, country);

        Airport savedAirport = new Airport();
        savedAirport.setName(airportName);
        savedAirport.setCity(city);
        savedAirport.setCountry(country);

        AirportResponseDto expectedResponse = new AirportResponseDto();
        expectedResponse.setName(airportName);
        expectedResponse.setCity(city);
        expectedResponse.setCountry(country);

        when(airportRepository.save(any(Airport.class))).thenReturn(savedAirport);
        when(airportMapper.toResponseDto(savedAirport)).thenReturn(expectedResponse);

        AirportResponseDto actualResponse = airportService.addAirport(requestDto);

        assertNotNull(actualResponse);
        assertEquals(airportName, actualResponse.getName());
        assertEquals(city, actualResponse.getCity());

        verify(airportRepository, times(1)).save(any(Airport.class));
        verify(airportMapper, times(1)).toResponseDto(savedAirport);
    }

    @Test
    void findAirportEntityById_shouldReturnEntityWhenFound() {
        Long existingId = 1L;
        Airport mockAirport = new Airport();
        mockAirport.setId(existingId);

        when(airportRepository.findById(existingId)).thenReturn(Optional.of(mockAirport));

        Airport actualAirport = airportService.findAirportEntityById(existingId);

        assertNotNull(actualAirport);
        assertEquals(existingId, actualAirport.getId());

        verify(airportRepository, times(1)).findById(existingId);
    }

    @Test
    void findAirportEntityById_shouldThrowRuntimeExceptionWhenNotFound() {
        Long nonExistentId = 99L;
        when(airportRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            airportService.findAirportEntityById(nonExistentId);
        });

        verify(airportRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void findAirportById_shouldReturnResponseWhenFound() {
        Long existingId = 1L;
        String name = "Test Airport";
        Airport mockAirport = new Airport();
        mockAirport.setName(name);

        AirportResponseDto expectedResponse = new AirportResponseDto();
        expectedResponse.setName(name);

        when(airportRepository.findById(existingId)).thenReturn(Optional.of(mockAirport));
        when(airportMapper.toResponseDto(mockAirport)).thenReturn(expectedResponse);

        AirportResponseDto actualResponse = airportService.findAirportById(existingId);

        assertNotNull(actualResponse);
        assertEquals(name, actualResponse.getName());

        verify(airportRepository, times(1)).findById(existingId);
        verify(airportMapper, times(1)).toResponseDto(mockAirport);
    }

    @Test
    void findAllAirports_shouldReturnAllAirports() {
        List<Airport> mockEntities = Arrays.asList(new Airport(), new Airport());

        AirportResponseDto dto1 = new AirportResponseDto(); dto1.setName("A");
        AirportResponseDto dto2 = new AirportResponseDto(); dto2.setName("B");
        List<AirportResponseDto> expectedResponses = Arrays.asList(dto1, dto2);

        when(airportRepository.findAll()).thenReturn(mockEntities);
        when(airportMapper.toResponseDtoList(mockEntities)).thenReturn(expectedResponses);

        List<AirportResponseDto> actualResponses = airportService.findAllAirports();

        assertNotNull(actualResponses);
        assertEquals(2, actualResponses.size());

        verify(airportRepository, times(1)).findAll();
        verify(airportMapper, times(1)).toResponseDtoList(mockEntities);
    }


    @Test
    void findAirportsByCity_shouldReturnMatchingAirportsWhenFound() {
        String city = "Istanbul";
        List<Airport> mockEntities = Collections.singletonList(new Airport());

        when(airportRepository.findAirportsByCity(city)).thenReturn(mockEntities);
        when(airportMapper.toResponseDtoList(mockEntities)).thenReturn(Collections.singletonList(new AirportResponseDto()));

        List<AirportResponseDto> actualResponses = airportService.findAirportsByCity(city);

        assertFalse(actualResponses.isEmpty());

        verify(airportRepository, times(1)).findAirportsByCity(city);
    }

    @Test
    void findAirportsByCity_shouldReturnEmptyListWhenNotFound() {
        String city = "Nonexistent City";
        when(airportRepository.findAirportsByCity(city)).thenReturn(Collections.emptyList());

        List<AirportResponseDto> actualResponses = airportService.findAirportsByCity(city);

        assertTrue(actualResponses.isEmpty());

        verify(airportRepository, times(1)).findAirportsByCity(city);
        verify(airportMapper, never()).toResponseDtoList(any());
    }

    @Test
    void findAirportsByCountry_shouldReturnMatchingAirportsWhenFound() {
        String country = "Turkey";
        List<Airport> mockEntities = Collections.singletonList(new Airport());

        when(airportRepository.findAirportsByCountry(country)).thenReturn(mockEntities);
        when(airportMapper.toResponseDtoList(mockEntities)).thenReturn(Collections.singletonList(new AirportResponseDto()));

        airportService.findAirportsByCountry(country);

        verify(airportRepository, times(1)).findAirportsByCountry(country);
    }

    @Test
    void searchAirports_whenNameIsPresent_shouldUseFilter() {
        String nameFilter = "Air";
        when(airportRepository.findByNameContainingIgnoreCase(nameFilter)).thenReturn(Collections.emptyList());
        when(airportMapper.toResponseDtoList(any())).thenReturn(Collections.emptyList());

        airportService.searchAirports(nameFilter);

        verify(airportRepository, times(1)).findByNameContainingIgnoreCase(nameFilter);
        verify(airportRepository, never()).findAll();
    }

    @Test
    void searchAirports_whenNameIsNull_shouldCallFindAllAirports() {
        when(airportRepository.findAll()).thenReturn(Collections.emptyList());
        when(airportMapper.toResponseDtoList(any())).thenReturn(Collections.emptyList());

        airportService.searchAirports(null);

        verify(airportRepository, never()).findByNameContainingIgnoreCase(any());
        verify(airportRepository, times(1)).findAll();
    }
}