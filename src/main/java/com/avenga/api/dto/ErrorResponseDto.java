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
public class ErrorResponseDto {

    private String type;
    private String title;
    private String status;
    private String traceId;
}
