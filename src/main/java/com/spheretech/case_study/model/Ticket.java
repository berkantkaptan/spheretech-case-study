package com.spheretech.case_study.model;

import com.spheretech.case_study.model.base.AbstractEntity;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket")
@Data
public class Ticket extends AbstractEntity {

    private BigDecimal calculatedPrice;

    @ManyToOne
    @JoinColumn(name = "flight_id")
    private Flight flight;

    @Column
    private String ticketNumber;

    private boolean active;

    private String maskedCreditCard;
}
