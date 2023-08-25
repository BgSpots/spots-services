package com.spots.repository;

import com.spots.domain.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReviewRepository extends MongoRepository<Review, String> {
    Page<Review> findAllBySpotId(Long spotId, Pageable pageable);
}
