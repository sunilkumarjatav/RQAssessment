package com.reliaquest.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponse;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class EmployeeApiClient implements IEmployeeApiClient {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeApiClient.class);

    @Value("${mock.employee.api.url}")
    private String mockEmployeeApiUrl;

    private final RestTemplate restTemplate;

    private final ObjectMapper objectMapper;

    @Autowired
    public EmployeeApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public List<Employee> getEmployees(String path) {
        ResponseEntity<List<Employee>> response = getEmployeeList(path);
        return (response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
                ? response.getBody()
                : Collections.emptyList();
    }

    @Override
    public Employee getEmployee(String path) {
        ResponseEntity<Employee> response = getSingleEmployee(path);
        return (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) ? response.getBody() : null;
    }

    @Override
    public Employee createEmployee(String path, Object requestBody) {
        ResponseEntity<Employee> response = postEmployee(path, requestBody);
        return (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null)
                ? response.getBody()
                : null;
    }

    @Override
    public Employee deleteEmployee(String path) {
        ResponseEntity<Employee> response = deleteSingleEmployee(path);
        return (response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
                ? response.getBody()
                : null;
    }

    private ResponseEntity<List<Employee>> getEmployeeList(String path) {
        String url = mockEmployeeApiUrl + path;
        logger.info("GET request to: {}", url);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<String>() {});

            logger.info("Raw Response: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                EmployeeResponse employeeResponse = objectMapper.readValue(response.getBody(), EmployeeResponse.class);

                return ResponseEntity.ok(employeeResponse.getData());
            }
            return ResponseEntity.noContent().build();

        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Not found: {}", url);
            return ResponseEntity.notFound().build();
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON response from: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RestClientException e) {
            logger.error("Error fetching employees from: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Employee> getSingleEmployee(String path) {
        String url = mockEmployeeApiUrl + path;
        logger.info("GET request to: {}", url);

        try {
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<String>() {});

            logger.info("Raw Response: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.get("data");

                if (dataNode != null && dataNode.isObject()) {
                    Employee employee = objectMapper.treeToValue(dataNode, Employee.class);
                    return ResponseEntity.ok(employee);
                }
            }
            return ResponseEntity.noContent().build();

        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("Not found: {}", url);
            return ResponseEntity.notFound().build();
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON response from: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RestClientException e) {
            logger.error("Error fetching employee from: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Employee> postEmployee(String path, Object requestBody) {
        String url = mockEmployeeApiUrl + path;
        logger.info("POST request to: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("data");

                Employee employee = objectMapper.treeToValue(dataNode, Employee.class);
                logger.info("Created Employee: {}", employee);

                return ResponseEntity.status(HttpStatus.CREATED).body(employee);
            } else {
                logger.error("Failed to create employee, Response: {}", response);
                return ResponseEntity.status(response.getStatusCode()).build();
            }
        } catch (RestClientException e) {
            logger.error("Error posting employee to: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Unexpected error while parsing response", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Employee> deleteSingleEmployee(String path) {
        String url = mockEmployeeApiUrl + path;
        logger.info("DELETE request to: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode rootNode = objectMapper.readTree(response.getBody());
                JsonNode dataNode = rootNode.path("data");

                Employee deletedEmployee = objectMapper.treeToValue(dataNode, Employee.class);
                logger.info("Deleted Employee: {}", deletedEmployee);

                return ResponseEntity.ok(deletedEmployee);
            }
            return ResponseEntity.noContent().build();

        } catch (HttpClientErrorException e) {
            logger.error("Client error deleting employee at: {}", url, e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON response from DELETE: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (RestClientException e) {
            logger.error("Error deleting employee at: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
