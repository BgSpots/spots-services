package com.spots.repository;

import com.spots.domain.Spot;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpotsRepository extends MongoRepository<Spot, Long> {
    boolean existsSpotByName(String name);

    boolean existsSpotById(Long id);

    Optional<Spot> findFirstByOrderByIdAsc();

    Optional<Spot> findFirstByOrderByIdDesc();
}
