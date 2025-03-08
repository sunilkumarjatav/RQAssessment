package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeDTO {
    private String id;

    @NotBlank(message = "Name cannot be blank")
    @JsonProperty("name")
    private String employee_name;

    @Min(value = 1, message = "Salary must be greater than zero")
    @JsonProperty("salary")
    private int employee_salary;

    @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 75, message = "Age must be at most 75")
    @JsonProperty("age")
    private int employee_age;

    @NotBlank(message = "Title cannot be blank")
    @JsonProperty("title")
    private String employee_title;

    @Email(message = "Email must be valid")
    @JsonProperty("email")
    private String employee_email;
}
