package com.spheretech.case_study.model;

import com.spheretech.case_study.model.base.AbstractEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "airline")
@Getter
@Setter
@NoArgsConstructor
public class Airline extends AbstractEntity {

    private String name;
}
