package com.spots.repository;

import com.spots.domain.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<Payment, Long> {}
