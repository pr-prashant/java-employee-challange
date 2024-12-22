package com.reliaquest.api.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Prashant Patel
 */
@Data
@Component
@ConfigurationProperties(prefix = "url")
public class UrlProperties {

    private String baseUrl;
    private String employee;
    private String employeeById;
}
