package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.Bug;
import com.arthur.easy_qa.domain.BugSeverity;
import com.arthur.easy_qa.domain.BugStatus;
import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.dto.bug.BugDetailsResponse;
import com.arthur.easy_qa.dto.bug.BugResponse;
import com.arthur.easy_qa.dto.bug.CreateBugRequest;
import com.arthur.easy_qa.repository.bug.BugRepository;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BugServiceTest {

    private BugRepository bugRepository;
    private ProjectRepository projectRepository;
    private BugService service;

    private final String PROJECT_KEY = "EASYQA";
    private Project project;
    private Bug defaultBug;

    @BeforeEach
    void setup() {
        bugRepository = mock(BugRepository.class);
        projectRepository = mock(ProjectRepository.class);
        service = new BugService(bugRepository, projectRepository);

        project = new Project("EasyQA", PROJECT_KEY, Instant.now(), false);
        defaultBug = new Bug(project, 1L, "Login page crashes", "Null pointer exception on click", BugSeverity.HIGH);
    }

    @Test
    void create_validRequest_shouldSaveAndReturnResponse() {
        CreateBugRequest request = new CreateBugRequest();
        request.setTitle("Login page crashes");
        request.setSeverity(BugSeverity.HIGH);

        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(project));
        when(bugRepository.findMaxBugNumberByProjectKey(PROJECT_KEY)).thenReturn(Optional.of(0L));
        when(bugRepository.save(any(Bug.class))).thenAnswer(invocation -> {
            Bug b = invocation.getArgument(0);
            simulateJpaPrePersist(b);
            return b;
        });

        BugResponse response = service.create(PROJECT_KEY, request);

        assertNotNull(response);
        assertEquals(1L, response.getBugNumber());
        assertEquals("Login page crashes", response.getTitle());
        assertEquals(BugSeverity.HIGH, response.getSeverity());
        assertEquals(BugStatus.OPEN, response.getStatus());
        assertNotNull(response.getOpenDate());
        verify(bugRepository, times(1)).save(any(Bug.class));
    }

    @Test
    void create_projectNotFound_shouldThrowException() {
        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.create(PROJECT_KEY, new CreateBugRequest()));
        verify(bugRepository, never()).save(any(Bug.class));
    }

    @Test
    void getByProjectAndNumber_found_shouldReturnResponse() {
        when(bugRepository.findByProjectKeyAndBugNumber(PROJECT_KEY, 1L))
                .thenReturn(Optional.of(defaultBug));

        Optional<BugDetailsResponse> response = service.getByProjectAndNumber(PROJECT_KEY, 1L);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get().getBugNumber());
        assertTrue(response.get().getLinkedExecutions().isEmpty());
    }

    @Test
    void getAllByProject_shouldPassFiltersToRepository() {
        when(bugRepository.findAllByProjectKeyAndFilters(PROJECT_KEY, BugStatus.OPEN, BugSeverity.HIGH))
                .thenReturn(List.of(defaultBug));

        List<BugResponse> result = service.getAllByProject(PROJECT_KEY, BugStatus.OPEN, BugSeverity.HIGH);

        assertEquals(1, result.size());
        verify(bugRepository).findAllByProjectKeyAndFilters(PROJECT_KEY, BugStatus.OPEN, BugSeverity.HIGH);
    }

    @Test
    void update_found_shouldUpdateAndReturnResponse() {
        CreateBugRequest request = new CreateBugRequest();
        request.setTitle("Updated Title");
        request.setSeverity(BugSeverity.CRITICAL);
        request.setStatus(BugStatus.RESOLVED);

        when(bugRepository.findByProjectKeyAndBugNumber(PROJECT_KEY, 1L)).thenReturn(Optional.of(defaultBug));
        when(bugRepository.save(any(Bug.class))).thenAnswer(i -> i.getArgument(0));

        Optional<BugResponse> response = service.update(PROJECT_KEY, 1L, request);

        assertTrue(response.isPresent());
        assertEquals("Updated Title", response.get().getTitle());
        assertEquals(BugSeverity.CRITICAL, response.get().getSeverity());
        assertEquals(BugStatus.RESOLVED, response.get().getStatus());
    }

    @Test
    void delete_shouldReturnRepositoryResult() {
        when(bugRepository.deleteByProjectKeyAndBugNumber(PROJECT_KEY, 1L)).thenReturn(true);

        assertTrue(service.delete(PROJECT_KEY, 1L));
        verify(bugRepository).deleteByProjectKeyAndBugNumber(PROJECT_KEY, 1L);
    }

    private static void simulateJpaPrePersist(Bug bug) {
        if (bug.getId() == null) {
            try {
                Field field = bug.getClass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(bug, UUID.randomUUID());

                Method method = bug.getClass().getDeclaredMethod("onPersist");
                method.setAccessible(true);
                method.invoke(bug);
            } catch (Exception e) {
                throw new RuntimeException("Failed to simulate JPA PrePersist", e);
            }
        }
    }
}