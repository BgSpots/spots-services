package com.spots.common.output;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

/** Object returned from the api in case error occurs */
@Data
@AllArgsConstructor
public class ApiError {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String path;
}
