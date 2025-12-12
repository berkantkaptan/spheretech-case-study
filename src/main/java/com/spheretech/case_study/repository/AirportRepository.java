package com.spheretech.case_study.repository;

import com.spheretech.case_study.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportRepository extends JpaRepository<Airport,Long> {

    List<Airport> findAirportsByCity(String city);

    List<Airport> findAirportsByCountry(String country);

    List<Airport> findByNameContainingIgnoreCase(String name);
}
