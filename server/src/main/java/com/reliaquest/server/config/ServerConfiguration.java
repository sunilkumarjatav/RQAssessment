package com.reliaquest.server.config;

import com.reliaquest.server.model.MockEmployee;
import com.reliaquest.server.web.RandomRequestLimitInterceptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import net.datafaker.transformations.Field;
import net.datafaker.transformations.JavaObjectTransformer;
import net.datafaker.transformations.Schema;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Slf4j
@Configuration
public class ServerConfiguration implements WebMvcConfigurer {

    public static final String EMAIL_TEMPLATE = "%s@company.com";

    @Bean
    public Faker faker() {
        return new Faker(Locale.getDefault());
    }

    /*
     * This list is modifiable by design for CRUD operations.
     */
    @Bean
    public List<MockEmployee> mockEmployees(Faker faker, @Value("${mock.employees.max:20}") int maxEmployees) {
        final var transformer = new JavaObjectTransformer();
        final var schema = Schema.of(
                Field.field("id", UUID::randomUUID),
                Field.field("name", () -> faker.name().fullName()),
                Field.field("salary", () -> faker.number().numberBetween(30000, 500000)),
                Field.field("age", () -> faker.number().numberBetween(16, 70)),
                Field.field("title", () -> faker.job().title()),
                Field.field(
                        "email",
                        () -> EMAIL_TEMPLATE.formatted(
                                faker.twitter().userName().toLowerCase())));
        return IntStream.rangeClosed(1, maxEmployees)
                .mapToObj(ignored -> (MockEmployee) transformer.apply(MockEmployee.class, schema))
                .peek(mockEmployee -> log.debug("Created employee: {}", mockEmployee))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RandomRequestLimitInterceptor());
    }
}
