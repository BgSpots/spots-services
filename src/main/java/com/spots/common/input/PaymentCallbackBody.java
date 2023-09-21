package com.spots.common.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCallbackBody {
    private String id;
    private String callback_url;
    private String success_url;
    private String status;
    private String order_id;
    private String description;
    private String price;
    private String fee;
    private String auto_settle;
    private String address;
    private String missing_amt;
    private String overpaid_by;
    private String hashed_order;
}
