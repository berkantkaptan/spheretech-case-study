package com.spheretech.case_study.mapper;

import com.spheretech.case_study.dto.response.TicketResponseDto;
import com.spheretech.case_study.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketResponseDto toResponse(Ticket ticket){
        TicketResponseDto ticketResponseDto = new TicketResponseDto();
        ticketResponseDto.setActive(ticket.isActive());
        ticketResponseDto.setTicketNumber(ticket.getTicketNumber());
        ticketResponseDto.setFlightDetail(ticket.getFlight().getRoute().getSource().getName() + " -> " + ticket.getFlight().getRoute().getDestination().getName());
        ticketResponseDto.setPaidPrice(ticket.getCalculatedPrice());
        ticketResponseDto.setMaskedCreditCard(ticket.getMaskedCreditCard());

        return ticketResponseDto;
    }
}
