package com.spots.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class VerificationCode {
    private Long id;
    private String email;
    private String code;
}
