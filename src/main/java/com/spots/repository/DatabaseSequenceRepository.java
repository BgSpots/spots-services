package com.spots.repository;

import com.spots.domain.DatabaseSequence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DatabaseSequenceRepository extends MongoRepository<DatabaseSequence, String> {}
