package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeDTO;
import com.reliaquest.api.service.EmployeeService;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
public class EmployeeController implements IEmployeeController<Employee, EmployeeDTO> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Fetching all employees.");
        List<Employee> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("Searching employees by name: {}", searchString);
        List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(String id) {
        logger.info("Fetching employee by id: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        if (employee != null) {
            return ResponseEntity.ok(employee);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Fetching highest salary.");
        int highestSalary = employeeService.getHighestSalaryOfEmployees();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Fetching top 10 highest earning employee names.");
        List<String> employeeNames = employeeService.getTop10HighestEarningEmployeeNames();
        return ResponseEntity.ok(employeeNames);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeDTO employeeInput) {
        logger.info("Creating employee: {}", employeeInput);
        try {
            Employee createdEmployee = employeeService.createEmployee(employeeInput);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmployee);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid employee input: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable("id") String id) {
        logger.info("Deleting employee by id: {}", id);
        String employeeName = employeeService.deleteEmployeeById(id);
        if (employeeName != null) {
            return ResponseEntity.ok(employeeName);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
