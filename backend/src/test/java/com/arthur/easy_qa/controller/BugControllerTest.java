package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.BugSeverity;
import com.arthur.easy_qa.domain.BugStatus;
import com.arthur.easy_qa.dto.bug.BugDetailsResponse;
import com.arthur.easy_qa.dto.bug.BugResponse;
import com.arthur.easy_qa.dto.bug.CreateBugRequest;
import com.arthur.easy_qa.service.BugService;
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

@WebMvcTest(BugController.class)
class BugControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BugService bugService;

    private BugResponse defaultResponse;
    private final String PROJECT_KEY = "EASYQA";
    private final Long BUG_NUMBER = 1L;

    @BeforeEach
    void setUp() {
        defaultResponse = new BugResponse(
                PROJECT_KEY, BUG_NUMBER, "Login crashes", "Description",
                BugStatus.OPEN, BugSeverity.HIGH, Instant.now(), null
        );
    }

    @Test
    void create_ShouldReturn201AndBugResponse() throws Exception {
        CreateBugRequest request = new CreateBugRequest();
        request.setTitle("Login crashes");
        request.setSeverity(BugSeverity.HIGH);

        when(bugService.create(eq(PROJECT_KEY), any(CreateBugRequest.class))).thenReturn(defaultResponse);

        mockMvc.perform(post("/api/v1/projects/{projectKey}/bugs", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/projects/" + PROJECT_KEY + "/bugs/" + BUG_NUMBER))
                .andExpect(jsonPath("$.bugNumber").value(BUG_NUMBER))
                .andExpect(jsonPath("$.title").value("Login crashes"));
    }

    @Test
    void create_ShouldReturn400_WhenTitleIsBlank() throws Exception {
        CreateBugRequest request = new CreateBugRequest();
        request.setSeverity(BugSeverity.HIGH);

        mockMvc.perform(post("/api/v1/projects/{projectKey}/bugs", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByNumber_ShouldReturn200_WhenBugExists() throws Exception {
        BugDetailsResponse detailsResponse = new BugDetailsResponse(
                PROJECT_KEY,
                BUG_NUMBER,
                "Login crashes",
                defaultResponse.getDescription(),
                defaultResponse.getStatus(),
                defaultResponse.getSeverity(),
                defaultResponse.getOpenDate(),
                defaultResponse.getCloseDate(),
                java.util.List.of()
        );

        when(bugService.getByProjectAndNumber(PROJECT_KEY, BUG_NUMBER))
                .thenReturn(Optional.of(detailsResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/bugs/{bugNumber}", PROJECT_KEY, BUG_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Login crashes"))
                .andExpect(jsonPath("$.linkedExecutions").isArray()); // Verify the new array is present
    }

    @Test
    void getByNumber_ShouldReturn404_WhenBugDoesNotExist() throws Exception {
        when(bugService.getByProjectAndNumber(PROJECT_KEY, BUG_NUMBER)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/projects/{projectKey}/bugs/{bugNumber}", PROJECT_KEY, BUG_NUMBER))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturn200AndHandleQueryParams() throws Exception {
        when(bugService.getAllByProject(PROJECT_KEY, BugStatus.OPEN, BugSeverity.HIGH))
                .thenReturn(List.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/bugs", PROJECT_KEY)
                        .param("status", "OPEN")
                        .param("severity", "HIGH"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(bugService).getAllByProject(PROJECT_KEY, BugStatus.OPEN, BugSeverity.HIGH);
    }

    @Test
    void update_ShouldReturn200_WhenBugExists() throws Exception {
        CreateBugRequest request = new CreateBugRequest();
        request.setTitle("Updated Title");
        request.setSeverity(BugSeverity.CRITICAL);

        BugResponse updatedResponse = new BugResponse(
                PROJECT_KEY, BUG_NUMBER, "Updated Title", "Description",
                BugStatus.OPEN, BugSeverity.CRITICAL, Instant.now(), null
        );

        when(bugService.update(eq(PROJECT_KEY), eq(BUG_NUMBER), any(CreateBugRequest.class)))
                .thenReturn(Optional.of(updatedResponse));

        mockMvc.perform(patch("/api/v1/projects/{projectKey}/bugs/{bugNumber}", PROJECT_KEY, BUG_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void delete_ShouldReturn204_WhenBugExists() throws Exception {
        when(bugService.delete(PROJECT_KEY, BUG_NUMBER)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/bugs/{bugNumber}", PROJECT_KEY, BUG_NUMBER))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenBugDoesNotExist() throws Exception {
        when(bugService.delete(PROJECT_KEY, BUG_NUMBER)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/bugs/{bugNumber}", PROJECT_KEY, BUG_NUMBER))
                .andExpect(status().isNotFound());
    }
}