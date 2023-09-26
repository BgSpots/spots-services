package com.spots.repository;

import com.spots.domain.VerificationCode;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationCodeRepository extends MongoRepository<VerificationCode, Long> {
    Optional<VerificationCode> findVerificationCodeByCode(String verificationCode);
}
