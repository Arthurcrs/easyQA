package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.domain.TestCycle;
import com.arthur.easy_qa.dto.testcycle.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.testcycle.TestCycleResponse;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import com.arthur.easy_qa.repository.testcycle.TestCycleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestCycleService {

    private final TestCycleRepository repository;
    private final ProjectRepository projectRepository;

    public TestCycleService(TestCycleRepository repository, ProjectRepository projectRepository) {
        this.repository = repository;
        this.projectRepository = projectRepository;
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
}