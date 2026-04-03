package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.CustomField;
import com.arthur.easy_qa.domain.CustomFieldType;
import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.dto.customfield.CreateCustomFieldRequest;
import com.arthur.easy_qa.dto.customfield.CustomFieldResponse;
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
        defaultField = new CustomField(project, 1L, "Browser", CustomFieldType.DROPDOWN, "Chrome,Firefox,Edge");
    }

    @Test
    void create_validRequest_shouldSaveAndReturnResponse() {
        CreateCustomFieldRequest request = new CreateCustomFieldRequest();
        request.setName("Browser");
        request.setType(CustomFieldType.DROPDOWN);
        request.setOptions("Chrome,Firefox,Edge");

        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(project));
        when(customFieldRepository.findMaxFieldNumberByProjectKey(PROJECT_KEY)).thenReturn(Optional.of(0L));
        when(customFieldRepository.save(any(CustomField.class))).thenAnswer(invocation -> {
            CustomField cf = invocation.getArgument(0);
            simulateIdGeneration(cf);
            return cf;
        });

        CustomFieldResponse response = service.create(PROJECT_KEY, request);

        assertNotNull(response);
        assertEquals(1L, response.getFieldNumber());
        assertEquals("Browser", response.getName());
        assertEquals(CustomFieldType.DROPDOWN, response.getType());
        assertEquals("Chrome,Firefox,Edge", response.getOptions());
        verify(customFieldRepository, times(1)).save(any(CustomField.class));
    }

    @Test
    void create_projectNotFound_shouldThrowException() {
        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.empty());

        CreateCustomFieldRequest request = new CreateCustomFieldRequest();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.create(PROJECT_KEY, request));

        assertEquals("Project not found: " + PROJECT_KEY, exception.getMessage());
        verify(customFieldRepository, never()).save(any());
    }

    @Test
    void getByProjectAndNumber_found_shouldReturnResponse() {
        when(customFieldRepository.findByProjectKeyAndFieldNumber(PROJECT_KEY, 1L))
                .thenReturn(Optional.of(defaultField));

        Optional<CustomFieldResponse> response = service.getByProjectAndNumber(PROJECT_KEY, 1L);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get().getFieldNumber());
        assertEquals("Browser", response.get().getName());
    }

    @Test
    void getAllByProject_shouldReturnMappedList() {
        when(customFieldRepository.findAllByProjectKey(PROJECT_KEY)).thenReturn(List.of(defaultField));

        List<CustomFieldResponse> result = service.getAllByProject(PROJECT_KEY);

        assertEquals(1, result.size());
        assertEquals("Browser", result.get(0).getName());
    }

    @Test
    void delete_shouldReturnRepositoryResult() {
        when(customFieldRepository.deleteByProjectKeyAndFieldNumber(PROJECT_KEY, 1L)).thenReturn(true);

        assertTrue(service.delete(PROJECT_KEY, 1L));
        verify(customFieldRepository).deleteByProjectKeyAndFieldNumber(PROJECT_KEY, 1L);
    }

    private static void simulateIdGeneration(CustomField customField) {
        try {
            Field field = customField.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(customField, UUID.randomUUID());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}