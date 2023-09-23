package com.spots.repository;

import com.spots.domain.Payment;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, Long> {
    Optional<Payment> findPaymentByUserId(long userId);

    boolean existsByUserId(long userId);
}
