package com.arthur.easy_qa.dto;

import com.arthur.easy_qa.domain.TestCasePriority;
import com.arthur.easy_qa.domain.TestCaseStatus;
import com.arthur.easy_qa.domain.TestCaseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateTestCaseRequest {

    @NotNull
    TestCaseStatus status;
    @NotNull
    private String us;
    private String feature;
    @NotBlank
    private String scenario;
    @NotBlank
    String description;
    @NotNull
    private TestCasePriority priority;
    @NotNull
    TestCaseType type;

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
}
