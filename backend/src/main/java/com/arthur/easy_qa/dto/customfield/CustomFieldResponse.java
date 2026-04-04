package com.arthur.easy_qa.dto.customfield;

import com.arthur.easy_qa.domain.customfield.CustomFieldType;

import java.util.List;

public class CustomFieldResponse {

    private String projectKey;
    private Long fieldNumber;
    private String name;
    private CustomFieldType type;
    private List<CustomFieldOptionResponse> options;

    public CustomFieldResponse(String projectKey,
                               Long fieldNumber,
                               String name,
                               CustomFieldType type,
                               List<CustomFieldOptionResponse> options) {
        this.projectKey = projectKey;
        this.fieldNumber = fieldNumber;
        this.name = name;
        this.type = type;
        this.options = options;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public Long getFieldNumber() {
        return fieldNumber;
    }

    public String getName() {
        return name;
    }

    public CustomFieldType getType() {
        return type;
    }

    public List<CustomFieldOptionResponse> getOptions() {
        return options;
    }
}