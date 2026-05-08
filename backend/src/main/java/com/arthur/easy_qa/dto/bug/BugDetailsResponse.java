package com.arthur.easy_qa.dto.bug;

import com.arthur.easy_qa.domain.bug.BugSeverity;
import com.arthur.easy_qa.domain.bug.BugStatus;
import com.arthur.easy_qa.dto.execution.ExecutionResponse;

import java.time.Instant;
import java.util.List;

public class BugDetailsResponse extends BugResponse {

    private List<ExecutionResponse> linkedExecutions;

    public BugDetailsResponse(String projectKey, Long bugNumber, String title, String description,
                              BugStatus status, BugSeverity severity, Instant openDate, Instant closeDate,
                              List<ExecutionResponse> linkedExecutions) {
        super(projectKey, bugNumber, title, description, status, severity, openDate, closeDate);
        this.linkedExecutions = linkedExecutions;
    }

    public List<ExecutionResponse> getLinkedExecutions() {
        return linkedExecutions;
    }

    public void setLinkedExecutions(List<ExecutionResponse> linkedExecutions) {
        this.linkedExecutions = linkedExecutions;
    }
}