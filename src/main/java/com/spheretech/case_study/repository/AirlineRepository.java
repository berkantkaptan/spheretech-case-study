package com.spheretech.case_study.repository;

import com.spheretech.case_study.model.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline,Long> {
    List<Airline> findByNameContainingIgnoreCase(String name);
}
