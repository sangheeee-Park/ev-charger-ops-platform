package com.example.backend.chargingstation.repository;

import com.example.backend.chargingstation.entity.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, String> {
}
