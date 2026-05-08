package com.arthur.easy_qa.dto.execution;

import com.arthur.easy_qa.domain.execution.ExecutionStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateExecutionRequest {

    @NotNull(message = "Status cannot be null")
    private ExecutionStatus status;

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }
}