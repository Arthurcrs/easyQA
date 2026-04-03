package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.ExecutionStatus;
import com.arthur.easy_qa.dto.execution.ExecutionResponse;
import com.arthur.easy_qa.dto.execution.UpdateExecutionRequest;
import com.arthur.easy_qa.service.ExecutionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectKey}")
public class ExecutionController {

    private final ExecutionService service;

    public ExecutionController(ExecutionService service) {
        this.service = service;
    }

    @GetMapping("/test-cycles/{testCycleNumber}/executions")
    public ResponseEntity<List<ExecutionResponse>> getByCycle(
            @PathVariable("projectKey") String projectKey,
            @PathVariable("testCycleNumber") Long testCycleNumber,
            @RequestParam(value = "status", required = false) ExecutionStatus status,
            @RequestParam(value = "sort", required = false) String sort) {
        return ResponseEntity.ok(service.getExecutionsByCycle(projectKey, testCycleNumber, status, sort));
    }

    @GetMapping("/executions/{executionNumber}")
    public ResponseEntity<ExecutionResponse> getByNumber(@PathVariable("projectKey") String projectKey,
                                                         @PathVariable("executionNumber") Long executionNumber) {
        return service.getByProjectAndNumber(projectKey, executionNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/executions/{executionNumber}")
    public ResponseEntity<ExecutionResponse> updateStatus(@PathVariable("projectKey") String projectKey,
                                                          @PathVariable("executionNumber") Long executionNumber,
                                                          @Valid @RequestBody UpdateExecutionRequest request) {
        return service.updateStatus(projectKey, executionNumber, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}