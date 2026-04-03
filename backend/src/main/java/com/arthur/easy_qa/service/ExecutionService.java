package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.Execution;
import com.arthur.easy_qa.domain.TestCycle;
import com.arthur.easy_qa.dto.execution.ExecutionResponse;
import com.arthur.easy_qa.dto.execution.UpdateExecutionRequest;
import com.arthur.easy_qa.repository.execution.ExecutionRepository;
import com.arthur.easy_qa.repository.testcycle.TestCycleRepository;
import org.springframework.stereotype.Service;
import com.arthur.easy_qa.domain.ExecutionStatus;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

@Service
public class ExecutionService {

    private final ExecutionRepository executionRepository;
    private final TestCycleRepository testCycleRepository;

    public ExecutionService(ExecutionRepository executionRepository, TestCycleRepository testCycleRepository) {
        this.executionRepository = executionRepository;
        this.testCycleRepository = testCycleRepository;
    }

    public List<ExecutionResponse> getExecutionsByCycle(String projectKey, Long testCycleNumber, ExecutionStatus status, String sortParam) {
        TestCycle cycle = testCycleRepository.findByProjectKeyAndTestCycleNumber(projectKey, testCycleNumber)
                .orElseThrow(() -> new IllegalArgumentException("Test Cycle not found"));

        Sort sort = Sort.unsorted();
        if (sortParam != null && !sortParam.isBlank()) {
            String[] sortArgs = sortParam.split(",");
            String property = sortArgs[0];
            Sort.Direction direction = (sortArgs.length > 1 && sortArgs[1].equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(direction, property);
        }

        return executionRepository.findAllByTestCycleAndFilters(cycle, status, sort)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ExecutionResponse> getByProjectAndNumber(String projectKey, Long executionNumber) {
        return executionRepository.findByProjectKeyAndExecutionNumber(projectKey, executionNumber)
                .map(this::toResponse);
    }

    public Optional<ExecutionResponse> updateStatus(String projectKey, Long executionNumber, UpdateExecutionRequest request) {
        return executionRepository.findByProjectKeyAndExecutionNumber(projectKey, executionNumber)
                .map(existing -> {
                    existing.setStatus(request.getStatus());
                    executionRepository.save(existing);
                    return toResponse(existing);
                });
    }

    private ExecutionResponse toResponse(Execution execution) {
        return new ExecutionResponse(
                execution.getProject().getKey(),
                execution.getTestCycle().getTestCycleNumber(),
                execution.getTestCase().getTestCaseNumber(),
                execution.getExecutionNumber(),
                execution.getTestCase().getUs(),
                execution.getStatus()
        );
    }
}