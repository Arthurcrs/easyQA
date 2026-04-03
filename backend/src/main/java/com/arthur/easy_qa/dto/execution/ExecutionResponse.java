package com.arthur.easy_qa.dto.execution;

import com.arthur.easy_qa.domain.execution.ExecutionStatus;

public class ExecutionResponse {

    private String projectKey;
    private Long testCycleNumber;
    private Long testCaseNumber;
    private Long executionNumber;
    private String testCaseUs;
    private ExecutionStatus status;

    public ExecutionResponse(String projectKey, Long testCycleNumber, Long testCaseNumber,
                             Long executionNumber, String testCaseUs, ExecutionStatus status) {
        this.projectKey = projectKey;
        this.testCycleNumber = testCycleNumber;
        this.testCaseNumber = testCaseNumber;
        this.executionNumber = executionNumber;
        this.testCaseUs = testCaseUs;
        this.status = status;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public Long getTestCycleNumber() {
        return testCycleNumber;
    }

    public Long getTestCaseNumber() {
        return testCaseNumber;
    }

    public Long getExecutionNumber() {
        return executionNumber;
    }

    public String getTestCaseUs() {
        return testCaseUs;
    }

    public ExecutionStatus getStatus() {
        return status;
    }
}