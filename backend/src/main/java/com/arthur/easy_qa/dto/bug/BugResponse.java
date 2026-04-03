package com.arthur.easy_qa.dto.bug;

import com.arthur.easy_qa.domain.BugSeverity;
import com.arthur.easy_qa.domain.BugStatus;

import java.time.Instant;

public class BugResponse {

    private String projectKey;
    private Long bugNumber;
    private String title;
    private String description;
    private BugStatus status;
    private BugSeverity severity;
    private Instant openDate;
    private Instant closeDate;

    public BugResponse(String projectKey, Long bugNumber, String title, String description,
                       BugStatus status, BugSeverity severity, Instant openDate, Instant closeDate) {
        this.projectKey = projectKey;
        this.bugNumber = bugNumber;
        this.title = title;
        this.description = description;
        this.status = status;
        this.severity = severity;
        this.openDate = openDate;
        this.closeDate = closeDate;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public Long getBugNumber() {
        return bugNumber;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public BugStatus getStatus() {
        return status;
    }

    public BugSeverity getSeverity() {
        return severity;
    }

    public Instant getOpenDate() {
        return openDate;
    }

    public Instant getCloseDate() {
        return closeDate;
    }
}