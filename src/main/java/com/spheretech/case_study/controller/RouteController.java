package com.spheretech.case_study.controller;

import com.spheretech.case_study.dto.request.RouteRequestDto;
import com.spheretech.case_study.dto.response.RouteResponseDto;
import com.spheretech.case_study.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/route")
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/add")
    public ResponseEntity<RouteResponseDto> addRoute(@RequestBody RouteRequestDto routeRequestDto) {
        return ResponseEntity.ok().body(routeService.addRoute(routeRequestDto));
    }

    @GetMapping("/getById")
    public ResponseEntity<RouteResponseDto> getRouteById(@RequestParam("routeId") Long routeId) {
        return ResponseEntity.ok().body(routeService.findRouteById(routeId));
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<RouteResponseDto>> getAllRoutes() {
        return ResponseEntity.ok().body(routeService.findAllRoutes());
    }

    @GetMapping("/search")
    public ResponseEntity<List<RouteResponseDto>> searchRoutes(@RequestParam(required = false) String sourceName, @RequestParam(required = false) String destinationName) {
        return ResponseEntity.ok().body(routeService.searchRoutes(sourceName, destinationName));
    }
}
