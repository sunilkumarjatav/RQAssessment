package com.reliaquest.api.client;

import com.reliaquest.api.model.Employee;
import java.util.List;

public interface IEmployeeApiClient {

    List<Employee> getEmployees(String path);

    Employee getEmployee(String path);

    Employee createEmployee(String path, Object requestBody);

    Employee deleteEmployee(String path);
}
