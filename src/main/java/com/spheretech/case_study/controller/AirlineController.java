package com.spheretech.case_study.controller;

import com.spheretech.case_study.dto.request.AirlineRequestDto;
import com.spheretech.case_study.dto.response.AirlineResponseDto;
import com.spheretech.case_study.service.AirlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/airline")
public class AirlineController {

    private final AirlineService airlineService;

    @PostMapping("/add")
    public ResponseEntity<AirlineResponseDto> addAirlines(@RequestBody AirlineRequestDto airlineRequestDto){
        return ResponseEntity.ok().body(airlineService.addAirline(airlineRequestDto));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<AirlineResponseDto>> getAllAirlines(){
        return ResponseEntity.ok().body(airlineService.getAllAirlines());
    }

    @GetMapping("/getById")
    public ResponseEntity<AirlineResponseDto> getAirlineById(@RequestParam("airlineId") Long airlineId){
        return ResponseEntity.ok().body(airlineService.findAirlineById(airlineId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<AirlineResponseDto>> searchAirlines(@RequestParam("airlineName") String airlineName){
        return ResponseEntity.ok().body(airlineService.searchAirlines(airlineName));
    }



}
