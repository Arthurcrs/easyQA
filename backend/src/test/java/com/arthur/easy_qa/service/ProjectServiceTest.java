package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.project.Project;
import com.arthur.easy_qa.dto.project.CreateProjectRequest;
import com.arthur.easy_qa.dto.project.ProjectResponse;
import com.arthur.easy_qa.dto.project.UpdateProjectRequest;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProjectServiceTest {

    private ProjectRepository projectRepository;
    private ProjectService projectService;

    private final String PROJECT_KEY = "EASYQA";
    private Project activeProject;
    private Project archivedProject;

    @BeforeEach
    void setUp() {
        projectRepository = mock(ProjectRepository.class);
        projectService = new ProjectService(projectRepository);

        activeProject = new Project("EasyQA", PROJECT_KEY, Instant.now(), false);
        setProjectId(activeProject, UUID.randomUUID());

        archivedProject = new Project("Archived App", "ARCH", Instant.now(), true);
        setProjectId(archivedProject, UUID.randomUUID());
    }

    @Test
    void create_validRequest_shouldSaveAndReturnResponse() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("New Project");

        when(projectRepository.existsByNameIgnoreCase("New Project")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> {
            Project p = invocation.getArgument(0);
            setProjectId(p, UUID.randomUUID());
            return p;
        });

        ProjectResponse response = projectService.create(request);

        assertNotNull(response);
        assertEquals("NEW-PROJECT", response.getKey());
        assertFalse(response.isArchived());

        verify(projectRepository, times(1)).existsByNameIgnoreCase("New Project");
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void create_duplicateName_shouldThrowException() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("EasyQA");

        when(projectRepository.existsByNameIgnoreCase("EasyQA")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.create(request));

        assertEquals("Project name already exists", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void getAll_excludeArchived_shouldReturnActiveOnly() {
        when(projectRepository.findAll(false)).thenReturn(List.of(activeProject));

        List<ProjectResponse> responses = projectService.getAll(false);

        assertEquals(1, responses.size());
        assertEquals("EasyQA", responses.get(0).getName());
        verify(projectRepository, times(1)).findAll(false);
    }

    @Test
    void getAll_includeArchived_shouldReturnAll() {
        when(projectRepository.findAll(true)).thenReturn(List.of(activeProject, archivedProject));

        List<ProjectResponse> responses = projectService.getAll(true);

        assertEquals(2, responses.size());
        verify(projectRepository, times(1)).findAll(true);
    }

    @Test
    void getByKey_existingProject_shouldReturnResponse() {
        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(activeProject));

        Optional<ProjectResponse> response = projectService.getByKey(PROJECT_KEY);

        assertTrue(response.isPresent());
        assertEquals("EasyQA", response.get().getName());
        verify(projectRepository, times(1)).findByKey(PROJECT_KEY);
    }

    @Test
    void getByKey_nonExistingProject_shouldReturnEmpty() {
        when(projectRepository.findByKey("UNKNOWN")).thenReturn(Optional.empty());

        Optional<ProjectResponse> response = projectService.getByKey("UNKNOWN");

        assertTrue(response.isEmpty());
        verify(projectRepository, times(1)).findByKey("UNKNOWN");
    }

    @Test
    void updateName_validRequest_shouldUpdateAndReturnResponse() {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Updated EasyQA");

        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(activeProject));
        when(projectRepository.existsByNameIgnoreCase("Updated EasyQA")).thenReturn(false);
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<ProjectResponse> response = projectService.updateName(PROJECT_KEY, request);

        assertTrue(response.isPresent());
        assertEquals("Updated EasyQA", response.get().getName());

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        assertEquals("Updated EasyQA", captor.getValue().getName());
    }

    @Test
    void updateName_duplicateName_shouldThrowException() {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Duplicate Name");

        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(activeProject));
        when(projectRepository.existsByNameIgnoreCase("Duplicate Name")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> projectService.updateName(PROJECT_KEY, request));

        assertEquals("Project name already exists", exception.getMessage());
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    void archive_existingProject_shouldArchiveAndReturnResponse() {
        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(activeProject));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<ProjectResponse> response = projectService.archive(PROJECT_KEY);

        assertTrue(response.isPresent());
        assertTrue(response.get().isArchived());

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        assertTrue(captor.getValue().isArchived());
    }

    @Test
    void restore_archivedProject_shouldRestoreAndReturnResponse() {
        when(projectRepository.findByKey("ARCH")).thenReturn(Optional.of(archivedProject));
        when(projectRepository.save(any(Project.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<ProjectResponse> response = projectService.restore("ARCH");

        assertTrue(response.isPresent());
        assertFalse(response.get().isArchived());

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectRepository).save(captor.capture());
        assertFalse(captor.getValue().isArchived());
    }

    @Test
    void delete_existingProject_shouldReturnTrue() {
        when(projectRepository.deleteByKey(PROJECT_KEY)).thenReturn(true);

        boolean result = projectService.delete(PROJECT_KEY);

        assertTrue(result);
        verify(projectRepository, times(1)).deleteByKey(PROJECT_KEY);
    }

    @Test
    void delete_nonExistingProject_shouldReturnFalse() {
        when(projectRepository.deleteByKey("UNKNOWN")).thenReturn(false);

        boolean result = projectService.delete("UNKNOWN");

        assertFalse(result);
        verify(projectRepository, times(1)).deleteByKey("UNKNOWN");
    }

    private void setProjectId(Project project, UUID id) {
        try {
            Field idField = Project.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(project, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Could not set project ID", e);
        }
    }
}