package com.spots.repository;

import com.spots.domain.Review;
import com.spots.domain.Spot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface SpotsRepository extends MongoRepository<Spot, String> {
    boolean existsSpotByName(String name);

    boolean existsSpotById(String id);

}
