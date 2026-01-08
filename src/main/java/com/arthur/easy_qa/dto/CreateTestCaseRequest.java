package com.arthur.easy_qa.dto;

import com.arthur.easy_qa.domain.TestCasePriority;
import com.arthur.easy_qa.domain.TestCaseStatus;
import com.arthur.easy_qa.domain.TestCaseType;

public class CreateTestCaseRequest {

    private TestCaseStatus status;
    private String us;
    private String feature;
    private String scenario;
    private String description;
    private TestCasePriority priority;
    private TestCaseType type;

    public TestCaseStatus getStatus() {
        return status;
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

    public TestCasePriority getPriority() {
        return priority;
    }

    public TestCaseType getType() {
        return type;
    }
}
