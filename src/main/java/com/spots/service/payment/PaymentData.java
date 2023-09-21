package com.spots.service.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentData {
    private Data data;

    @lombok.Data
    public static class Data {
        private String id;
        private String description;
        private int price;
        private String status;

        @JsonProperty("created_at")
        private String createdAt;

        private int fee;

        @JsonProperty("fiat_value")
        private int fiatValue;

        private Object notes;

        @JsonProperty("order_id")
        private Object orderId;

        private Object[] onchain;
        private Lightning lightning;
        private Object metadata;
        private String address;
        private boolean exchanged;

        @JsonProperty("net_fiat_value")
        private int netFiatValue;

        @JsonProperty("missing_amt")
        private int missingAmt;

        @JsonProperty("settled_at")
        private Object settledAt;

        @JsonProperty("payment_method")
        private Object paymentMethod;

        private int ttl;

        @JsonProperty("desc_hash")
        private boolean descHash;

        @JsonProperty("hosted_checkout_url")
        private String hostedCheckoutUrl;
    }

    @lombok.Data
    public static class Lightning {
        private String id;
        private String status;
        private int price;
        private String payreq;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("expires_at")
        private String expiresAt;

        @JsonProperty("settled_at")
        private Object settledAt;

        @JsonProperty("checkout_id")
        private String checkoutId;
    }
}
