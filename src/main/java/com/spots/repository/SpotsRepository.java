package com.spots.repository;

import com.spots.domain.Spot;
import com.spots.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpotsRepository extends MongoRepository<Spot, String> {
    boolean existsSpotByName(String name);

    boolean existsSpotById(String id);
}
