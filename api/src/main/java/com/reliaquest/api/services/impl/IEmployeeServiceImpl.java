package com.reliaquest.api.services.impl;

import com.reliaquest.api.exceptionhandler.ApiException;
import com.reliaquest.api.model.*;
import com.reliaquest.api.properties.UrlProperties;
import com.reliaquest.api.services.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Prashant Patel
 */
@Slf4j
@Service
public class IEmployeeServiceImpl implements IEmployeeService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UrlProperties properties;

    @Override
    public List<Employee> getAllEmployees() {
        String url = properties.getEmployee();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<ApiResponse<List<Employee>>> typeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<ApiResponse<List<Employee>>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), typeReference);
        if (response.getBody() != null) {
            return response.getBody().getData();
        }
        return new ArrayList<>();
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        return getAllEmployees().stream().filter(e -> e.getName().toLowerCase().contains(searchString.toLowerCase())).toList();
    }

    @Override
    public Employee getEmployeeById(String id) {
        String url = String.format(properties.getEmployeeById(), id);
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ParameterizedTypeReference<ApiResponse<Employee>> typeReference = new ParameterizedTypeReference<>() {
        };
        try {
            ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), typeReference);
            if (response.getBody() != null) {
                return response.getBody().getData();
            }
        } catch (HttpStatusCodeException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new ApiException(HttpStatus.NOT_FOUND, "Employee with " + id + " not found.");
            }
            throw e;
        }
        return null;
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        return getAllEmployees().stream().mapToInt(Employee::getSalary).max().orElse(0);
    }

    @Override
    public List<String> getTopTenHighestEarningEmployeeNames() {
        return getAllEmployees().stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(10).map(Employee::getName).toList();
    }

    @Override
    public Employee createEmployee(EmployeeRequest employeeInput) {
        String url = properties.getEmployee();
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EmployeeRequest> httpEntity = new HttpEntity<>(employeeInput, headers);
        ParameterizedTypeReference<ApiResponse<Employee>> typeReference = new ParameterizedTypeReference<>() {
        };
        ResponseEntity<ApiResponse<Employee>> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, typeReference);
        if (response.getBody() != null) {
            return response.getBody().getData();
        }
        return null;
    }

    @Override
    public String deleteEmployeeById(String id) {
        Employee employeeById = getEmployeeById(id);
        if (employeeById != null) {
            String url = properties.getEmployee();
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            DeleteEmployeeRequest deleteEmployeeRequest = new DeleteEmployeeRequest(employeeById.getName());
            HttpEntity<DeleteEmployeeRequest> httpEntity = new HttpEntity<>(deleteEmployeeRequest, headers);
            ParameterizedTypeReference<ApiResponse<Boolean>> typeReference = new ParameterizedTypeReference<>() {
            };
            try {
                ResponseEntity<ApiResponse<Boolean>> response = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, typeReference);
                if (response.getBody() != null) {
                    if (response.getBody().getData()) {
                        return employeeById.getName();
                    }
                }
            } catch (HttpStatusCodeException e) {
                if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                    throw new ApiException(HttpStatus.NOT_FOUND, "Employee with " + id + " not found.");
                }
                throw e;
            }
        }
        return "";
    }
}
