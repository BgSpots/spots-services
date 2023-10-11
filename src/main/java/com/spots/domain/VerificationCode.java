package com.spots.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class VerificationCode {
    private long id;
    private String email;
    private String code;
    @Transient public static final String SEQUENCE_NAME = "code_sequence";
}
