package com.spheretech.case_study.service;

import com.spheretech.case_study.dto.request.AirlineRequestDto;
import com.spheretech.case_study.dto.response.AirlineResponseDto;
import com.spheretech.case_study.mapper.AirlineMapper;
import com.spheretech.case_study.model.Airline;
import com.spheretech.case_study.repository.AirlineRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AirlineServiceTest {

    @Mock
    private AirlineRepository airlineRepository;

    @Mock
    private AirlineMapper airlineMapper;

    @InjectMocks
    private AirlineService airlineService;

    @Test
    void addAirline_shouldSaveAndReturnResponse() {
        Long expectedId = 1L;
        String airlineName = "Turkish Airlines";
        AirlineRequestDto requestDto = new AirlineRequestDto(airlineName);

        Airline savedAirline = new Airline();
        savedAirline.setId(expectedId);
        savedAirline.setName(airlineName);

        AirlineResponseDto expectedResponse = new AirlineResponseDto();
        expectedResponse.setId(expectedId);
        expectedResponse.setName(airlineName);

        when(airlineRepository.save(any(Airline.class))).thenReturn(savedAirline);

        when(airlineMapper.toResponseDto(savedAirline)).thenReturn(expectedResponse);

        AirlineResponseDto actualResponse = airlineService.addAirline(requestDto);

        assertNotNull(actualResponse);
        assertEquals(airlineName, actualResponse.getName());

        verify(airlineRepository, times(1)).save(any(Airline.class));
        verify(airlineMapper, times(1)).toResponseDto(savedAirline);
    }

    @Test
    void getAllAirlines_shouldReturnAllAirlines() {
        List<Airline> mockEntities = Arrays.asList(new Airline(), new Airline());
        List<AirlineResponseDto> expectedResponses = Arrays.asList(
                new AirlineResponseDto(), new AirlineResponseDto()
        );

        when(airlineRepository.findAll()).thenReturn(mockEntities);
        when(airlineMapper.toResponseDtoList(mockEntities)).thenReturn(expectedResponses);

        List<AirlineResponseDto> actualResponses = airlineService.getAllAirlines();

        assertNotNull(actualResponses);
        assertEquals(2, actualResponses.size());

        verify(airlineRepository, times(1)).findAll();
        verify(airlineMapper, times(1)).toResponseDtoList(mockEntities);
    }

    @Test
    void findAirlineEntityById_shouldThrowRuntimeExceptionWhenNotFound() {
        Long nonExistentId = 99L;
        when(airlineRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            airlineService.findAirlineEntityById(nonExistentId);
        });

        verify(airlineRepository, times(1)).findById(nonExistentId);
    }

    @Test
    void findAirlineEntityById_shouldReturnEntityWhenFound() {
        Long existingId = 1L;
        Airline mockAirline = new Airline();
        mockAirline.setId(existingId);

        when(airlineRepository.findById(existingId)).thenReturn(Optional.of(mockAirline));

        Airline actualAirline = airlineService.findAirlineEntityById(existingId);

        assertNotNull(actualAirline);
        assertEquals(existingId, actualAirline.getId());

        verify(airlineRepository, times(1)).findById(existingId);
    }

    @Test
    void findAirlineById_shouldReturnResponseWhenFound() {
        Long existingId = 1L;
        Airline mockAirline = new Airline();
        AirlineResponseDto expectedResponse = new AirlineResponseDto();
        expectedResponse.setId(existingId);

        when(airlineRepository.findById(existingId)).thenReturn(Optional.of(mockAirline));
        when(airlineMapper.toResponseDto(mockAirline)).thenReturn(expectedResponse);

        AirlineResponseDto actualResponse = airlineService.findAirlineById(existingId);

        assertNotNull(actualResponse);

        verify(airlineRepository, times(1)).findById(existingId);
        verify(airlineMapper, times(1)).toResponseDto(mockAirline);
    }

    @Test
    void findAirlineById_shouldThrowRuntimeExceptionWhenNotFound() {
        Long nonExistentId = 99L;
        when(airlineRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            airlineService.findAirlineById(nonExistentId);
        });

        verify(airlineRepository, times(1)).findById(nonExistentId);
        verify(airlineMapper, never()).toResponseDto(any());
    }

    @Test
    void searchAirlines_whenNameIsPresent_shouldUseFilter() {
        String nameFilter = "Air";
        List<Airline> mockEntities = Collections.singletonList(new Airline());

        when(airlineRepository.findByNameContainingIgnoreCase(nameFilter)).thenReturn(mockEntities);
        when(airlineMapper.toResponseDtoList(mockEntities)).thenReturn(Collections.emptyList());

        airlineService.searchAirlines(nameFilter);

        verify(airlineRepository, times(1)).findByNameContainingIgnoreCase(nameFilter);
        verify(airlineRepository, never()).findAll();
    }

    @Test
    void searchAirlines_whenNameIsNull_shouldCallGetAllAirlines() {
        when(airlineRepository.findAll()).thenReturn(Collections.emptyList());
        when(airlineMapper.toResponseDtoList(any())).thenReturn(Collections.emptyList());

        airlineService.searchAirlines(null);

        verify(airlineRepository, never()).findByNameContainingIgnoreCase(any());
        verify(airlineRepository, times(1)).findAll();
    }

}