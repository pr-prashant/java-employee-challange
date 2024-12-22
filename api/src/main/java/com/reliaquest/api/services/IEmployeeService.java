package com.reliaquest.api.services;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeRequest;

import java.util.List;

/**
 * @author Prashant Patel
 */
public interface IEmployeeService {
    List<Employee> getAllEmployees();

    List<Employee> getEmployeesByNameSearch(String searchString);

    Employee getEmployeeById(String id);

    Integer getHighestSalaryOfEmployees();

    List<String> getTopTenHighestEarningEmployeeNames();

    Employee createEmployee(EmployeeRequest employeeInput);

    String deleteEmployeeById(String id);
}
