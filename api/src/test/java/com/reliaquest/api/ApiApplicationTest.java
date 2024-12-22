package com.reliaquest.api;

import com.reliaquest.api.controller.IEmployeeControllerImpl;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeRequest;
import com.reliaquest.api.services.IEmployeeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
public class ApiApplicationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEmployeeService employeeService;

    @InjectMocks
    private IEmployeeControllerImpl employeeController;

    @Test
    public void testGetEmployeeById() throws Exception {
        String employeeId = UUID.randomUUID().toString();
        Employee employee = new Employee(employeeId, "John Doe", 5000, 30, "Developer", "john.doe@example.com");
        when(employeeService.getEmployeeById(employeeId)).thenReturn(employee);

        mockMvc.perform(get("/employees/" + employeeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employeeId)))
                .andExpect(jsonPath("$.employee_name", is("John Doe")))
                .andExpect(jsonPath("$.employee_salary", is(5000)))
                .andExpect(jsonPath("$.employee_age", is(30)))
                .andExpect(jsonPath("$.employee_title", is("Developer")))
                .andExpect(jsonPath("$.employee_email", is("john.doe@example.com")));

        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    public void testAddEmployee() throws Exception {
        String employeeId = UUID.randomUUID().toString();
        Employee employee = new Employee(employeeId, "Jane Doe", 8000, 28, "Manager", "jane.doe@example.com");
        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(employee);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Jane Doe\",\"salary\":8000,\"age\":28,\"title\":\"Manager\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(employeeId)))
                .andExpect(jsonPath("$.employee_name", is("Jane Doe")))
                .andExpect(jsonPath("$.employee_salary", is(8000)))
                .andExpect(jsonPath("$.employee_age", is(28)))
                .andExpect(jsonPath("$.employee_title", is("Manager")))
                .andExpect(jsonPath("$.employee_email", is("jane.doe@example.com")));

        verify(employeeService, times(1)).createEmployee(any(EmployeeRequest.class));
    }

    @Test
    public void testDeleteEmployee() throws Exception {
        String employeeId = UUID.randomUUID().toString();
        when(employeeService.deleteEmployeeById(any(String.class))).thenReturn("true");

        mockMvc.perform(delete("/employees/" + employeeId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(true)));

        verify(employeeService, times(1)).deleteEmployeeById(employeeId);
    }

    @Test
    public void testGetAllEmployees() throws Exception {
        String employeeId1 = UUID.randomUUID().toString();
        String employeeId2 = UUID.randomUUID().toString();
        when(employeeService.getAllEmployees()).thenReturn(List.of(
                new Employee(employeeId1, "John Doe", 5000, 30, "Developer", "john.doe@example.com"),
                new Employee(employeeId2, "Jane Doe", 8000, 28, "Manager", "jane.doe@example.com")
        ));

        mockMvc.perform(get("/employees")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(employeeId1)))
                .andExpect(jsonPath("$[0].employee_name", is("John Doe")))
                .andExpect(jsonPath("$[0].employee_salary", is(5000)))
                .andExpect(jsonPath("$[0].employee_age", is(30)))
                .andExpect(jsonPath("$[0].employee_title", is("Developer")))
                .andExpect(jsonPath("$[0].employee_email", is("john.doe@example.com")))
                .andExpect(jsonPath("$[1].id", is(employeeId2)))
                .andExpect(jsonPath("$[1].employee_name", is("Jane Doe")))
                .andExpect(jsonPath("$[1].employee_salary", is(8000)))
                .andExpect(jsonPath("$[1].employee_age", is(28)))
                .andExpect(jsonPath("$[1].employee_title", is("Manager")))
                .andExpect(jsonPath("$[1].employee_email", is("jane.doe@example.com")));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    public void testGetEmployeesByNameSearch() throws Exception {
        String employeeId1 = UUID.randomUUID().toString();
        String employeeId2 = UUID.randomUUID().toString();
        when(employeeService.getEmployeesByNameSearch(anyString())).thenReturn(List.of(
                new Employee(employeeId1, "John Doe", 5000, 30, "Developer", "john.doe@example.com"),
                new Employee(employeeId2, "Johny Doe", 8000, 28, "Manager", "johny.doe@example.com")
        ));

        mockMvc.perform(get("/employees/search/john" )
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(employeeId1)))
                .andExpect(jsonPath("$[0].employee_name", is("John Doe")))
                .andExpect(jsonPath("$[0].employee_salary", is(5000)))
                .andExpect(jsonPath("$[0].employee_age", is(30)))
                .andExpect(jsonPath("$[0].employee_title", is("Developer")))
                .andExpect(jsonPath("$[0].employee_email", is("john.doe@example.com")))
                .andExpect(jsonPath("$[1].id", is(employeeId2)))
                .andExpect(jsonPath("$[1].employee_name", is("Johny Doe")))
                .andExpect(jsonPath("$[1].employee_salary", is(8000)))
                .andExpect(jsonPath("$[1].employee_age", is(28)))
                .andExpect(jsonPath("$[1].employee_title", is("Manager")))
                .andExpect(jsonPath("$[1].employee_email", is("johny.doe@example.com")));

        verify(employeeService, times(1)).getEmployeesByNameSearch(anyString());
    }

    @Test
    public void testGetHighestSalary() throws Exception {
        int highestSalary = 10000;
        when(employeeService.getHighestSalaryOfEmployees()).thenReturn(highestSalary);

        mockMvc.perform(get("/employees/highestSalary")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(highestSalary)));

        verify(employeeService, times(1)).getHighestSalaryOfEmployees();
    }

    @Test
    public void testGetTopTenHighestEarningEmployeeNames() throws Exception {
        List<String> topTenEmployees = List.of("John Doe", "Jane Doe", "Alice", "Bob", "Charlie", "David", "Eve", "Frank", "Grace", "Hank");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(topTenEmployees);

        mockMvc.perform(get("/employees/topTenHighestEarningEmployeeNames")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(10)))
                .andExpect(jsonPath("$[0]", is("John Doe")))
                .andExpect(jsonPath("$[1]", is("Jane Doe")))
                .andExpect(jsonPath("$[2]", is("Alice")))
                .andExpect(jsonPath("$[3]", is("Bob")))
                .andExpect(jsonPath("$[4]", is("Charlie")))
                .andExpect(jsonPath("$[5]", is("David")))
                .andExpect(jsonPath("$[6]", is("Eve")))
                .andExpect(jsonPath("$[7]", is("Frank")))
                .andExpect(jsonPath("$[8]", is("Grace")))
                .andExpect(jsonPath("$[9]", is("Hank")));

        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }
}