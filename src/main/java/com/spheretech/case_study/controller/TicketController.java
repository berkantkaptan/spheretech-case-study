package com.spheretech.case_study.controller;

import com.spheretech.case_study.dto.request.TicketRequestDto;
import com.spheretech.case_study.dto.response.TicketResponseDto;
import com.spheretech.case_study.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ticket")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/buy")
    public ResponseEntity<TicketResponseDto> buyTicket(@RequestBody TicketRequestDto ticketRequestDto) {
        return ResponseEntity.ok().body(ticketService.buyTicket(ticketRequestDto));
    }

    @GetMapping("/getByTicketNumber")
    public ResponseEntity<TicketResponseDto> buyTicket(@RequestParam("ticketNumber") String ticketNumber) {
        return ResponseEntity.ok().body(ticketService.findTicketByNumber(ticketNumber));
    }

    @PutMapping("/cancel")
    public ResponseEntity<TicketResponseDto> cancelTicket(@RequestParam("ticketNumber") String ticketNumber) {
        return ResponseEntity.ok().body(ticketService.cancelTicket(ticketNumber));
    }

}
