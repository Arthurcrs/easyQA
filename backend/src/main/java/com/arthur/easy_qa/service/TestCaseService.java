package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.dto.testcase.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.testcase.TestCaseResponse;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import com.arthur.easy_qa.repository.testcase.TestCaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ProjectRepository projectRepository;

    public TestCaseService(TestCaseRepository testCaseRepository, ProjectRepository projectRepository) {
        this.testCaseRepository = testCaseRepository;
        this.projectRepository = projectRepository;
    }

    public TestCaseResponse create(String projectKey, CreateTestCaseRequest request) {
        Project project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectKey));

        Long nextNumber = testCaseRepository.findMaxTestCaseNumberByProjectKey(projectKey).orElse(0L) + 1L;

        TestCase testCase = new TestCase(
                project,
                nextNumber,
                request.getUs(),
                request.getStatus(),
                request.getFeature(),
                request.getScenario(),
                request.getDescription(),
                request.getPriority(),
                request.getType()
        );

        testCaseRepository.save(testCase);
        return toResponse(testCase);
    }

    public Optional<TestCaseResponse> getByProjectAndNumber(String projectKey, Long testCaseNumber) {
        return testCaseRepository.findByProjectKeyAndTestCaseNumber(projectKey, testCaseNumber)
                .map(this::toResponse);
    }

    public List<TestCaseResponse> getAllByProject(String projectKey) {
        return testCaseRepository.findAllByProjectKey(projectKey)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<TestCaseResponse> update(String projectKey, Long testCaseNumber, CreateTestCaseRequest request) {
        return testCaseRepository.findByProjectKeyAndTestCaseNumber(projectKey, testCaseNumber)
                .map(existing -> {
                    existing.setUs(request.getUs());
                    existing.setStatus(request.getStatus());
                    existing.setFeature(request.getFeature());
                    existing.setScenario(request.getScenario());
                    existing.setDescription(request.getDescription());
                    existing.setPriority(request.getPriority());
                    existing.setType(request.getType());

                    testCaseRepository.save(existing);
                    return toResponse(existing);
                });
    }

    public boolean delete(String projectKey, Long testCaseNumber) {
        return testCaseRepository.deleteByProjectKeyAndTestCaseNumber(projectKey, testCaseNumber);
    }

    private TestCaseResponse toResponse(TestCase testCase) {
        return new TestCaseResponse(
                testCase.getProject().getKey(),
                testCase.getTestCaseNumber(),
                testCase.getUs(),
                testCase.getStatus(),
                testCase.getFeature(),
                testCase.getScenario(),
                testCase.getDescription(),
                testCase.getPriority(),
                testCase.getType(),
                testCase.getCreationInstant(),
                testCase.getLastUpdateInstant()
        );
    }
}
