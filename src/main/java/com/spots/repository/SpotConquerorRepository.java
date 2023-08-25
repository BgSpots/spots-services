package com.spots.repository;

import com.spots.domain.SpotConqueror;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SpotConquerorRepository extends MongoRepository<SpotConqueror, String> {
    Optional<SpotConqueror> findSpotConquerorByUsername(String username);

    Page<SpotConqueror> findAllBySpotId(Long spotId, Pageable pageable);
}
