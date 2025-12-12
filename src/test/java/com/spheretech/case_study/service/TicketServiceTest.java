package com.spheretech.case_study.service;

import com.spheretech.case_study.dto.request.TicketRequestDto;
import com.spheretech.case_study.dto.response.TicketResponseDto;
import com.spheretech.case_study.mapper.TicketMapper;
import com.spheretech.case_study.model.Airport;
import com.spheretech.case_study.model.Flight;
import com.spheretech.case_study.model.Route;
import com.spheretech.case_study.model.Ticket;
import com.spheretech.case_study.repository.TicketRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private FlightService flightService;

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper ticketMapper;

    @InjectMocks
    private TicketService ticketService;

    private Flight createMockFlight(Long flightId, String destName, BigDecimal basePrice, int quota) {
        Flight flight = new Flight();
        flight.setId(flightId);
        flight.setBasePrice(basePrice);
        flight.setQuota(quota);

        Route route = new Route();
        Airport destination = new Airport();
        destination.setName(destName);
        route.setDestination(destination);

        Airport source = new Airport();
        source.setName("Istanbul");
        route.setSource(source);

        flight.setRoute(route);
        return flight;
    }


    @Test
    void buyTicket_shouldSaveAndReturnResponse_onSuccess() {
        Long flightId = 1L;
        String destName = "London";
        BigDecimal basePrice = BigDecimal.valueOf(100.00);
        String creditCard = "1234567890123456";

        TicketRequestDto request = new TicketRequestDto(flightId, creditCard);

        Flight mockFlight = createMockFlight(flightId, destName, basePrice, 100);
        Ticket savedTicket = new Ticket();
        savedTicket.setCalculatedPrice(BigDecimal.valueOf(100.00));
        savedTicket.setTicketNumber("LON11");

        TicketResponseDto expectedResponse = new TicketResponseDto();
        expectedResponse.setTicketNumber("LON11");

        when(flightService.findFlightEntityById(flightId)).thenReturn(mockFlight);
        when(ticketRepository.countByFlightIdAndActiveTrue(flightId)).thenReturn(0L);
        when(ticketRepository.countByFlightId(flightId)).thenReturn(0L);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);
        when(ticketMapper.toResponse(savedTicket)).thenReturn(expectedResponse);

        TicketResponseDto actualResponse = ticketService.buyTicket(request);

        assertNotNull(actualResponse);

        verify(flightService, times(1)).findFlightEntityById(flightId);
        verify(ticketRepository, times(1)).save(any(Ticket.class));

        verify(ticketRepository, times(1)).countByFlightId(flightId);
    }

    @Test
    void buyTicket_shouldThrowRuntimeException_whenQuotaIsFull() {
        Long flightId = 1L;
        TicketRequestDto request = new TicketRequestDto(flightId, "1234");

        Flight mockFlight = createMockFlight(flightId, "PARIS", BigDecimal.TEN, 10);

        when(flightService.findFlightEntityById(flightId)).thenReturn(mockFlight);
        when(ticketRepository.countByFlightIdAndActiveTrue(flightId)).thenReturn(10L);

        assertThrows(RuntimeException.class, () -> {
            ticketService.buyTicket(request);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
    }


    @Test
    void findTicketByNumber_shouldReturnResponseWhenFound() {
        String ticketNum = "IST11";
        Ticket mockTicket = new Ticket();
        TicketResponseDto expectedResponse = new TicketResponseDto();
        expectedResponse.setTicketNumber(ticketNum);

        when(ticketRepository.findByTicketNumber(ticketNum)).thenReturn(Optional.of(mockTicket));
        when(ticketMapper.toResponse(mockTicket)).thenReturn(expectedResponse);

        TicketResponseDto actualResponse = ticketService.findTicketByNumber(ticketNum);

        assertEquals(ticketNum, actualResponse.getTicketNumber());
        verify(ticketRepository, times(1)).findByTicketNumber(ticketNum);
    }

    @Test
    void findTicketByNumber_shouldThrowRuntimeExceptionWhenNotFound() {
        String ticketNum = "NONEXIST";
        when(ticketRepository.findByTicketNumber(ticketNum)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            ticketService.findTicketByNumber(ticketNum);
        });

        verify(ticketRepository, times(1)).findByTicketNumber(ticketNum);
    }


    @Test
    void cancelTicket_shouldSetInactiveAndReturnResponse_onSuccess() {
        String ticketNum = "IST11";
        Ticket mockTicket = new Ticket();
        mockTicket.setActive(true);

        Ticket savedTicket = new Ticket();
        savedTicket.setActive(false);

        TicketResponseDto expectedResponse = new TicketResponseDto();
        expectedResponse.setActive(false);

        when(ticketRepository.findByTicketNumber(ticketNum)).thenReturn(Optional.of(mockTicket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(savedTicket);
        when(ticketMapper.toResponse(savedTicket)).thenReturn(expectedResponse);

        TicketResponseDto actualResponse = ticketService.cancelTicket(ticketNum);

        assertFalse(actualResponse.isActive());
        verify(ticketRepository, times(1)).save(mockTicket);
        assertFalse(mockTicket.isActive());
    }

    @Test
    void cancelTicket_shouldThrowIllegalArgumentException_whenAlreadyCancelled() {
        String ticketNum = "IST11";
        Ticket mockTicket = new Ticket();
        mockTicket.setActive(false);

        when(ticketRepository.findByTicketNumber(ticketNum)).thenReturn(Optional.of(mockTicket));

        assertThrows(IllegalArgumentException.class, () -> {
            ticketService.cancelTicket(ticketNum);
        });

        verify(ticketRepository, never()).save(any(Ticket.class));
    }


    private BigDecimal callCalculateDynamicPrice(BigDecimal basePrice, int quota, long soldTicketCount) {
        return calculatePriceUtility(basePrice, quota, soldTicketCount);
    }

    private BigDecimal calculatePriceUtility(BigDecimal basePrice, int quota, long soldTicketCount) {

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

    @Test
    void calculateDynamicPrice_shouldReturnBasePrice_whenQuotaIsUnder10Percent() {
        BigDecimal result = callCalculateDynamicPrice(BigDecimal.valueOf(100.00), 100, 9);
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP), result);
    }

    @Test
    void calculateDynamicPrice_shouldIncreaseBy10Percent_whenQuotaIs10To19Percent() {
        BigDecimal result = callCalculateDynamicPrice(BigDecimal.valueOf(100.00), 100, 10);
        assertEquals(BigDecimal.valueOf(110.00).setScale(2, RoundingMode.HALF_UP), result);

        BigDecimal result2 = callCalculateDynamicPrice(BigDecimal.valueOf(200.00), 100, 19);
        assertEquals(BigDecimal.valueOf(220.00).setScale(2, RoundingMode.HALF_UP), result2);
    }

    @Test
    void calculateDynamicPrice_shouldIncreaseCumulatively_whenQuotaIsOver20Percent() {
        BigDecimal result = callCalculateDynamicPrice(BigDecimal.valueOf(100.00), 100, 20);
        assertEquals(BigDecimal.valueOf(121.00).setScale(2, RoundingMode.HALF_UP), result);

        BigDecimal result2 = callCalculateDynamicPrice(BigDecimal.valueOf(100.00), 100, 90);
        assertEquals(BigDecimal.valueOf(235.79).setScale(2, RoundingMode.HALF_UP), result2);
    }


    @Test
    void maskCreditCard_shouldMaskCorrectly() throws Exception {
        java.lang.reflect.Method method = TicketService.class.getDeclaredMethod("maskCreditCard", String.class);
        method.setAccessible(true);

        String cleanCard = "1234567890123456";
        String expectedMasked = "123456******3456";

        String actualMasked = (String) method.invoke(ticketService, cleanCard);

        assertEquals(expectedMasked, actualMasked);

        String cardWithSpaces = "9876 5432 1098 7654";
        String expectedMasked2 = "987654******7654";
        String actualMasked2 = (String) method.invoke(ticketService, cardWithSpaces);

        assertEquals(expectedMasked2, actualMasked2);
    }
}