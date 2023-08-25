package com.spots.repository;

import com.spots.domain.Spot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpotsRepository extends MongoRepository<Spot, Long> {
    boolean existsSpotByName(String name);

    boolean existsSpotById(Long id);
}
