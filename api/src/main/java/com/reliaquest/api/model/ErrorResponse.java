package com.reliaquest.api.model;

import lombok.Data;

/**
 * @author Prashant Patel
 */
@Data
public class ErrorResponse {
    private String error;
    private String status;
}
