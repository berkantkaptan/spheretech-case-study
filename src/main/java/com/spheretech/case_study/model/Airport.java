package com.spheretech.case_study.model;

import com.spheretech.case_study.model.base.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "airport")
@Data
public class Airport extends AbstractEntity {

    private String name;

    private String city;

    private String country;
}
