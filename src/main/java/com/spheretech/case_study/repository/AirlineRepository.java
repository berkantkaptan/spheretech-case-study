package com.spheretech.case_study.repository;

import com.spheretech.case_study.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirlineRepository extends JpaRepository<Airline,Long> {
    List<Airline> findByNameContainingIgnoreCase(String name);
}
