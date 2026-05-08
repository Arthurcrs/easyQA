package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.execution.Execution;
import com.arthur.easy_qa.domain.project.Project;
import com.arthur.easy_qa.domain.testcase.TestCase;
import com.arthur.easy_qa.domain.testcycle.TestCycle;
import com.arthur.easy_qa.dto.testcycle.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.testcycle.TestCycleResponse;
import com.arthur.easy_qa.repository.execution.ExecutionRepository;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import com.arthur.easy_qa.repository.testcase.TestCaseRepository;
import com.arthur.easy_qa.repository.testcycle.TestCycleRepository;
import org.springframework.stereotype.Service;
import com.arthur.easy_qa.domain.execution.ExecutionStatus;
import com.arthur.easy_qa.dto.execution.ExecutionResponse;
import com.arthur.easy_qa.dto.testcycle.TestCycleDetailsResponse;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
public class TestCycleService {

    private final TestCycleRepository repository;
    private final ProjectRepository projectRepository;
    private final TestCaseRepository testCaseRepository;
    private final ExecutionRepository executionRepository;

    public TestCycleService(TestCycleRepository repository,
                            ProjectRepository projectRepository,
                            TestCaseRepository testCaseRepository,
                            ExecutionRepository executionRepository) {
        this.repository = repository;
        this.projectRepository = projectRepository;
        this.testCaseRepository = testCaseRepository;
        this.executionRepository = executionRepository;
    }

    public TestCycleResponse create(String projectKey, CreateTestCycleRequest request) {
        Project project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectKey));

        Long nextNumber = repository.findMaxTestCycleNumberByProjectKey(projectKey).orElse(0L) + 1L;

        TestCycle testCycle = new TestCycle(
                project,
                nextNumber,
                request.getName(),
                request.getVersion(),
                request.getEnvironment(),
                request.getType()
        );

        repository.save(testCycle);
        return toResponse(testCycle);
    }

    public Optional<TestCycleResponse> getByProjectAndNumber(String projectKey, Long testCycleNumber) {
        return repository.findByProjectKeyAndTestCycleNumber(projectKey, testCycleNumber)
                .map(this::toResponse);
    }

    public List<TestCycleResponse> getAllByProject(String projectKey) {
        return repository.findAllByProjectKey(projectKey)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<TestCycleResponse> update(String projectKey, Long testCycleNumber, CreateTestCycleRequest request) {
        return repository.findByProjectKeyAndTestCycleNumber(projectKey, testCycleNumber)
                .map(existing -> {
                    existing.setName(request.getName());
                    existing.setVersion(request.getVersion());
                    existing.setEnvironment(request.getEnvironment());
                    existing.setType(request.getType());

                    repository.save(existing);
                    return toResponse(existing);
                });
    }

    public boolean delete(String projectKey, Long testCycleNumber) {
        return repository.deleteByProjectKeyAndTestCycleNumber(projectKey, testCycleNumber);
    }

    public void addTestCasesToCycle(String projectKey, Long testCycleNumber, List<Long> testCaseNumbers) {
        TestCycle cycle = repository.findByProjectKeyAndTestCycleNumber(projectKey, testCycleNumber)
                .orElseThrow(() -> new IllegalArgumentException("Test Cycle not found"));

        Long currentExecNumber = executionRepository.findMaxExecutionNumberByProjectKey(projectKey).orElse(0L);

        for (Long tcNumber : testCaseNumbers) {
            TestCase testCase = testCaseRepository.findByProjectKeyAndTestCaseNumber(projectKey, tcNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Test Case not found: " + tcNumber));

            boolean exists = executionRepository.existsByTestCycleAndTestCase(cycle, testCase);
            if (!exists) {
                currentExecNumber++;
                Execution execution = new Execution(cycle.getProject(), currentExecNumber, cycle, testCase);
                executionRepository.save(execution);
            }
        }
    }

    public void removeTestCaseFromCycle(String projectKey, Long testCycleNumber, Long testCaseNumber) {
        executionRepository.deleteByProjectKeyAndCycleNumberAndCaseNumber(projectKey, testCycleNumber, testCaseNumber);
    }

    public TestCycleResponse duplicate(String projectKey, Long testCycleNumber) {
        TestCycle originalCycle = repository.findByProjectKeyAndTestCycleNumber(projectKey, testCycleNumber)
                .orElseThrow(() -> new IllegalArgumentException("Test Cycle not found"));

        Long nextCycleNumber = repository.findMaxTestCycleNumberByProjectKey(projectKey).orElse(0L) + 1L;
        TestCycle newCycle = new TestCycle(
                originalCycle.getProject(),
                nextCycleNumber,
                originalCycle.getName() + " (Copy)",
                originalCycle.getVersion(),
                originalCycle.getEnvironment(),
                originalCycle.getType()
        );
        repository.save(newCycle);

        List<Execution> originalExecutions = executionRepository.findAllByTestCycle(originalCycle);
        Long currentExecNumber = executionRepository.findMaxExecutionNumberByProjectKey(projectKey).orElse(0L);

        for (Execution origExec : originalExecutions) {
            currentExecNumber++;
            Execution newExecution = new Execution(newCycle.getProject(), currentExecNumber, newCycle, origExec.getTestCase());
            executionRepository.save(newExecution);
        }

        return toResponse(newCycle);
    }

    private TestCycleResponse toResponse(TestCycle testCycle) {
        return new TestCycleResponse(
                testCycle.getProject().getKey(),
                testCycle.getTestCycleNumber(),
                testCycle.getName(),
                testCycle.getVersion(),
                testCycle.getEnvironment(),
                testCycle.getType(),
                testCycle.getCreationInstant(),
                testCycle.getLastUpdateInstant()
        );
    }

    public Optional<TestCycleDetailsResponse> getDetailsByProjectAndNumber(String projectKey, Long testCycleNumber) {
        return repository.findByProjectKeyAndTestCycleNumber(projectKey, testCycleNumber)
                .map(cycle -> {
                    List<Execution> executions = executionRepository.findAllByTestCycle(cycle);

                    List<ExecutionResponse> executionResponses = executions.stream()
                            .map(e -> new ExecutionResponse(
                                    e.getProject().getKey(),
                                    e.getTestCycle().getTestCycleNumber(),
                                    e.getTestCase().getTestCaseNumber(),
                                    e.getExecutionNumber(),
                                    e.getTestCase().getUs(),
                                    e.getStatus()
                            )).toList();

                    Map<ExecutionStatus, Long> counts = executions.stream()
                            .collect(Collectors.groupingBy(Execution::getStatus, Collectors.counting()));

                    Map<ExecutionStatus, Long> progressSummary = new EnumMap<>(ExecutionStatus.class);
                    for (ExecutionStatus status : ExecutionStatus.values()) {
                        progressSummary.put(status, counts.getOrDefault(status, 0L));
                    }

                    return new TestCycleDetailsResponse(
                            cycle.getProject().getKey(),
                            cycle.getTestCycleNumber(),
                            cycle.getName(),
                            cycle.getVersion(),
                            cycle.getEnvironment(),
                            cycle.getType(),
                            cycle.getCreationInstant(),
                            cycle.getLastUpdateInstant(),
                            progressSummary,
                            executionResponses
                    );
                });
    }
}