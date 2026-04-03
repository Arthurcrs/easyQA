package com.arthur.easy_qa.dto.testcase;

import com.arthur.easy_qa.domain.TestCasePriority;
import com.arthur.easy_qa.domain.TestCaseStatus;
import com.arthur.easy_qa.domain.TestCaseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.HashMap;
import java.util.Map;

public class CreateTestCaseRequest {

    @NotNull(message = "Status is required")
    private TestCaseStatus status;

    @NotBlank(message = "User Story is required")
    private String us;

    private String feature;

    @NotBlank(message = "Scenario is required")
    private String scenario;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Priority is required")
    private TestCasePriority priority;

    @NotNull(message = "Type is required")
    private TestCaseType type;

    private Map<Long, String> customFields = new HashMap<>();

    public CreateTestCaseRequest() {
    }

    public TestCaseStatus getStatus() {
        return status;
    }

    public void setStatus(TestCaseStatus status) {
        this.status = status;
    }

    public String getUs() {
        return us;
    }

    public void setUs(String us) {
        this.us = us;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TestCasePriority getPriority() {
        return priority;
    }

    public void setPriority(TestCasePriority priority) {
        this.priority = priority;
    }

    public TestCaseType getType() {
        return type;
    }

    public void setType(TestCaseType type) {
        this.type = type;
    }

    public Map<Long, String> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(Map<Long, String> customFields) {
        this.customFields = customFields;
    }
}