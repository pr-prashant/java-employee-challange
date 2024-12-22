package com.reliaquest.api.model;

import lombok.Data;

/**
 * @author Prashant Patel
 */
@Data
public class ApiResponse<T> {
    private T data;
    private String error;
    private String status;
}
