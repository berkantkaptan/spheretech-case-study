package com.spheretech.case_study.model;

import com.spheretech.case_study.model.base.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flight")
@Data
public class Flight extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "airline_id")
    private Airline airline;

    @ManyToOne
    @JoinColumn(name = "route_id")
    private Route route;

    private Integer quota;

    private BigDecimal basePrice;

    private LocalDateTime departureDate;
}
