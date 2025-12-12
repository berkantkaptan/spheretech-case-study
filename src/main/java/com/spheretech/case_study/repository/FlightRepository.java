package com.spheretech.case_study.repository;

import com.spheretech.case_study.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface FlightRepository extends JpaRepository<Flight,Long>, JpaSpecificationExecutor<Flight> {

}
