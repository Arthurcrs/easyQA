package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.customfield.CustomField;
import com.arthur.easy_qa.domain.customfield.CustomFieldOption;
import com.arthur.easy_qa.domain.customfield.CustomFieldType;
import com.arthur.easy_qa.domain.project.Project;
import com.arthur.easy_qa.dto.customfield.*;
import com.arthur.easy_qa.repository.customfield.CustomFieldRepository;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomFieldServiceTest {

    private CustomFieldRepository customFieldRepository;
    private ProjectRepository projectRepository;
    private CustomFieldService service;

    private final String PROJECT_KEY = "EASYQA";
    private Project project;
    private CustomField defaultField;

    @BeforeEach
    void setup() {
        customFieldRepository = mock(CustomFieldRepository.class);
        projectRepository = mock(ProjectRepository.class);
        service = new CustomFieldService(customFieldRepository, projectRepository);

        project = new Project("EasyQA", PROJECT_KEY, Instant.now(), false);
        defaultField = new CustomField(project, 1L, "Browser", CustomFieldType.DROPDOWN);

        CustomFieldOption option = new CustomFieldOption(defaultField, "Chrome", 0);
        setPrivateField(option, "id", UUID.randomUUID());
        defaultField.addOption(option);
    }

    @Test
    void create_shouldSaveAndReturnResponse() {
        CreateCustomFieldRequest request = new CreateCustomFieldRequest();
        request.setName("Browser");
        request.setType(CustomFieldType.DROPDOWN);
        request.setOptions(List.of("Chrome", "Firefox"));

        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(project));
        when(customFieldRepository.findMaxFieldNumberByProjectKey(PROJECT_KEY)).thenReturn(Optional.of(0L));
        when(customFieldRepository.save(any(CustomField.class))).thenAnswer(i -> i.getArgument(0));

        CustomFieldResponse response = service.create(PROJECT_KEY, request);

        assertEquals("Browser", response.getName());
        assertEquals(2, response.getOptions().size());
        assertEquals("Chrome", response.getOptions().get(0).getValue());
    }

    @Test
    void getByProjectAndNumber_shouldReturnResponse() {
        when(customFieldRepository.findByProjectKeyAndFieldNumber(PROJECT_KEY, 1L)).thenReturn(Optional.of(defaultField));

        Optional<CustomFieldResponse> response = service.getByProjectAndNumber(PROJECT_KEY, 1L);

        assertTrue(response.isPresent());
        assertEquals("Browser", response.get().getName());
        assertEquals(1, response.get().getOptions().size());
    }

    @Test
    void update_shouldUpdateNameOnly() {
        CreateCustomFieldRequest request = new CreateCustomFieldRequest();
        request.setName("Updated Name");

        when(customFieldRepository.findByProjectKeyAndFieldNumber(PROJECT_KEY, 1L)).thenReturn(Optional.of(defaultField));
        when(customFieldRepository.save(any(CustomField.class))).thenAnswer(i -> i.getArgument(0));

        Optional<CustomFieldResponse> response = service.update(PROJECT_KEY, 1L, request);

        assertTrue(response.isPresent());
        assertEquals("Updated Name", response.get().getName());
    }

    @Test
    void addOption_shouldSaveNewOption() {
        CreateFieldOptionRequest request = new CreateFieldOptionRequest();
        request.setValue("Safari");
        request.setSortOrder(2);

        when(customFieldRepository.findByProjectKeyAndFieldNumber(PROJECT_KEY, 1L)).thenReturn(Optional.of(defaultField));

        CustomFieldOptionResponse response = service.addOption(PROJECT_KEY, 1L, request);

        assertEquals("Safari", response.getValue());
        assertEquals(2, defaultField.getOptionList().size());
    }

    private void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}