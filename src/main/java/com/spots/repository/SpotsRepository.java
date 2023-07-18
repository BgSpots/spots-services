package com.spots.repository;

import com.spots.domain.Spot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpotsRepository extends MongoRepository<Spot, String> {
    boolean existsSpotByName(String name);

    boolean existsSpotById(String id);
}
