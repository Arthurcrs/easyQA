package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.execution.ExecutionStatus;
import com.arthur.easy_qa.dto.execution.ExecutionResponse;
import com.arthur.easy_qa.dto.execution.UpdateExecutionRequest;
import com.arthur.easy_qa.service.ExecutionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExecutionController.class)
class ExecutionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ExecutionService executionService;

    private ExecutionResponse defaultResponse;
    private final String PROJECT_KEY = "EASYQA";
    private final Long TEST_CYCLE_NUMBER = 1L;
    private final Long TEST_CASE_NUMBER = 100L;
    private final Long EXECUTION_NUMBER = 5L;

    @BeforeEach
    void setUp() {
        defaultResponse = new ExecutionResponse(
                PROJECT_KEY,
                TEST_CYCLE_NUMBER,
                TEST_CASE_NUMBER,
                EXECUTION_NUMBER,
                "Login User Story",
                ExecutionStatus.NOT_EXECUTED
        );
    }

    @Test
    void getByCycle_ShouldReturn200AndListOfExecutions() throws Exception {
        when(executionService.getExecutionsByCycle(eq(PROJECT_KEY), eq(TEST_CYCLE_NUMBER), any(), any()))
                .thenReturn(List.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cycles/{cycleNumber}/executions", PROJECT_KEY, TEST_CYCLE_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].executionNumber").value(EXECUTION_NUMBER))
                .andExpect(jsonPath("$[0].status").value("NOT_EXECUTED"));
    }

    @Test
    void getByNumber_ShouldReturn200_WhenExecutionExists() throws Exception {
        when(executionService.getByProjectAndNumber(PROJECT_KEY, EXECUTION_NUMBER))
                .thenReturn(Optional.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/executions/{executionNumber}", PROJECT_KEY, EXECUTION_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.executionNumber").value(EXECUTION_NUMBER))
                .andExpect(jsonPath("$.testCaseUs").value("Login User Story"));
    }

    @Test
    void getByNumber_ShouldReturn404_WhenExecutionDoesNotExist() throws Exception {
        when(executionService.getByProjectAndNumber(PROJECT_KEY, EXECUTION_NUMBER))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/projects/{projectKey}/executions/{executionNumber}", PROJECT_KEY, EXECUTION_NUMBER))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_ShouldReturn200_WhenExecutionExists() throws Exception {
        UpdateExecutionRequest request = new UpdateExecutionRequest();
        request.setStatus(ExecutionStatus.PASS);

        ExecutionResponse updatedResponse = new ExecutionResponse(
                PROJECT_KEY, TEST_CYCLE_NUMBER, TEST_CASE_NUMBER, EXECUTION_NUMBER, "Login User Story", ExecutionStatus.PASS
        );

        when(executionService.updateStatus(eq(PROJECT_KEY), eq(EXECUTION_NUMBER), any(UpdateExecutionRequest.class)))
                .thenReturn(Optional.of(updatedResponse));

        mockMvc.perform(patch("/api/v1/projects/{projectKey}/executions/{executionNumber}", PROJECT_KEY, EXECUTION_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PASS"));
    }

    @Test
    void updateStatus_ShouldReturn400_WhenStatusIsNull() throws Exception {
        UpdateExecutionRequest request = new UpdateExecutionRequest();

        mockMvc.perform(patch("/api/v1/projects/{projectKey}/executions/{executionNumber}", PROJECT_KEY, EXECUTION_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_ShouldReturn404_WhenExecutionDoesNotExist() throws Exception {
        UpdateExecutionRequest request = new UpdateExecutionRequest();
        request.setStatus(ExecutionStatus.FAIL);

        when(executionService.updateStatus(eq(PROJECT_KEY), eq(EXECUTION_NUMBER), any(UpdateExecutionRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/v1/projects/{projectKey}/executions/{executionNumber}", PROJECT_KEY, EXECUTION_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void linkBug_ShouldReturn200() throws Exception {
        Long bugNumber = 10L;

        mockMvc.perform(post("/api/v1/projects/{projectKey}/executions/{executionNumber}/bugs/{bugNumber}",
                        PROJECT_KEY, EXECUTION_NUMBER, bugNumber))
                .andExpect(status().isOk());

        verify(executionService).linkBug(PROJECT_KEY, EXECUTION_NUMBER, bugNumber);
    }

    @Test
    void unlinkBug_ShouldReturn204() throws Exception {
        Long bugNumber = 10L;

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/executions/{executionNumber}/bugs/{bugNumber}",
                        PROJECT_KEY, EXECUTION_NUMBER, bugNumber))
                .andExpect(status().isNoContent());

        verify(executionService).unlinkBug(PROJECT_KEY, EXECUTION_NUMBER, bugNumber);
    }
}