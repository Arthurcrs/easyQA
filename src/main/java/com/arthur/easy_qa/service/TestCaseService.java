package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.dto.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.TestCaseResponse;
import com.arthur.easy_qa.repository.TestCaseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TestCaseService {

    private final TestCaseRepository repository;

    public TestCaseService(TestCaseRepository repository) {
        this.repository = repository;
    }

    public TestCaseResponse create(CreateTestCaseRequest request) {
        TestCase testCase = new TestCase(
                request.getUs(),
                request.getStatus(),
                request.getFeature(),
                request.getScenario(),
                request.getDescription(),
                request.getPriority(),
                request.getType()
        );

        repository.save(testCase);
        return toResponse(testCase);
    }

    public Optional<TestCaseResponse> getById(UUID uuid) {
        return repository.findById(uuid).map(this::toResponse);
    }

    public List<TestCaseResponse> getAll() {
        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<TestCaseResponse> update(UUID uuid, CreateTestCaseRequest request) {
        return repository.findById(uuid)
                .map(existing -> {
                    existing.setUs(request.getUs());
                    existing.setStatus(request.getStatus());
                    existing.setFeature(request.getFeature());
                    existing.setScenario(request.getScenario());
                    existing.setDescription(request.getDescription());
                    existing.setPriority(request.getPriority());
                    existing.setType(request.getType());

                    repository.save(existing);
                    return toResponse(existing);
                });
    }

    public boolean delete(UUID uuid) {
        return repository.deleteById(uuid);
    }

    private TestCaseResponse toResponse(TestCase testCase) {
        return new TestCaseResponse(
                testCase.getId(),
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
