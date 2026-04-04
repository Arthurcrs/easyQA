package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.customfield.CustomField;
import com.arthur.easy_qa.domain.project.Project;
import com.arthur.easy_qa.domain.testcase.*;
import com.arthur.easy_qa.dto.testcase.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.testcase.TestCaseResponse;
import com.arthur.easy_qa.repository.customfield.CustomFieldRepository;
import com.arthur.easy_qa.repository.customfield.TestCaseFieldValueRepository;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import com.arthur.easy_qa.repository.testcase.TestCaseRepository;
import org.springframework.stereotype.Service;
import com.arthur.easy_qa.domain.testcase.TestCaseFieldValue;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final ProjectRepository projectRepository;
    private final CustomFieldRepository customFieldRepository;
    private final TestCaseFieldValueRepository fieldValueRepository;

    public TestCaseService(TestCaseRepository testCaseRepository,
                           ProjectRepository projectRepository,
                           CustomFieldRepository customFieldRepository,
                           TestCaseFieldValueRepository fieldValueRepository) {
        this.testCaseRepository = testCaseRepository;
        this.projectRepository = projectRepository;
        this.customFieldRepository = customFieldRepository;
        this.fieldValueRepository = fieldValueRepository;
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

        if (request.getCustomFields() != null && !request.getCustomFields().isEmpty()) {
            saveCustomFields(projectKey, testCase, request.getCustomFields());
        }

        return toResponse(testCase);
    }

    public Optional<TestCaseResponse> getByProjectAndNumber(String projectKey, Long testCaseNumber) {
        return testCaseRepository.findByProjectKeyAndTestCaseNumber(projectKey, testCaseNumber)
                .map(this::toResponse);
    }

    public List<TestCaseResponse> getAllByProject(String projectKey,
                                                  TestCaseStatus status,
                                                  TestCaseType type,
                                                  TestCasePriority priority,
                                                  String q) {
        return testCaseRepository.findAllByProjectKeyAndFilters(projectKey, status, type, priority, q)
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

                    if (request.getCustomFields() != null) {
                        saveCustomFields(projectKey, existing, request.getCustomFields());
                    }

                    return toResponse(existing);
                });
    }

    @Transactional
    public boolean delete(String projectKey, Long testCaseNumber) {
        return testCaseRepository.findByProjectKeyAndTestCaseNumber(projectKey, testCaseNumber)
                .map(testCase -> {
                    if (!testCase.getExecutions().isEmpty()) {
                        throw new IllegalStateException("Cannot delete test case: it has execution history in a test cycle.");
                    }
                    return testCaseRepository.deleteByProjectKeyAndTestCaseNumber(projectKey, testCaseNumber);
                }).orElse(false);
    }

    private void saveCustomFields(String projectKey, TestCase testCase, Map<Long, String> customFields) {
        for (Map.Entry<Long, String> entry : customFields.entrySet()) {
            Long fieldNumber = entry.getKey();
            String value = entry.getValue();

            CustomField customField = customFieldRepository.findByProjectKeyAndFieldNumber(projectKey, fieldNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Custom field not found: " + fieldNumber));

            TestCaseFieldValue fieldValue = fieldValueRepository.findByTestCaseAndCustomField(testCase, customField)
                    .orElse(new TestCaseFieldValue(testCase, customField, value));

            fieldValue.setValue(value);
            fieldValueRepository.save(fieldValue);
        }
    }

    private TestCaseResponse toResponse(TestCase testCase) {
        Map<String, String> customFieldValues = fieldValueRepository.findAllByTestCase(testCase)
                .stream()
                .collect(Collectors.toMap(
                        fv -> fv.getCustomField().getName(),
                        TestCaseFieldValue::getValue
                ));

        return new TestCaseResponse(
                testCase.getProject().getKey(),
                testCase.getTestCaseNumber(),
                testCase.getUs(),
                testCase.getFeature(),
                testCase.getScenario(),
                testCase.getDescription(),
                testCase.getStatus(),
                testCase.getPriority(),
                testCase.getType(),
                testCase.getCreationInstant(),
                testCase.getLastUpdateInstant(),
                customFieldValues
        );
    }

    public Map<String, String> getCustomFields(String projectKey, Long testCaseNumber) {
        TestCase testCase = testCaseRepository.findByProjectKeyAndTestCaseNumber(projectKey, testCaseNumber)
                .orElseThrow(() -> new IllegalArgumentException("Test Case not found"));

        return fieldValueRepository.findAllByTestCase(testCase)
                .stream()
                .collect(Collectors.toMap(
                        fv -> fv.getCustomField().getName(),
                        TestCaseFieldValue::getValue
                ));
    }

    public void updateCustomFields(String projectKey, Long testCaseNumber, Map<Long, String> customFields) {
        TestCase testCase = testCaseRepository.findByProjectKeyAndTestCaseNumber(projectKey, testCaseNumber)
                .orElseThrow(() -> new IllegalArgumentException("Test Case not found"));

        List<TestCaseFieldValue> existingValues = fieldValueRepository.findAllByTestCase(testCase);
        for (TestCaseFieldValue value : existingValues) {
            if (!customFields.containsKey(value.getCustomField().getFieldNumber())) {
                fieldValueRepository.delete(value);
            }
        }

        saveCustomFields(projectKey, testCase, customFields);
    }
}