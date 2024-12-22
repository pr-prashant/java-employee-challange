package com.reliaquest.api.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Prashant Patel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeleteEmployeeRequest {

    @NotBlank
    private String name;
}
