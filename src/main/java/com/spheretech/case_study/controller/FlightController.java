package com.spheretech.case_study.controller;

import com.spheretech.case_study.dto.request.FlightRequestDto;
import com.spheretech.case_study.dto.request.FlightSearchRequestDto;
import com.spheretech.case_study.dto.response.FlightResponseDto;
import com.spheretech.case_study.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/flight")
public class FlightController {

    private final FlightService flightService;

    @PostMapping("/add")
    public ResponseEntity<FlightResponseDto> addFlight(@RequestBody FlightRequestDto flightRequestDto){
        return ResponseEntity.ok().body(flightService.addFlight(flightRequestDto));
    }

    @GetMapping("/getById")
    public ResponseEntity<FlightResponseDto> getFlightById(@RequestParam("flightId") Long flightId){
        return ResponseEntity.ok().body(flightService.findFlightById(flightId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FlightResponseDto>> searchFlight(@RequestBody FlightSearchRequestDto flightSearchRequestDto){
        return ResponseEntity.ok().body(flightService.searchFlights(flightSearchRequestDto));
    }
}
