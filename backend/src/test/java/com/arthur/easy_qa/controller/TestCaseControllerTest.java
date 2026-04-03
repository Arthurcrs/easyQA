package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.TestCasePriority;
import com.arthur.easy_qa.domain.TestCaseStatus;
import com.arthur.easy_qa.domain.TestCaseType;
import com.arthur.easy_qa.dto.testcase.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.testcase.TestCaseResponse;
import com.arthur.easy_qa.service.TestCaseService;
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
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestCaseController.class)
class TestCaseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TestCaseService testCaseService;

    private TestCaseResponse defaultResponse;
    private final String PROJECT_KEY = "EASYQA";
    private final Long TEST_CASE_NUMBER = 1L;

    @BeforeEach
    void setUp() {
        defaultResponse = new TestCaseResponse(
                PROJECT_KEY,
                TEST_CASE_NUMBER,
                "US-100",
                "Authentication",
                "Successful Login",
                "User logs in with valid credentials",
                TestCaseStatus.DRAFT,
                TestCasePriority.HIGH,
                TestCaseType.FUNCTIONAL,
                Instant.now(),
                Instant.now(),
                Map.of("Browser", "Chrome")
        );
    }

    @Test
    void create_ShouldReturn201AndTestCaseResponse() throws Exception {
        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setUs("US-100");
        request.setFeature("Authentication");
        request.setScenario("Successful Login");
        request.setDescription("User logs in with valid credentials");
        request.setStatus(TestCaseStatus.DRAFT);
        request.setPriority(TestCasePriority.HIGH);
        request.setType(TestCaseType.FUNCTIONAL);
        request.setCustomFields(Map.of(10L, "Chrome"));

        when(testCaseService.create(eq(PROJECT_KEY), any(CreateTestCaseRequest.class)))
                .thenReturn(defaultResponse);

        mockMvc.perform(post("/api/v1/projects/{projectKey}/test-cases", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/projects/" + PROJECT_KEY + "/test-cases/" + TEST_CASE_NUMBER))
                .andExpect(jsonPath("$.projectKey").value(PROJECT_KEY))
                .andExpect(jsonPath("$.testCaseNumber").value(TEST_CASE_NUMBER))
                .andExpect(jsonPath("$.scenario").value("Successful Login"))
                .andExpect(jsonPath("$.customFields.Browser").value("Chrome"));
    }

    @Test
    void getByNumber_ShouldReturn200_WhenTestCaseExists() throws Exception {
        when(testCaseService.getByProjectAndNumber(PROJECT_KEY, TEST_CASE_NUMBER))
                .thenReturn(Optional.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cases/{number}", PROJECT_KEY, TEST_CASE_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testCaseNumber").value(TEST_CASE_NUMBER))
                .andExpect(jsonPath("$.scenario").value("Successful Login"));
    }

    @Test
    void getByNumber_ShouldReturn404_WhenTestCaseDoesNotExist() throws Exception {
        when(testCaseService.getByProjectAndNumber(PROJECT_KEY, TEST_CASE_NUMBER))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cases/{number}", PROJECT_KEY, TEST_CASE_NUMBER))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturnListOfTestCases() throws Exception {
        when(testCaseService.getAllByProject(eq(PROJECT_KEY), any(), any(), any(), any())).thenReturn(List.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cases", PROJECT_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].scenario").value("Successful Login"));
    }

    @Test
    void update_ShouldReturn200_WhenTestCaseExists() throws Exception {
        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setUs("US-100");
        request.setFeature("Authentication");
        request.setScenario("Updated Scenario");
        request.setDescription("Updated description");
        request.setStatus(TestCaseStatus.FINISHED);
        request.setPriority(TestCasePriority.HIGH);
        request.setType(TestCaseType.FUNCTIONAL);

        TestCaseResponse updatedResponse = new TestCaseResponse(
                PROJECT_KEY, TEST_CASE_NUMBER, "US-100", "Authentication", "Updated Scenario",
                "Updated description", TestCaseStatus.FINISHED, TestCasePriority.HIGH,
                TestCaseType.FUNCTIONAL, defaultResponse.getCreationInstant(), Instant.now(), Map.of()
        );

        when(testCaseService.update(eq(PROJECT_KEY), eq(TEST_CASE_NUMBER), any(CreateTestCaseRequest.class)))
                .thenReturn(Optional.of(updatedResponse));

        mockMvc.perform(patch("/api/v1/projects/{projectKey}/test-cases/{number}", PROJECT_KEY, TEST_CASE_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenario").value("Updated Scenario"));
    }

    @Test
    void delete_ShouldReturn204_WhenTestCaseExists() throws Exception {
        when(testCaseService.delete(PROJECT_KEY, TEST_CASE_NUMBER)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/test-cases/{number}", PROJECT_KEY, TEST_CASE_NUMBER))
                .andExpect(status().isNoContent());
    }
}