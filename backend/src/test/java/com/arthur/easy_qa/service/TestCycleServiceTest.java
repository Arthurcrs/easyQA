package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.domain.TestCycle;
import com.arthur.easy_qa.dto.testcycle.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.testcycle.TestCycleResponse;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import com.arthur.easy_qa.repository.testcycle.TestCycleRepository;
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

class TestCycleServiceTest {

    private TestCycleRepository testCycleRepository;
    private ProjectRepository projectRepository;
    private TestCycleService service;

    private final String PROJECT_KEY = "EASYQA";
    private Project project;
    private TestCycle defaultCycle;

    @BeforeEach
    void setup() {
        testCycleRepository = mock(TestCycleRepository.class);
        projectRepository = mock(ProjectRepository.class);
        service = new TestCycleService(testCycleRepository, projectRepository);

        project = new Project("EasyQA", PROJECT_KEY, Instant.now(), false);
        defaultCycle = new TestCycle(project, 1L, "Release 1.0", "v1.0.0", "Production", "Regression");
    }

    @Test
    void create_validRequest_shouldSaveAndReturnResponse() {
        CreateTestCycleRequest request = new CreateTestCycleRequest();
        request.setName("Release 1.0");

        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(project));
        when(testCycleRepository.findMaxTestCycleNumberByProjectKey(PROJECT_KEY)).thenReturn(Optional.of(0L));
        when(testCycleRepository.save(any(TestCycle.class))).thenAnswer(invocation -> {
            TestCycle tc = invocation.getArgument(0);
            simulateJpaPrePersist(tc);
            return tc;
        });

        TestCycleResponse response = service.create(PROJECT_KEY, request);

        assertEquals(1L, response.getTestCycleNumber());
        assertEquals("Release 1.0", response.getName());
    }

    @Test
    void create_projectNotFound_shouldThrowException() {
        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.create(PROJECT_KEY, new CreateTestCycleRequest()));
    }

    @Test
    void getByProjectAndNumber_found_shouldReturnResponse() {
        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L)).thenReturn(Optional.of(defaultCycle));

        Optional<TestCycleResponse> response = service.getByProjectAndNumber(PROJECT_KEY, 1L);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get().getTestCycleNumber());
    }

    @Test
    void getByProjectAndNumber_notFound_shouldReturnEmpty() {
        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L)).thenReturn(Optional.empty());
        assertTrue(service.getByProjectAndNumber(PROJECT_KEY, 1L).isEmpty());
    }

    @Test
    void getAllByProject_shouldReturnMappedList() {
        when(testCycleRepository.findAllByProjectKey(PROJECT_KEY)).thenReturn(List.of(defaultCycle));

        List<TestCycleResponse> result = service.getAllByProject(PROJECT_KEY);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getTestCycleNumber());
    }

    @Test
    void update_found_shouldUpdateAndReturnResponse() {
        CreateTestCycleRequest request = new CreateTestCycleRequest();
        request.setName("Updated Release");

        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L)).thenReturn(Optional.of(defaultCycle));
        when(testCycleRepository.save(any(TestCycle.class))).thenAnswer(i -> i.getArgument(0));

        Optional<TestCycleResponse> response = service.update(PROJECT_KEY, 1L, request);

        assertTrue(response.isPresent());
        assertEquals("Updated Release", response.get().getName());
    }

    @Test
    void update_notFound_shouldReturnEmpty() {
        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L)).thenReturn(Optional.empty());
        assertTrue(service.update(PROJECT_KEY, 1L, new CreateTestCycleRequest()).isEmpty());
    }

    @Test
    void delete_shouldReturnRepositoryResult() {
        when(testCycleRepository.deleteByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L)).thenReturn(true);
        assertTrue(service.delete(PROJECT_KEY, 1L));
        verify(testCycleRepository).deleteByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L);
    }

    private static void simulateJpaPrePersist(TestCycle testCycle) {
        if (testCycle.getId() == null) {
            try {
                Field field = testCycle.getClass().getDeclaredField("id");
                field.setAccessible(true);
                field.set(testCycle, UUID.randomUUID());

                Method method = testCycle.getClass().getDeclaredMethod("onCreate");
                method.setAccessible(true);
                method.invoke(testCycle);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}