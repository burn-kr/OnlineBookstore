package com.avenga.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Represents an HTTP error response object
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private String type;
    private String title;
    private String status;
    private String traceId;
}
