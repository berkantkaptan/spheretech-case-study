package com.spheretech.case_study.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TicketResponseDto {
    private String ticketNumber;
    private BigDecimal paidPrice;
    private String maskedCreditCard;
    private String flightDetail;
    private boolean isActive;
}
