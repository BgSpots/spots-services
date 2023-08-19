package com.spots.repository;

import com.spots.domain.SpotConqueror;
import com.spots.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SpotConquerorRepository extends MongoRepository<SpotConqueror, String> {
    Optional<SpotConqueror> findSpotConquerorByUsername(String username);

    Page<SpotConqueror> findAllBySpotId(String spotId, Pageable pageable);


}
