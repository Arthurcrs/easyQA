package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.CustomField;
import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.dto.customfield.CreateCustomFieldRequest;
import com.arthur.easy_qa.dto.customfield.CustomFieldResponse;
import com.arthur.easy_qa.repository.customfield.CustomFieldRepository;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomFieldService {

    private final CustomFieldRepository customFieldRepository;
    private final ProjectRepository projectRepository;

    public CustomFieldService(CustomFieldRepository customFieldRepository, ProjectRepository projectRepository) {
        this.customFieldRepository = customFieldRepository;
        this.projectRepository = projectRepository;
    }

    public CustomFieldResponse create(String projectKey, CreateCustomFieldRequest request) {
        Project project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectKey));

        Long nextNumber = customFieldRepository.findMaxFieldNumberByProjectKey(projectKey).orElse(0L) + 1L;

        CustomField customField = new CustomField(
                project,
                nextNumber,
                request.getName(),
                request.getType(),
                request.getOptions()
        );

        customFieldRepository.save(customField);
        return toResponse(customField);
    }

    public Optional<CustomFieldResponse> getByProjectAndNumber(String projectKey, Long fieldNumber) {
        return customFieldRepository.findByProjectKeyAndFieldNumber(projectKey, fieldNumber)
                .map(this::toResponse);
    }

    public List<CustomFieldResponse> getAllByProject(String projectKey) {
        return customFieldRepository.findAllByProjectKey(projectKey)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public boolean delete(String projectKey, Long fieldNumber) {
        return customFieldRepository.deleteByProjectKeyAndFieldNumber(projectKey, fieldNumber);
    }

    private CustomFieldResponse toResponse(CustomField customField) {
        return new CustomFieldResponse(
                customField.getProject().getKey(),
                customField.getFieldNumber(),
                customField.getName(),
                customField.getType(),
                customField.getOptions()
        );
    }
}