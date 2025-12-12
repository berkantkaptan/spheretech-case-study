package com.spheretech.case_study.service;

import com.spheretech.case_study.dto.request.TicketRequestDto;
import com.spheretech.case_study.dto.response.TicketResponseDto;
import com.spheretech.case_study.mapper.TicketMapper;
import com.spheretech.case_study.model.Flight;
import com.spheretech.case_study.model.Ticket;
import com.spheretech.case_study.repository.TicketRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final FlightService flightService;
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Transactional
    public TicketResponseDto buyTicket(TicketRequestDto request) {

        Flight flight = flightService.findFlightEntityById(request.flightId());

        long soldTicketCount = ticketRepository.countByFlightIdAndActiveTrue(flight.getId());

        long allSoldTicketCount = ticketRepository.countByFlightId(flight.getId());

        if (soldTicketCount >= flight.getQuota()) {
            throw new RuntimeException("The quota is full for the flight");
        }

        BigDecimal currentPrice = calculateDynamicPrice(
                flight.getBasePrice(),
                flight.getQuota(),
                soldTicketCount
        );

        String maskedCard = maskCreditCard(request.creditCard());

        Ticket ticket = new Ticket();
        ticket.setFlight(flight);
        ticket.setTicketNumber(generateTicketNumber(flight.getRoute().getDestination().getName(), flight.getId(), allSoldTicketCount));
        ticket.setMaskedCreditCard(maskedCard);
        ticket.setCalculatedPrice(currentPrice);
        ticket.setActive(true);

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    public TicketResponseDto findTicketByNumber(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Ticket doesn't exist with the number:" + ticketNumber));

        return ticketMapper.toResponse(ticket);
    }

    @Transactional
    public TicketResponseDto cancelTicket(String ticketNumber) {
        Ticket ticket = ticketRepository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new RuntimeException("Ticket doesn't exist with the number:"+ ticketNumber));

        if (!ticket.isActive()) {
            throw new IllegalArgumentException("Ticket already cancelled");
        }

        ticket.setActive(false);

        return ticketMapper.toResponse(ticketRepository.save(ticket));
    }

    private String generateTicketNumber(String destinationName, Long flightId, Long soldTicketCount) {

        String destinationCode = destinationName
                .replaceAll("[^a-zA-Z]", "")
                .substring(0, 3)
                .toUpperCase(Locale.ROOT);

        long nextTicketNum = soldTicketCount + 1;

        return destinationCode
                + flightId.toString()
                + nextTicketNum;
    }

    private BigDecimal calculateDynamicPrice(BigDecimal basePrice, int quota, long soldTicketCount) {

        BigDecimal soldCountDecimal = BigDecimal.valueOf(soldTicketCount);
        BigDecimal quotaDecimal = BigDecimal.valueOf(quota);

        BigDecimal fullnessPercentage = soldCountDecimal
                .multiply(BigDecimal.valueOf(100))
                .divide(quotaDecimal, 0, RoundingMode.FLOOR);

        long increaseMultiplier = fullnessPercentage.divide(BigDecimal.TEN, 0, RoundingMode.FLOOR).longValue();

        BigDecimal multiplierFactor = BigDecimal.valueOf(1.10);

        BigDecimal cumulativeMultiplier = multiplierFactor.pow((int) increaseMultiplier);

        return basePrice
                .multiply(cumulativeMultiplier)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String maskCreditCard(String cardNumber) {
        String cleanCardNumber = cardNumber.replaceAll("[^0-9]", "");

        String cardNumberStart = cleanCardNumber.substring(0, 6);
        String cardNumberEnd = cleanCardNumber.substring(cleanCardNumber.length() - 4);

        return cardNumberStart + "******" + cardNumberEnd;
    }
}
