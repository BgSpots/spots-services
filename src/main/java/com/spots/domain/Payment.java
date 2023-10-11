package com.spots.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document
public class Payment {
    private long id;
    private String opennodeId;
    private long userId;
    private String status;
    private int sats;
    private String lightningInvoice;
    private String uri;
    private boolean used;
    private boolean isAdWatched;

    @Transient public static final String SEQUENCE_NAME = "payment_sequence";
}
