package com.spots.common.input;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitiatePaymentBody {
    private int amount;
    private boolean isAd;
}
