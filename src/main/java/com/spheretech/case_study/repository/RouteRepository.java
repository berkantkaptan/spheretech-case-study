package com.spheretech.case_study.repository;

import com.spheretech.case_study.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRepository extends JpaRepository<Route,Long>, JpaSpecificationExecutor<Route> {

    boolean existsBySourceIdAndDestinationId(Long sourceId, Long destinationId);
}
