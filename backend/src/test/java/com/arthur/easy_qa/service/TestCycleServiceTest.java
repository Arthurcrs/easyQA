package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.*;
import com.arthur.easy_qa.dto.testcycle.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.testcycle.TestCycleDetailsResponse;
import com.arthur.easy_qa.dto.testcycle.TestCycleResponse;
import com.arthur.easy_qa.repository.execution.ExecutionRepository;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import com.arthur.easy_qa.repository.testcase.TestCaseRepository;
import com.arthur.easy_qa.repository.testcycle.TestCycleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

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
    private TestCaseRepository testCaseRepository;
    private ExecutionRepository executionRepository;
    private TestCycleService service;

    private final String PROJECT_KEY = "EASYQA";
    private Project project;
    private TestCycle defaultCycle;
    private TestCase defaultTestCase;

    @BeforeEach
    void setup() {
        testCycleRepository = mock(TestCycleRepository.class);
        projectRepository = mock(ProjectRepository.class);
        testCaseRepository = mock(TestCaseRepository.class);
        executionRepository = mock(ExecutionRepository.class);

        service = new TestCycleService(
                testCycleRepository,
                projectRepository,
                testCaseRepository,
                executionRepository
        );

        project = new Project("EasyQA", PROJECT_KEY, Instant.now(), false);

        defaultCycle = new TestCycle(project, 1L, "Release 1.0", "v1.0", "Prod", "Reg");

        defaultTestCase = new TestCase(
                project, 100L, "US-1", TestCaseStatus.DRAFT,
                "Auth", "Login", "Desc", TestCasePriority.HIGH, TestCaseType.FUNCTIONAL
        );
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

        assertNotNull(response);
        assertEquals(1L, response.getTestCycleNumber());
        assertEquals("Release 1.0", response.getName());
        verify(testCycleRepository).save(any(TestCycle.class));
    }

    @Test
    void getByProjectAndNumber_found_shouldReturnResponse() {
        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L))
                .thenReturn(Optional.of(defaultCycle));

        Optional<TestCycleResponse> response = service.getByProjectAndNumber(PROJECT_KEY, 1L);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get().getTestCycleNumber());
    }

    @Test
    void getAllByProject_shouldReturnMappedList() {
        when(testCycleRepository.findAllByProjectKey(PROJECT_KEY)).thenReturn(List.of(defaultCycle));

        List<TestCycleResponse> result = service.getAllByProject(PROJECT_KEY);

        assertEquals(1, result.size());
        assertEquals("Release 1.0", result.get(0).getName());
    }

    @Test
    void update_found_shouldUpdateAndReturnResponse() {
        CreateTestCycleRequest request = new CreateTestCycleRequest();
        request.setName("Updated Name");

        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L))
                .thenReturn(Optional.of(defaultCycle));
        when(testCycleRepository.save(any(TestCycle.class))).thenAnswer(i -> i.getArgument(0));

        Optional<TestCycleResponse> response = service.update(PROJECT_KEY, 1L, request);

        assertTrue(response.isPresent());
        assertEquals("Updated Name", response.get().getName());
    }

    @Test
    void delete_shouldReturnRepositoryResult() {
        when(testCycleRepository.deleteByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L)).thenReturn(true);

        boolean result = service.delete(PROJECT_KEY, 1L);

        assertTrue(result);
        verify(testCycleRepository).deleteByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L);
    }

    @Test
    void addTestCasesToCycle_validRequest_shouldCreateExecutions() {
        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L)).thenReturn(Optional.of(defaultCycle));
        when(executionRepository.findMaxExecutionNumberByProjectKey(PROJECT_KEY)).thenReturn(Optional.of(10L));
        when(testCaseRepository.findByProjectKeyAndTestCaseNumber(PROJECT_KEY, 100L)).thenReturn(Optional.of(defaultTestCase));
        when(executionRepository.existsByTestCycleAndTestCase(defaultCycle, defaultTestCase)).thenReturn(false);

        service.addTestCasesToCycle(PROJECT_KEY, 1L, List.of(100L));

        ArgumentCaptor<Execution> captor = ArgumentCaptor.forClass(Execution.class);
        verify(executionRepository).save(captor.capture());

        assertEquals(11L, captor.getValue().getExecutionNumber());
        assertEquals(defaultCycle, captor.getValue().getTestCycle());
        assertEquals(defaultTestCase, captor.getValue().getTestCase());
    }

    @Test
    void removeTestCaseFromCycle_shouldCallExecutionRepository() {
        service.removeTestCaseFromCycle(PROJECT_KEY, 1L, 100L);
        verify(executionRepository).deleteByProjectKeyAndCycleNumberAndCaseNumber(PROJECT_KEY, 1L, 100L);
    }

    @Test
    void duplicate_validCycle_shouldCopyCycleAndExecutions() {
        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L)).thenReturn(Optional.of(defaultCycle));
        when(testCycleRepository.findMaxTestCycleNumberByProjectKey(PROJECT_KEY)).thenReturn(Optional.of(1L));
        when(executionRepository.findMaxExecutionNumberByProjectKey(PROJECT_KEY)).thenReturn(Optional.of(50L));

        Execution existingExec = new Execution(project, 50L, defaultCycle, defaultTestCase);
        when(executionRepository.findAllByTestCycle(defaultCycle)).thenReturn(List.of(existingExec));
        when(testCycleRepository.save(any(TestCycle.class))).thenAnswer(i -> i.getArgument(0));

        TestCycleResponse response = service.duplicate(PROJECT_KEY, 1L);

        assertEquals(2L, response.getTestCycleNumber());
        assertEquals("Release 1.0 (Copy)", response.getName());

        ArgumentCaptor<Execution> execCaptor = ArgumentCaptor.forClass(Execution.class);
        verify(executionRepository).save(execCaptor.capture());
        assertEquals(51L, execCaptor.getValue().getExecutionNumber());
        assertEquals(ExecutionStatus.NOT_EXECUTED, execCaptor.getValue().getStatus());
    }

    private static void simulateJpaPrePersist(TestCycle testCycle) {
        try {
            if (testCycle.getId() == null) {
                setPrivateField(testCycle, "id", UUID.randomUUID());
            }
            Method onCreate = testCycle.getClass().getDeclaredMethod("onCreate");
            onCreate.setAccessible(true);
            onCreate.invoke(testCycle);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void getDetailsByProjectAndNumber_found_shouldReturnDetailedResponse() {
        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L))
                .thenReturn(Optional.of(defaultCycle));

        Execution exec1 = new Execution(project, 10L, defaultCycle, defaultTestCase);
        exec1.setStatus(ExecutionStatus.PASS);

        Execution exec2 = new Execution(project, 11L, defaultCycle, defaultTestCase);
        exec2.setStatus(ExecutionStatus.FAIL);

        when(executionRepository.findAllByTestCycle(defaultCycle)).thenReturn(List.of(exec1, exec2));

        Optional<TestCycleDetailsResponse> response = service.getDetailsByProjectAndNumber(PROJECT_KEY, 1L);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get().getTestCycleNumber());
        assertEquals(2, response.get().getExecutions().size());

        java.util.Map<ExecutionStatus, Long> summary = response.get().getProgressSummary();
        assertEquals(1L, summary.get(ExecutionStatus.PASS));
        assertEquals(1L, summary.get(ExecutionStatus.FAIL));
        assertEquals(0L, summary.get(ExecutionStatus.NOT_EXECUTED));
        assertEquals(0L, summary.get(ExecutionStatus.BLOCKED));
    }
}