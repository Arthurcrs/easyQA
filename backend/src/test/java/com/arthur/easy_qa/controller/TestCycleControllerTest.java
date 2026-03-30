package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.dto.testcycle.CreateTestCycleRequest;
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
                PROJECT_KEY, TEST_CYCLE_NUMBER, "Release 1.0", "v1.0.0", "Production", "Regression", Instant.now(), Instant.now()
        );
    }

    @Test
    void create_ShouldReturn201AndTestCycleResponse() throws Exception {
        CreateTestCycleRequest request = new CreateTestCycleRequest();
        request.setName("Release 1.0");

        when(testCycleService.create(eq(PROJECT_KEY), any(CreateTestCycleRequest.class))).thenReturn(defaultResponse);

        mockMvc.perform(post("/api/v1/projects/{projectKey}/test-cycles", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/projects/" + PROJECT_KEY + "/test-cycles/" + TEST_CYCLE_NUMBER))
                .andExpect(jsonPath("$.name").value("Release 1.0"));
    }

    @Test
    void getByNumber_ShouldReturn200_WhenCycleExists() throws Exception {
        when(testCycleService.getByProjectAndNumber(PROJECT_KEY, TEST_CYCLE_NUMBER)).thenReturn(Optional.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cycles/{number}", PROJECT_KEY, TEST_CYCLE_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.testCycleNumber").value(TEST_CYCLE_NUMBER));
    }

    @Test
    void getByNumber_ShouldReturn404_WhenCycleDoesNotExist() throws Exception {
        when(testCycleService.getByProjectAndNumber(PROJECT_KEY, TEST_CYCLE_NUMBER)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cycles/{number}", PROJECT_KEY, TEST_CYCLE_NUMBER))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturnListOfCycles() throws Exception {
        when(testCycleService.getAllByProject(PROJECT_KEY)).thenReturn(List.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/test-cycles", PROJECT_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void update_ShouldReturn200_WhenCycleExists() throws Exception {
        CreateTestCycleRequest request = new CreateTestCycleRequest();
        request.setName("Updated Release");

        TestCycleResponse updatedResponse = new TestCycleResponse(
                PROJECT_KEY, TEST_CYCLE_NUMBER, "Updated Release", null, null, null, Instant.now(), Instant.now()
        );

        when(testCycleService.update(eq(PROJECT_KEY), eq(TEST_CYCLE_NUMBER), any(CreateTestCycleRequest.class)))
                .thenReturn(Optional.of(updatedResponse));

        mockMvc.perform(put("/api/v1/projects/{projectKey}/test-cycles/{number}", PROJECT_KEY, TEST_CYCLE_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Release"));
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
}