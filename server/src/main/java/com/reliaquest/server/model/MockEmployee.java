package com.reliaquest.server.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(MockEmployee.PrefixNamingStrategy.class)
public class MockEmployee {

    private UUID id;
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
    private String email;

    public static MockEmployee from(@NonNull String email, @NonNull CreateMockEmployeeInput input) {
        return MockEmployee.builder()
                .id(UUID.randomUUID())
                .email(email)
                .name(input.getName())
                .salary(input.getSalary())
                .age(input.getAge())
                .title(input.getTitle())
                .build();
    }

    static class PrefixNamingStrategy extends PropertyNamingStrategies.NamingBase {

        @Override
        public String translate(String propertyName) {
            if ("id".equals(propertyName)) {
                return propertyName;
            }
            return "employee_" + propertyName;
        }
    }
}
