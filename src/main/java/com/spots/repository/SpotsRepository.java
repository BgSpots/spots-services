package com.spots.repository;

import com.spots.domain.Spot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpotsRepository extends MongoRepository<Spot, Long> {
    boolean existsSpotByName(String name);

    boolean existsSpotById(Long id);
    Optional<Spot> findFirstByOrderByIdAsc();
    Optional<Spot> findFirstByOrderByIdDesc();
}
