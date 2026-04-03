package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.*;
import com.arthur.easy_qa.dto.execution.ExecutionResponse;
import com.arthur.easy_qa.dto.execution.UpdateExecutionRequest;
import com.arthur.easy_qa.repository.execution.ExecutionRepository;
import com.arthur.easy_qa.repository.testcycle.TestCycleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExecutionServiceTest {

    private ExecutionRepository executionRepository;
    private TestCycleRepository testCycleRepository;
    private ExecutionService service;

    private final String PROJECT_KEY = "EASYQA";
    private Project project;
    private TestCycle testCycle;
    private TestCase testCase;
    private Execution defaultExecution;

    @BeforeEach
    void setup() {
        executionRepository = mock(ExecutionRepository.class);
        testCycleRepository = mock(TestCycleRepository.class);
        service = new ExecutionService(executionRepository, testCycleRepository);

        project = new Project("EasyQA", PROJECT_KEY, Instant.now(), false);

        testCycle = new TestCycle(project, 1L, "Release 1.0", "v1.0", "Prod", "Reg");

        testCase = new TestCase(
                project, 100L, "Login User Story", TestCaseStatus.DRAFT,
                "Auth", "Login", "Desc", TestCasePriority.HIGH, TestCaseType.FUNCTIONAL
        );

        defaultExecution = new Execution(project, 5L, testCycle, testCase);
    }

    @Test
    void getExecutionsByCycle_cycleFound_shouldReturnMappedList() {
        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L))
                .thenReturn(Optional.of(testCycle));

        when(executionRepository.findAllByTestCycle(testCycle))
                .thenReturn(List.of(defaultExecution));

        List<ExecutionResponse> result = service.getExecutionsByCycle(PROJECT_KEY, 1L);

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getExecutionNumber());
        assertEquals(100L, result.get(0).getTestCaseNumber());
        assertEquals("Login User Story", result.get(0).getTestCaseUs());
        assertEquals(ExecutionStatus.NOT_EXECUTED, result.get(0).getStatus());
    }

    @Test
    void getExecutionsByCycle_cycleNotFound_shouldThrowException() {
        when(testCycleRepository.findByProjectKeyAndTestCycleNumber(PROJECT_KEY, 1L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> service.getExecutionsByCycle(PROJECT_KEY, 1L));

        assertEquals("Test Cycle not found", exception.getMessage());
        verify(executionRepository, never()).findAllByTestCycle(any());
    }

    @Test
    void getByProjectAndNumber_executionFound_shouldReturnResponse() {
        when(executionRepository.findByProjectKeyAndExecutionNumber(PROJECT_KEY, 5L))
                .thenReturn(Optional.of(defaultExecution));

        Optional<ExecutionResponse> response = service.getByProjectAndNumber(PROJECT_KEY, 5L);

        assertTrue(response.isPresent());
        assertEquals(5L, response.get().getExecutionNumber());
        assertEquals(ExecutionStatus.NOT_EXECUTED, response.get().getStatus());
    }

    @Test
    void getByProjectAndNumber_executionNotFound_shouldReturnEmpty() {
        when(executionRepository.findByProjectKeyAndExecutionNumber(PROJECT_KEY, 5L))
                .thenReturn(Optional.empty());

        Optional<ExecutionResponse> response = service.getByProjectAndNumber(PROJECT_KEY, 5L);

        assertTrue(response.isEmpty());
    }

    @Test
    void updateStatus_executionFound_shouldUpdateSaveAndReturnResponse() {
        UpdateExecutionRequest request = new UpdateExecutionRequest();
        request.setStatus(ExecutionStatus.PASS);

        when(executionRepository.findByProjectKeyAndExecutionNumber(PROJECT_KEY, 5L))
                .thenReturn(Optional.of(defaultExecution));
        when(executionRepository.save(any(Execution.class))).thenAnswer(i -> i.getArgument(0));

        Optional<ExecutionResponse> response = service.updateStatus(PROJECT_KEY, 5L, request);

        assertTrue(response.isPresent());
        assertEquals(ExecutionStatus.PASS, response.get().getStatus());

        ArgumentCaptor<Execution> captor = ArgumentCaptor.forClass(Execution.class);
        verify(executionRepository).save(captor.capture());

        assertEquals(ExecutionStatus.PASS, captor.getValue().getStatus());
    }

    @Test
    void updateStatus_executionNotFound_shouldReturnEmpty() {
        UpdateExecutionRequest request = new UpdateExecutionRequest();
        request.setStatus(ExecutionStatus.FAIL);

        when(executionRepository.findByProjectKeyAndExecutionNumber(PROJECT_KEY, 5L))
                .thenReturn(Optional.empty());

        Optional<ExecutionResponse> response = service.updateStatus(PROJECT_KEY, 5L, request);

        assertTrue(response.isEmpty());
        verify(executionRepository, never()).save(any());
    }
}