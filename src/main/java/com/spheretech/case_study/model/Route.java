package com.spheretech.case_study.model;

import com.spheretech.case_study.model.base.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "route")
@Data
public class Route extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "source_airport_id")
    private Airport source;

    @ManyToOne
    @JoinColumn(name = "destination_airport_id")
    private Airport destination;
}
