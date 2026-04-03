package com.arthur.easy_qa.dto.customfield;

import com.arthur.easy_qa.domain.customfield.CustomFieldType;

public class CustomFieldResponse {

    private String projectKey;
    private Long fieldNumber;
    private String name;
    private CustomFieldType type;
    private String options;

    public CustomFieldResponse(String projectKey, Long fieldNumber, String name,
                               CustomFieldType type, String options) {
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

    public String getOptions() {
        return options;
    }
}