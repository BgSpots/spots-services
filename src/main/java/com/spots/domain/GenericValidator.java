package com.spots.domain;

import com.spots.service.auth.AuthenticationService;
import com.spots.service.auth.InvalidInputException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@Component
public class GenericValidator<T> {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    public void validate(T entity) {
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        List<String> violationsMsg = new ArrayList<>();
        for (ConstraintViolation<T> violation : violations) {
            violationsMsg.add(violation.getMessage());
            logger.error(violation.getMessage());
        }
        if (!violationsMsg.isEmpty()) {
            throw new InvalidInputException(violationsMsg.toString());
        }
    }
}
