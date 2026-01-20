package com.example.backend.chargingstation.repository;

import com.example.backend.chargingstation.entity.RegionCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegionCodeRepository extends JpaRepository<RegionCode, String> {
}
