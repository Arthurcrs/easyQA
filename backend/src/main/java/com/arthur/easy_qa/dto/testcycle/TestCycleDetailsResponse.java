package com.arthur.easy_qa.dto.testcycle;

import com.arthur.easy_qa.domain.ExecutionStatus;
import com.arthur.easy_qa.dto.execution.ExecutionResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public class TestCycleDetailsResponse {

    private String projectKey;
    private Long testCycleNumber;
    private String name;
    private String version;
    private String environment;
    private String type;
    private Instant creationInstant;
    private Instant lastUpdateInstant;
    private Map<ExecutionStatus, Long> progressSummary;
    private List<ExecutionResponse> executions;

    public TestCycleDetailsResponse(String projectKey,
                                    Long testCycleNumber,
                                    String name,
                                    String version,
                                    String environment,
                                    String type,
                                    Instant creationInstant,
                                    Instant lastUpdateInstant,
                                    Map<ExecutionStatus, Long> progressSummary,
                                    List<ExecutionResponse> executions) {
        this.projectKey = projectKey;
        this.testCycleNumber = testCycleNumber;
        this.name = name;
        this.version = version;
        this.environment = environment;
        this.type = type;
        this.creationInstant = creationInstant;
        this.lastUpdateInstant = lastUpdateInstant;
        this.progressSummary = progressSummary;
        this.executions = executions;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public Long getTestCycleNumber() {
        return testCycleNumber;
    }

    public void setTestCycleNumber(Long testCycleNumber) {
        this.testCycleNumber = testCycleNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getCreationInstant() {
        return creationInstant;
    }

    public void setCreationInstant(Instant creationInstant) {
        this.creationInstant = creationInstant;
    }

    public Instant getLastUpdateInstant() {
        return lastUpdateInstant;
    }

    public void setLastUpdateInstant(Instant lastUpdateInstant) {
        this.lastUpdateInstant = lastUpdateInstant;
    }

    public Map<ExecutionStatus, Long> getProgressSummary() {
        return progressSummary;
    }

    public void setProgressSummary(Map<ExecutionStatus, Long> progressSummary) {
        this.progressSummary = progressSummary;
    }

    public List<ExecutionResponse> getExecutions() {
        return executions;
    }

    public void setExecutions(List<ExecutionResponse> executions) {
        this.executions = executions;
    }
}