package com.example.library_be.repository;

import com.example.library_be.entity.FinePolicy;
import com.example.library_be.entity.enums.DamageLevel;
import com.example.library_be.entity.enums.FineType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FinePolicyRepository extends JpaRepository<FinePolicy, UUID> {

    List<FinePolicy> findByTypeAndIsActiveTrueOrderByDayFromAsc(FineType type);

    Optional<FinePolicy> findByTypeAndDamageLevelAndIsActiveTrue(
            FineType type,
            DamageLevel damageLevel
    );
}
