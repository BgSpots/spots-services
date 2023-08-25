package com.spots.repository;

import com.spots.domain.User;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findUserByEmail(String email);

    @Query(value = "{'$sample': {'size': 1}}")
    User findRandomEntity();

    boolean existsUserByEmail(String email);
}
