package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.execution.ExecutionStatus;
import com.arthur.easy_qa.dto.testcycle.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.testcycle.TestCycleDetailsResponse;
import com.arthur.easy_qa.dto.testcycle.TestCycleResponse;
import com.arthur.easy_qa.service.TestCycleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestCycleController.class)
class TestCycleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TestCycleService testCycleService;

    private TestCycleResponse defaultResponse;
    private final String PROJECT_KEY = "EASYQA";
    private final Long TEST_CYCLE_NUMBER = 1L;

    @BeforeEach
    void setUp() {
        defaultResponse = new TestCycleResponse(
                PROJECT_KEY,
                TEST_CYCLE_NUMBER,
                "Release 1.0",
                "v1.0.0",
                "Production",
                "Regression",
                Instant.now(),
                Instant.now()
        );
    }

    @Test
    void create_ShouldReturn201AndTestCycleResponse() throws Exception {
        CreateTestCycleRequest request = new CreateTestCycleRequest();
        request.setName("Release 1.0");
        request.setVersion("v1.0.0");
        request.setEnvironment("Production");
        request.setType("Regression");

        when(testCycleService.create(eq(PROJECT_KEY), any(CreateTestCycleRequest.class)))
                .thenReturn(defaultResponse);

        mockMvc.perform(post("/api/v1/projects/{projectKey}/test-cycles", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/projects/" + PROJECT_KEY + "/test-cycles/" + TEST_CYCLE_NUMBER))
                .andExpect(jsonPath("$.projectKey").value(PROJECT_KEY))
                .andExpect(jsonPath("$.testCycleNumber").value(TEST_CYCLE_NUMBER))
                .andExpect(jsonPath("$.name").value("Release 1.0"));
    }

    @Test
    void create_ShouldReturn400_WhenNameIsBlank() throws Exception {
        CreateTestCycleRequest request = new CreateTestCycleRequest();
        // Name is explicitly null/blank to trigger @NotBlank validation
        request.setVersion("v1.0.0");

        mockMvc.perform(post("/api/v1/projects/{projectKey}/test-cycles", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByNumber_ShouldReturn200_WhenCycleExists() throws Exception {
        TestCycleDetailsResponse detailsResponse = new TestCycleDetailsResponse(
                PROJECT_KEY,
                TEST_CYCLE_NUMBER,
                "Release 1.0",
                "v1.0.0",
                "Production",
                "Regression",
                Instant.now(),
                Instant.now(),
                java.util.Map.of(ExecutionStatus.PASS, 5L, ExecutionStatus.FAIL, 1L),
                List.of()
        );

        when(testCycleService.getDetailsByProjectAndNumber(PROJECT_KEY, TEST_CYCLE_NUMBER))
                .thenReturn(Optional.of(detailsResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cycles/{number}", PROJECT_KEY, TEST_CYCLE_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testCycleNumber").value(TEST_CYCLE_NUMBER))
                .andExpect(jsonPath("$.name").value("Release 1.0"))
                .andExpect(jsonPath("$.progressSummary.PASS").value(5))
                .andExpect(jsonPath("$.progressSummary.FAIL").value(1));
    }

    @Test
    void getByNumber_ShouldReturn404_WhenCycleDoesNotExist() throws Exception {
        when(testCycleService.getDetailsByProjectAndNumber(PROJECT_KEY, TEST_CYCLE_NUMBER))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cycles/{number}", PROJECT_KEY, TEST_CYCLE_NUMBER))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturnListOfCycles() throws Exception {
        when(testCycleService.getAllByProject(PROJECT_KEY)).thenReturn(List.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cycles", PROJECT_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Release 1.0"));
    }

    @Test
    void update_ShouldReturn200_WhenCycleExists() throws Exception {
        CreateTestCycleRequest request = new CreateTestCycleRequest();
        request.setName("Updated Release");

        TestCycleResponse updatedResponse = new TestCycleResponse(
                PROJECT_KEY,
                TEST_CYCLE_NUMBER,
                "Updated Release",
                "v1.0.0",
                "Production",
                "Regression",
                defaultResponse.getCreationInstant(),
                Instant.now()
        );

        when(testCycleService.update(eq(PROJECT_KEY), eq(TEST_CYCLE_NUMBER), any(CreateTestCycleRequest.class)))
                .thenReturn(Optional.of(updatedResponse));

        mockMvc.perform(patch("/api/v1/projects/{projectKey}/test-cycles/{number}", PROJECT_KEY, TEST_CYCLE_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Release"));
    }

    @Test
    void update_ShouldReturn404_WhenCycleDoesNotExist() throws Exception {
        CreateTestCycleRequest request = new CreateTestCycleRequest();
        request.setName("Updated Release");

        when(testCycleService.update(eq(PROJECT_KEY), eq(TEST_CYCLE_NUMBER), any(CreateTestCycleRequest.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(patch("/api/v1/projects/{projectKey}/test-cycles/{number}", PROJECT_KEY, TEST_CYCLE_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturn204_WhenCycleExists() throws Exception {
        when(testCycleService.delete(PROJECT_KEY, TEST_CYCLE_NUMBER)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/test-cycles/{number}", PROJECT_KEY, TEST_CYCLE_NUMBER))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenCycleDoesNotExist() throws Exception {
        when(testCycleService.delete(PROJECT_KEY, TEST_CYCLE_NUMBER)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/test-cycles/{number}", PROJECT_KEY, TEST_CYCLE_NUMBER))
                .andExpect(status().isNotFound());
    }

    // --- Execution & Linking Tests ---

    @Test
    void addTestCases_ShouldReturn200() throws Exception {
        List<Long> testCaseNumbers = List.of(100L, 101L);

        mockMvc.perform(post("/api/v1/projects/{projectKey}/test-cycles/{number}/test-cases", PROJECT_KEY, TEST_CYCLE_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testCaseNumbers)))
                .andExpect(status().isOk());

        verify(testCycleService).addTestCasesToCycle(PROJECT_KEY, TEST_CYCLE_NUMBER, testCaseNumbers);
    }

    @Test
    void removeTestCase_ShouldReturn204() throws Exception {
        Long testCaseNumberToRemove = 100L;

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/test-cycles/{number}/test-cases/{testCaseNumber}",
                        PROJECT_KEY, TEST_CYCLE_NUMBER, testCaseNumberToRemove))
                .andExpect(status().isNoContent());

        verify(testCycleService).removeTestCaseFromCycle(PROJECT_KEY, TEST_CYCLE_NUMBER, testCaseNumberToRemove);
    }

    @Test
    void duplicate_ShouldReturn201AndDuplicatedCycle() throws Exception {
        TestCycleResponse duplicatedResponse = new TestCycleResponse(
                PROJECT_KEY,
                2L,
                "Release 1.0 (Copy)",
                "v1.0.0",
                "Production",
                "Regression",
                Instant.now(),
                Instant.now()
        );

        when(testCycleService.duplicate(PROJECT_KEY, TEST_CYCLE_NUMBER)).thenReturn(duplicatedResponse);

        mockMvc.perform(post("/api/v1/projects/{projectKey}/test-cycles/{number}/duplicate", PROJECT_KEY, TEST_CYCLE_NUMBER))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.testCycleNumber").value(2L))
                .andExpect(jsonPath("$.name").value("Release 1.0 (Copy)"));
    }
}