package com.arthur.easy_qa.dto.testcase;

import com.arthur.easy_qa.domain.testcase.TestCasePriority;
import com.arthur.easy_qa.domain.testcase.TestCaseStatus;
import com.arthur.easy_qa.domain.testcase.TestCaseType;

import java.time.Instant;
import java.util.Map;

public class TestCaseResponse {

    private String projectKey;
    private Long testCaseNumber;
    private String us;
    private String feature;
    private String scenario;
    private String description;
    private TestCaseStatus status;
    private TestCasePriority priority;
    private TestCaseType type;
    private Instant creationInstant;
    private Instant lastUpdateInstant;

    private Map<String, String> customFields;

    public TestCaseResponse(String projectKey, Long testCaseNumber, String us, String feature,
                            String scenario, String description, TestCaseStatus status,
                            TestCasePriority priority, TestCaseType type, Instant creationInstant,
                            Instant lastUpdateInstant, Map<String, String> customFields) {
        this.projectKey = projectKey;
        this.testCaseNumber = testCaseNumber;
        this.us = us;
        this.feature = feature;
        this.scenario = scenario;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.type = type;
        this.creationInstant = creationInstant;
        this.lastUpdateInstant = lastUpdateInstant;
        this.customFields = customFields;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public Long getTestCaseNumber() {
        return testCaseNumber;
    }

    public String getUs() {
        return us;
    }

    public String getFeature() {
        return feature;
    }

    public String getScenario() {
        return scenario;
    }

    public String getDescription() {
        return description;
    }

    public TestCaseStatus getStatus() {
        return status;
    }

    public TestCasePriority getPriority() {
        return priority;
    }

    public TestCaseType getType() {
        return type;
    }

    public Instant getCreationInstant() {
        return creationInstant;
    }

    public Instant getLastUpdateInstant() {
        return lastUpdateInstant;
    }

    public Map<String, String> getCustomFields() {
        return customFields;
    }
}