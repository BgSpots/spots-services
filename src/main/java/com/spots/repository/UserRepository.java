package com.spots.repository;

import com.spots.domain.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByEmail(String email);

    boolean existsUserByEmail(String email);
}