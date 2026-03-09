package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.dto.project.CreateProjectRequest;
import com.arthur.easy_qa.dto.project.ProjectResponse;
import com.arthur.easy_qa.dto.project.UpdateProjectRequest;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

class ProjectServiceTest {

    private ProjectRepository repository;
    private ProjectService service;

    @BeforeEach
    void setup() {
        repository = mock(ProjectRepository.class);
        service = new ProjectService(repository);
    }

    @Test
    void create_validRequest_shouldSaveAndReturnResponse() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Easy QA");

        when(repository.existsByNameIgnoreCase("Easy QA")).thenReturn(false);
        when(repository.save(any(Project.class))).thenAnswer(invocation -> {
            Project project = invocation.getArgument(0);
            setPrivateField(project, "id", UUID.randomUUID());
            return project;
        });

        ProjectResponse response = service.create(request);

        verify(repository).existsByNameIgnoreCase("Easy QA");
        verify(repository).save(any(Project.class));

        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals("Easy QA", response.getName());
        assertEquals("EASY-QA", response.getKey());
        assertNotNull(response.getCreationDate());
        assertFalse(response.isArchived());
    }

    @Test
    void create_blankName_shouldThrowIllegalArgumentException() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(request)
        );

        assertEquals("Project name must not be blank", exception.getMessage());
        verify(repository, never()).existsByNameIgnoreCase(anyString());
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    void create_nullName_shouldThrowIllegalArgumentException() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(request)
        );

        assertEquals("Project name must not be blank", exception.getMessage());
        verify(repository, never()).existsByNameIgnoreCase(anyString());
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    void create_nullRequest_shouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(null)
        );

        assertEquals("Project name must not be blank", exception.getMessage());
        verify(repository, never()).existsByNameIgnoreCase(anyString());
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    void create_duplicateName_shouldThrowIllegalArgumentException() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Easy QA");

        when(repository.existsByNameIgnoreCase("Easy QA")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.create(request)
        );

        assertEquals("Project name already exists", exception.getMessage());
        verify(repository).existsByNameIgnoreCase("Easy QA");
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    void create_nameWithExtraSpaces_shouldTrimBeforeSave() {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("   Easy QA   ");

        when(repository.existsByNameIgnoreCase("Easy QA")).thenReturn(false);
        when(repository.save(any(Project.class))).thenAnswer(invocation -> {
            Project project = invocation.getArgument(0);
            setPrivateField(project, "id", UUID.randomUUID());
            return project;
        });

        ProjectResponse response = service.create(request);

        verify(repository).existsByNameIgnoreCase("Easy QA");
        verify(repository).save(any(Project.class));

        assertEquals("Easy QA", response.getName());
        assertEquals("EASY-QA", response.getKey());
    }

    @Test
    void getAll_includeArchivedFalse_shouldReturnOnlyActiveProjects() {
        Project project1 = buildProject("EASY-QA", "Easy QA", false);
        Project project2 = buildProject("BANK-QA", "Bank QA", false);

        when(repository.findAllByArchivedFalse()).thenReturn(List.of(project1, project2));

        List<ProjectResponse> result = service.getAll(false);

        verify(repository).findAllByArchivedFalse();
        verify(repository, never()).findAll();

        assertEquals(2, result.size());
        assertEquals("EASY-QA", result.get(0).getKey());
        assertEquals("Easy QA", result.get(0).getName());
        assertFalse(result.get(0).isArchived());

        assertEquals("BANK-QA", result.get(1).getKey());
        assertEquals("Bank QA", result.get(1).getName());
        assertFalse(result.get(1).isArchived());
    }

    @Test
    void getAll_includeArchivedTrue_shouldReturnAllProjects() {
        Project activeProject = buildProject("EASY-QA", "Easy QA", false);
        Project archivedProject = buildProject("OLD-QA", "Old QA", true);

        when(repository.findAll()).thenReturn(List.of(activeProject, archivedProject));

        List<ProjectResponse> result = service.getAll(true);

        verify(repository).findAll();
        verify(repository, never()).findAllByArchivedFalse();

        assertEquals(2, result.size());
        assertEquals("EASY-QA", result.get(0).getKey());
        assertFalse(result.get(0).isArchived());

        assertEquals("OLD-QA", result.get(1).getKey());
        assertTrue(result.get(1).isArchived());
    }

    @Test
    void getByKey_existingProject_shouldReturnResponse() {
        Project project = buildProject("EASY-QA", "Easy QA", false);

        when(repository.findByKey("EASY-QA")).thenReturn(Optional.of(project));

        Optional<ProjectResponse> result = service.getByKey("EASY-QA");

        verify(repository).findByKey("EASY-QA");
        assertTrue(result.isPresent());
        assertEquals("EASY-QA", result.get().getKey());
        assertEquals("Easy QA", result.get().getName());
        assertFalse(result.get().isArchived());
    }

    @Test
    void getByKey_missingProject_shouldReturnEmpty() {
        when(repository.findByKey("EASY-QA")).thenReturn(Optional.empty());

        Optional<ProjectResponse> result = service.getByKey("EASY-QA");

        verify(repository).findByKey("EASY-QA");
        assertTrue(result.isEmpty());
    }

    @Test
    void updateName_existingProject_shouldSaveAndReturnUpdatedResponse() {
        Project existingProject = buildProject("EASY-QA", "Easy QA", false);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Easy QA Renamed");

        when(repository.findByKey("EASY-QA")).thenReturn(Optional.of(existingProject));
        when(repository.existsByNameIgnoreCase("Easy QA Renamed")).thenReturn(false);
        when(repository.save(existingProject)).thenReturn(existingProject);

        Optional<ProjectResponse> result = service.updateName("EASY-QA", request);

        verify(repository).findByKey("EASY-QA");
        verify(repository).existsByNameIgnoreCase("Easy QA Renamed");
        verify(repository).save(existingProject);

        assertTrue(result.isPresent());
        assertEquals("Easy QA Renamed", result.get().getName());
        assertEquals("EASY-QA", result.get().getKey());
        assertFalse(result.get().isArchived());
    }

    @Test
    void updateName_blankName_shouldThrowIllegalArgumentException() {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("   ");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateName("EASY-QA", request)
        );

        assertEquals("Project name must not be blank", exception.getMessage());
        verify(repository, never()).findByKey(anyString());
        verify(repository, never()).existsByNameIgnoreCase(anyString());
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    void updateName_nullName_shouldThrowIllegalArgumentException() {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateName("EASY-QA", request)
        );

        assertEquals("Project name must not be blank", exception.getMessage());
        verify(repository, never()).findByKey(anyString());
        verify(repository, never()).existsByNameIgnoreCase(anyString());
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    void updateName_nullRequest_shouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateName("EASY-QA", null)
        );

        assertEquals("Project name must not be blank", exception.getMessage());
        verify(repository, never()).findByKey(anyString());
        verify(repository, never()).existsByNameIgnoreCase(anyString());
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    void updateName_duplicateName_shouldThrowIllegalArgumentException() {
        Project existingProject = buildProject("EASY-QA", "Easy QA", false);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Another Project");

        when(repository.findByKey("EASY-QA")).thenReturn(Optional.of(existingProject));
        when(repository.existsByNameIgnoreCase("Another Project")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.updateName("EASY-QA", request)
        );

        assertEquals("Project name already exists", exception.getMessage());
        verify(repository).findByKey("EASY-QA");
        verify(repository).existsByNameIgnoreCase("Another Project");
        verify(repository, never()).save(any(Project.class));
    }

    @Test
    void updateName_sameNameIgnoringCase_shouldUpdateWithoutDuplicateError() {
        Project existingProject = buildProject("EASY-QA", "Easy QA", false);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("easy qa");

        when(repository.findByKey("EASY-QA")).thenReturn(Optional.of(existingProject));
        when(repository.save(existingProject)).thenReturn(existingProject);

        Optional<ProjectResponse> result = service.updateName("EASY-QA", request);

        verify(repository).findByKey("EASY-QA");
        verify(repository, never()).existsByNameIgnoreCase(anyString());
        verify(repository).save(existingProject);

        assertTrue(result.isPresent());
        assertEquals("easy qa", result.get().getName());
        assertEquals("EASY-QA", result.get().getKey());
    }

    @Test
    void updateName_nameWithExtraSpaces_shouldTrimBeforeSave() {
        Project existingProject = buildProject("EASY-QA", "Easy QA", false);

        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("   Easy QA Renamed   ");

        when(repository.findByKey("EASY-QA")).thenReturn(Optional.of(existingProject));
        when(repository.existsByNameIgnoreCase("Easy QA Renamed")).thenReturn(false);
        when(repository.save(existingProject)).thenReturn(existingProject);

        Optional<ProjectResponse> result = service.updateName("EASY-QA", request);

        verify(repository).findByKey("EASY-QA");
        verify(repository).existsByNameIgnoreCase("Easy QA Renamed");
        verify(repository).save(existingProject);

        assertTrue(result.isPresent());
        assertEquals("Easy QA Renamed", result.get().getName());
        assertEquals("EASY-QA", result.get().getKey());
    }

    @Test
    void updateName_missingProject_shouldReturnEmpty() {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("Renamed");

        when(repository.findByKey("EASY-QA")).thenReturn(Optional.empty());

        Optional<ProjectResponse> result = service.updateName("EASY-QA", request);

        verify(repository).findByKey("EASY-QA");
        verify(repository, never()).existsByNameIgnoreCase(anyString());
        verify(repository, never()).save(any(Project.class));
        assertTrue(result.isEmpty());
    }

    @Test
    void archive_existingProject_shouldSaveArchivedProject() {
        Project existingProject = buildProject("EASY-QA", "Easy QA", false);

        when(repository.findByKey("EASY-QA")).thenReturn(Optional.of(existingProject));
        when(repository.save(existingProject)).thenReturn(existingProject);

        Optional<ProjectResponse> result = service.archive("EASY-QA");

        verify(repository).findByKey("EASY-QA");
        verify(repository).save(existingProject);

        assertTrue(result.isPresent());
        assertTrue(result.get().isArchived());
    }

    @Test
    void archive_missingProject_shouldReturnEmpty() {
        when(repository.findByKey("EASY-QA")).thenReturn(Optional.empty());

        Optional<ProjectResponse> result = service.archive("EASY-QA");

        verify(repository).findByKey("EASY-QA");
        verify(repository, never()).save(any(Project.class));
        assertTrue(result.isEmpty());
    }

    @Test
    void restore_existingProject_shouldSaveRestoredProject() {
        Project existingProject = buildProject("EASY-QA", "Easy QA", true);

        when(repository.findByKey("EASY-QA")).thenReturn(Optional.of(existingProject));
        when(repository.save(existingProject)).thenReturn(existingProject);

        Optional<ProjectResponse> result = service.restore("EASY-QA");

        verify(repository).findByKey("EASY-QA");
        verify(repository).save(existingProject);

        assertTrue(result.isPresent());
        assertFalse(result.get().isArchived());
    }

    @Test
    void restore_missingProject_shouldReturnEmpty() {
        when(repository.findByKey("EASY-QA")).thenReturn(Optional.empty());

        Optional<ProjectResponse> result = service.restore("EASY-QA");

        verify(repository).findByKey("EASY-QA");
        verify(repository, never()).save(any(Project.class));
        assertTrue(result.isEmpty());
    }

    @Test
    void delete_existingProject_shouldReturnTrue() {
        when(repository.deleteByKey("EASY-QA")).thenReturn(1L);

        boolean result = service.delete("EASY-QA");

        verify(repository).deleteByKey("EASY-QA");
        assertTrue(result);
    }

    @Test
    void delete_missingProject_shouldReturnFalse() {
        when(repository.deleteByKey("EASY-QA")).thenReturn(0L);

        boolean result = service.delete("EASY-QA");

        verify(repository).deleteByKey("EASY-QA");
        assertFalse(result);
    }

    private Project buildProject(String key, String name, boolean archived) {
        Project project = new Project(
                name,
                key,
                Instant.parse("2026-03-09T12:00:00Z"),
                archived
        );
        setPrivateField(project, "id", UUID.randomUUID());
        return project;
    }

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}
