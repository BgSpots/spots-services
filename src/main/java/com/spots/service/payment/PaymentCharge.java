package com.spots.service.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCharge {
    private Data data;

    @lombok.Data
    public static class Data {
        private String id;
        private String description;
        private boolean desc_hash;
        private long created_at;
        private String status;
        private int amount;
        private String callback_url;
        private String success_url;
        private String hosted_checkout_url;
        private String order_id;
        private String currency;
        private int source_fiat_value;
        private int fiat_value;
        private boolean auto_settle;
        private String notif_email;
        private String address;
        private ChainInvoice chain_invoice;
        private String uri;
        private int ttl;
        private LightningInvoice lightning_invoice;
    }

    @lombok.Data
    public static class ChainInvoice {
        private String address;
    }

    @lombok.Data
    public static class LightningInvoice {
        private long expires_at;
        private String payreq;
    }
}
