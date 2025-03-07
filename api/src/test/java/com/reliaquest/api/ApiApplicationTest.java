package com.reliaquest.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = ApiApplication.class)
class ApiApplicationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetAllEmployees() throws Exception { //Sunil
        MvcResult result = mockMvc.perform(get("/api/v1/employee"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isNotEmpty();
    }

    @Test
    public void testGetEmployeesByNameSearch() throws Exception { //Sunil
        MvcResult result = mockMvc.perform(get("/api/v1/employee/search/Emmanuel"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).contains("Emmanuel Brakus");
    }

    @Test
    public void testGetEmployeeById() throws Exception {//Sunil
        MvcResult result = mockMvc.perform(get("/api/v1/employee/79a983b8-270b-43c1-8bb3-f532467afdf8"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).contains("Emmanuel Brakus");
    }

    @Test
    public void testGetHighestSalaryOfEmployees() throws Exception { //Sunil
        MvcResult result = mockMvc.perform(get("/api/v1/employee/highestSalary"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isEqualTo("492902");
    }

    @Test
    public void testGetTop10HighestEarningEmployeeNames() throws Exception { //Sunil
        MvcResult result = mockMvc.perform(get("/api/v1/employee/topTenHighestEarningEmployeeNames"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).contains("Emmanuel Brakus");
    }

    @Test
    public void testCreateEmployee() throws Exception {
        String employeeJson = Files.readString(Paths.get("src/test/resources/employee.json"), StandardCharsets.UTF_8);

        // Perform POST request
        MvcResult result = mockMvc.perform(post("/api/v1/employee")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(employeeJson))
                .andExpect(status().isCreated())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).contains("Sunil");
    }

    @Test
    public void testDeleteEmployeeById() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/v1/employee/79a983b8-270b-43c1-8bb3-f532467afdf8"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        assertThat(responseContent).isEqualTo("Emmanuel Brakus");
    }
}
