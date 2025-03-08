package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDTO;
import java.util.List;

public interface IEmployeeService {
    List<Employee> getAllEmployees();
    List<Employee> getEmployeesByNameSearch(String searchString);
    Employee getEmployeeById(String id);
    Integer getHighestSalaryOfEmployees();
    List<String> getTop10HighestEarningEmployeeNames();
    Employee createEmployee(EmployeeDTO employeeInput);
    String deleteEmployeeById(String id);
}
