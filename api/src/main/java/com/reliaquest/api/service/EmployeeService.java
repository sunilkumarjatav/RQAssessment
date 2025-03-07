package com.reliaquest.api.service;

import com.reliaquest.api.client.RestApiClient;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDTO;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final RestApiClient restApiClient;

    @Autowired
    public EmployeeService(RestApiClient restApiClient) {
        this.restApiClient = restApiClient;
        logger.info("EmployeeService constructor called");
    }

    public List<Employee> getAllEmployees() {
        List<EmployeeDTO> employeeDTOs = restApiClient.getEmployeeList("");
        if (employeeDTOs != null) {
            return employeeDTOs.stream().map(this::mapDtoToEmployee).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Employee> getEmployeesByNameSearch(String searchString) {
        List<Employee> allEmployees = getAllEmployees();
        if (allEmployees == null) {
            return Collections.emptyList();
        }
        return allEmployees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        EmployeeDTO employeeDTO = restApiClient.getEmployee("/" + id);
        if (employeeDTO != null) {
            return mapDtoToEmployee(employeeDTO);
        }
        return null;
    }

    public int getHighestSalaryOfEmployees() {
        List<Employee> allEmployees = getAllEmployees();
        if (allEmployees == null) {
            return 0;
        }
        return allEmployees.stream()
                .max(Comparator.comparingInt(Employee::getSalary))
                .map(Employee::getSalary)
                .orElse(0);
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        List<Employee> allEmployees = getAllEmployees();
        if (allEmployees == null) {
            return null;
        }
        return allEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(EmployeeDTO employeeInput) {
        EmployeeDTO employeeDTO = restApiClient.createEmployee("", employeeInput);
        if (employeeDTO != null) {
            return mapDtoToEmployee(employeeDTO);
        }
        return null;
    }

    public String deleteEmployeeById(String id) {
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            return null;
        }
        restApiClient.deleteEmployee("/" + id);
        return employee.getName();
    }

    private Employee mapDtoToEmployee(EmployeeDTO dto) {
        Employee employee = new Employee();
        employee.setId(dto.getId());
        employee.setName(dto.getEmployee_name());
        employee.setSalary(dto.getEmployee_salary());
        employee.setAge(dto.getEmployee_age());
        employee.setTitle(dto.getEmployee_title());
        employee.setEmail(dto.getEmployee_email());
        return employee;
    }
}
