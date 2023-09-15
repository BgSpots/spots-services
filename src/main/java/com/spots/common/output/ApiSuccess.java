package com.spots.common.output;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiSuccess {
    public String action;
    public String message;
}
