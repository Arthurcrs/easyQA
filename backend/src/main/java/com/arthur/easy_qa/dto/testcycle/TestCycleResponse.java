package com.arthur.easy_qa.dto.testcycle;

import java.time.Instant;

public class TestCycleResponse {

    private String projectKey;
    private Long testCycleNumber;
    private String name;
    private String version;
    private String environment;
    private String type;
    private Instant creationInstant;
    private Instant lastUpdateInstant;

    public TestCycleResponse(String projectKey, Long testCycleNumber, String name, String version,
                             String environment, String type, Instant creationInstant, Instant lastUpdateInstant) {
        this.projectKey = projectKey;
        this.testCycleNumber = testCycleNumber;
        this.name = name;
        this.version = version;
        this.environment = environment;
        this.type = type;
        this.creationInstant = creationInstant;
        this.lastUpdateInstant = lastUpdateInstant;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public Long getTestCycleNumber() {
        return testCycleNumber;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getEnvironment() {
        return environment;
    }

    public String getType() {
        return type;
    }

    public Instant getCreationInstant() {
        return creationInstant;
    }

    public Instant getLastUpdateInstant() {
        return lastUpdateInstant;
    }
}