package com.spots.domain;

import lombok.Builder;
import lombok.Data;
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
}
