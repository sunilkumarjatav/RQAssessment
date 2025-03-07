package com.reliaquest.api.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.model.EmployeeDTO;
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
public class RestApiClient {
    private static final Logger logger = LoggerFactory.getLogger(RestApiClient.class);
    ObjectMapper objectMapper = new ObjectMapper();

    @Value("${mock.employee.api.url}")
    private String mockEmployeeApiUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public RestApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        logger.info("RestApiClient constructor called");
    }

    public List<EmployeeDTO> getEmployeeList(String path) {
        ResponseEntity<List<EmployeeDTO>> response = getEmployees(path);
        return (response.getStatusCode() == HttpStatus.OK && response.getBody() != null)
                ? response.getBody()
                : Collections.emptyList();
    }

    public ResponseEntity<List<EmployeeDTO>> getEmployees(String path) {
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

    public EmployeeDTO getEmployee(String path) {
        ResponseEntity<EmployeeDTO> response = getEmployeeDto(path);
        return (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) ? response.getBody() : null;
    }

    public ResponseEntity<EmployeeDTO> getEmployeeDto(String path) {
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
                    EmployeeDTO employeeDTO = objectMapper.treeToValue(dataNode, EmployeeDTO.class);
                    return ResponseEntity.ok(employeeDTO);
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

    public EmployeeDTO createEmployee(String path, Object requestBody) {
        ResponseEntity<EmployeeDTO> response = postEmployee(path, requestBody);
        return (response.getStatusCode() == HttpStatus.CREATED && response.getBody() != null)
                ? response.getBody()
                : null;
    }

    public ResponseEntity<EmployeeDTO> postEmployee(String path, Object requestBody) {
        String url = mockEmployeeApiUrl + path;
        logger.info("POST request to: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<EmployeeDTO> response = restTemplate.exchange(
                    url, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<EmployeeDTO>() {});

            logger.info("Response: {}", response.getBody());
            return response;
        } catch (RestClientException e) {
            logger.error("Error posting employee to: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Void> deleteEmployee(String path) {
        String url = mockEmployeeApiUrl + path;
        logger.info("DELETE request to: {}", url);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            restTemplate.exchange(url, HttpMethod.DELETE, requestEntity, Void.class);
            return ResponseEntity.noContent().build();
        } catch (HttpClientErrorException e) {
            logger.error("Client error deleting employee at: {}", url, e);
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (RestClientException e) {
            logger.error("Error deleting employee at: {}", url, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
