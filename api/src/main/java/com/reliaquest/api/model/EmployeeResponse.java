package com.reliaquest.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeResponse {
    @JsonProperty("data")
    private List<EmployeeDTO> data;

    public List<EmployeeDTO> getData() {
        return data;
    }

    public void setData(List<EmployeeDTO> data) {
        this.data = data;
    }
}
