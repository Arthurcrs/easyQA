package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.customfield.CustomField;
import com.arthur.easy_qa.domain.customfield.CustomFieldOption;
import com.arthur.easy_qa.domain.project.Project;
import com.arthur.easy_qa.dto.customfield.*;
import com.arthur.easy_qa.repository.customfield.CustomFieldRepository;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomFieldService {

    private final CustomFieldRepository customFieldRepository;
    private final ProjectRepository projectRepository;

    public CustomFieldService(CustomFieldRepository customFieldRepository, ProjectRepository projectRepository) {
        this.customFieldRepository = customFieldRepository;
        this.projectRepository = projectRepository;
    }

    public CustomFieldResponse create(String projectKey, CreateCustomFieldRequest request) {
        Project project = projectRepository.findByKey(projectKey).orElseThrow();
        Long nextNumber = customFieldRepository.findMaxFieldNumberByProjectKey(projectKey).orElse(0L) + 1L;

        CustomField customField = new CustomField(project, nextNumber, request.getName(), request.getType());

        if (request.getOptions() != null) {
            int order = 0;
            for (String optValue : request.getOptions()) {
                customField.addOption(new CustomFieldOption(customField, optValue, order++));
            }
        }
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
        List<CustomFieldOptionResponse> optionResponses = customField.getOptionList().stream()
                .sorted(Comparator.comparing(CustomFieldOption::getSortOrder))
                .map(opt -> new CustomFieldOptionResponse(opt.getId(), opt.getValue(), opt.isActive(), opt.getSortOrder()))
                .toList();

        return new CustomFieldResponse(customField.getProject().getKey(), customField.getFieldNumber(),
                customField.getName(), customField.getType(), optionResponses);
    }

    public Optional<CustomFieldResponse> update(String projectKey, Long fieldNumber, CreateCustomFieldRequest request) {
        return customFieldRepository.findByProjectKeyAndFieldNumber(projectKey, fieldNumber)
                .map(existing -> {
                    if (request.getName() != null && !request.getName().isBlank()) {
                        existing.setName(request.getName());
                    }

                    customFieldRepository.save(existing);
                    return toResponse(existing);
                });
    }

    public CustomFieldOptionResponse addOption(String projectKey, Long fieldNumber, CreateFieldOptionRequest request) {
        CustomField customField = customFieldRepository.findByProjectKeyAndFieldNumber(projectKey, fieldNumber).orElseThrow();
        CustomFieldOption option = new CustomFieldOption(customField, request.getValue(), request.getSortOrder());
        customField.addOption(option);
        customFieldRepository.save(customField);
        return new CustomFieldOptionResponse(option.getId(), option.getValue(), option.isActive(), option.getSortOrder());
    }

    public CustomFieldOptionResponse updateOption(String projectKey, Long fieldNumber, UUID optionId, UpdateFieldOptionRequest request) {
        CustomField customField = customFieldRepository.findByProjectKeyAndFieldNumber(projectKey, fieldNumber).orElseThrow();

        CustomFieldOption option = customField.getOptionList().stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Option not found"));

        if (request.getValue() != null) option.setValue(request.getValue());
        if (request.getActive() != null) option.setActive(request.getActive());
        if (request.getSortOrder() != null) option.setSortOrder(request.getSortOrder());

        customFieldRepository.save(customField);
        return new CustomFieldOptionResponse(option.getId(), option.getValue(), option.isActive(), option.getSortOrder());
    }

    public void deleteOption(String projectKey, Long fieldNumber, UUID optionId) {
        CustomField customField = customFieldRepository.findByProjectKeyAndFieldNumber(projectKey, fieldNumber).orElseThrow();

        CustomFieldOption option = customField.getOptionList().stream()
                .filter(o -> o.getId().equals(optionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Option not found"));

        customField.removeOption(option);
        customFieldRepository.save(customField);
    }
}