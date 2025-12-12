package com.spheretech.case_study.controller;

import com.spheretech.case_study.dto.request.AirportRequestDto;
import com.spheretech.case_study.dto.response.AirportResponseDto;
import com.spheretech.case_study.service.AirportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/airport")
public class AirportController {

    private final AirportService airportService;

    @PostMapping("/add")
    public ResponseEntity<AirportResponseDto> addAirports(@RequestBody AirportRequestDto airportRequestDto){
        return ResponseEntity.ok().body(airportService.addAirport(airportRequestDto));
    }

    @GetMapping("/getById")
    public ResponseEntity<AirportResponseDto> findAirportById(@RequestParam("airportId") Long airportId){
        return ResponseEntity.ok().body(airportService.findAirportById(airportId));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<AirportResponseDto>> findAllAirports(){
        return ResponseEntity.ok().body(airportService.findAllAirports());
    }

    @GetMapping("/getByCity")
    public ResponseEntity<List<AirportResponseDto>> findAirportsByCity(@RequestParam("city") String city){
        return ResponseEntity.ok().body(airportService.findAirportsByCity(city));
    }

    @GetMapping("/getByCountry")
    public ResponseEntity<List<AirportResponseDto>> findAirportsByCountry(@RequestParam("country") String country){
        return ResponseEntity.ok().body(airportService.findAirportsByCountry(country));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AirportResponseDto>> searchAirports(@RequestParam("airportName") String airportName){
        return ResponseEntity.ok().body(airportService.searchAirports(airportName));
    }


}
