package com.reliaquest.api.service;

import com.reliaquest.api.client.EmployeeApiClient;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDTO;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService implements IEmployeeService {

    private final EmployeeApiClient restApiClient;

    @Autowired
    public EmployeeService(EmployeeApiClient restApiClient) {
        this.restApiClient = restApiClient;
    }

    @Override
    public List<Employee> getAllEmployees() {
        return restApiClient.getEmployees("");
    }

    @Override
    public List<Employee> getEmployeesByNameSearch(String searchString) {
        List<Employee> allEmployees = getAllEmployees();
        if (allEmployees == null) {
            return Collections.emptyList();
        }
        return allEmployees.stream()
                .filter(employee -> employee.getName().toLowerCase().contains(searchString.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Employee getEmployeeById(String id) {
        return restApiClient.getEmployee("/" + id);
    }

    @Override
    public Integer getHighestSalaryOfEmployees() {
        List<Employee> allEmployees = getAllEmployees();
        if (allEmployees == null) {
            return 0;
        }
        return allEmployees.stream()
                .max(Comparator.comparingInt(Employee::getSalary))
                .map(Employee::getSalary)
                .orElse(0);
    }

    @Override
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

    @Override
    public Employee createEmployee(EmployeeDTO employeeInput) {
        employeeInput.setId(UUID.randomUUID().toString());
        return restApiClient.createEmployee("", employeeInput);
    }

    @Override
    public String deleteEmployeeById(String id) {
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            return null;
        }
        Employee deletedEmployee = restApiClient.deleteEmployee("/" + id);
        return deletedEmployee != null ? deletedEmployee.getName() : null;
    }

}
