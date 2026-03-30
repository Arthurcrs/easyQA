package com.arthur.easy_qa.dto.testcase;

import com.arthur.easy_qa.domain.TestCasePriority;
import com.arthur.easy_qa.domain.TestCaseStatus;
import com.arthur.easy_qa.domain.TestCaseType;

import java.time.Instant;

public class TestCaseResponse {

    private String projectKey;
    private Long testCaseNumber;
    private String us;
    private TestCaseStatus status;
    private String feature;
    private String scenario;
    private String description;
    private TestCasePriority priority;
    private TestCaseType type;
    private Instant creationInstant;
    private Instant lastUpdateInstant;

    public TestCaseResponse(String projectKey,
                            Long testCaseNumber,
                            String us,
                            TestCaseStatus status,
                            String feature,
                            String scenario,
                            String description,
                            TestCasePriority priority,
                            TestCaseType type,
                            Instant creationInstant,
                            Instant lastUpdateInstant) {
        this.projectKey = projectKey;
        this.testCaseNumber = testCaseNumber;
        this.us = us;
        this.status = status;
        this.feature = feature;
        this.scenario = scenario;
        this.description = description;
        this.priority = priority;
        this.type = type;
        this.creationInstant = creationInstant;
        this.lastUpdateInstant = lastUpdateInstant;
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

    public TestCaseStatus getStatus() {
        return status;
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
}
