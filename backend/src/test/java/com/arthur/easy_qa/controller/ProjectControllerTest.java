package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.dto.project.CreateProjectRequest;
import com.arthur.easy_qa.dto.project.ProjectResponse;
import com.arthur.easy_qa.dto.project.UpdateProjectRequest;
import com.arthur.easy_qa.service.ProjectService;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    private ProjectResponse defaultResponse;
    private final String PROJECT_KEY = "EASYQA";

    @BeforeEach
    void setUp() {
        defaultResponse = new ProjectResponse(
                UUID.randomUUID(),
                "EasyQA",
                PROJECT_KEY,
                Instant.now(),
                false
        );
    }

    @Test
    void create_ShouldReturn201AndProjectResponse() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("EasyQA");

        when(projectService.create(any(CreateProjectRequest.class))).thenReturn(defaultResponse);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/projects/" + PROJECT_KEY))
                .andExpect(jsonPath("$.name").value("EasyQA"))
                .andExpect(jsonPath("$.key").value(PROJECT_KEY))
                .andExpect(jsonPath("$.archived").value(false));
    }

    @Test
    void create_ShouldReturn400_WhenNameIsBlank() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("");

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_ShouldReturnListOfProjects() throws Exception {
        when(projectService.getAll(false)).thenReturn(List.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("EasyQA"));
    }

    @Test
    void getByKey_ShouldReturn200_WhenProjectExists() throws Exception {
        when(projectService.getByKey(PROJECT_KEY)).thenReturn(Optional.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}", PROJECT_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("EasyQA"));
    }

    @Test
    void getByKey_ShouldReturn404_WhenProjectDoesNotExist() throws Exception {
        when(projectService.getByKey(PROJECT_KEY)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/projects/{projectKey}", PROJECT_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateName_ShouldReturn200_WhenProjectExists() throws Exception {
        UpdateProjectRequest request = new UpdateProjectRequest();
        request.setName("New Name");

        ProjectResponse updatedResponse = new ProjectResponse(
                defaultResponse.getId(),
                "New Name",
                PROJECT_KEY,
                defaultResponse.getCreationDate(),
                false
        );

        when(projectService.updateName(eq(PROJECT_KEY), any(UpdateProjectRequest.class)))
                .thenReturn(Optional.of(updatedResponse));

        mockMvc.perform(patch("/api/v1/projects/{projectKey}", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }

    @Test
    void archive_ShouldReturn200_WhenProjectExists() throws Exception {
        ProjectResponse archivedResponse = new ProjectResponse(
                defaultResponse.getId(),
                defaultResponse.getName(),
                PROJECT_KEY,
                defaultResponse.getCreationDate(),
                true
        );

        when(projectService.archive(PROJECT_KEY)).thenReturn(Optional.of(archivedResponse));

        mockMvc.perform(post("/api/v1/projects/{projectKey}/archive", PROJECT_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(true));
    }

    @Test
    void restore_ShouldReturn200_WhenProjectExists() throws Exception {
        when(projectService.restore(PROJECT_KEY)).thenReturn(Optional.of(defaultResponse));

        mockMvc.perform(post("/api/v1/projects/{projectKey}/restore", PROJECT_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.archived").value(false));
    }

    @Test
    void delete_ShouldReturn204_WhenProjectExists() throws Exception {
        when(projectService.delete(PROJECT_KEY)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}", PROJECT_KEY))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenProjectDoesNotExist() throws Exception {
        when(projectService.delete(PROJECT_KEY)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}", PROJECT_KEY))
                .andExpect(status().isNotFound());
    }

    @Test
    void exceptionHandler_ShouldReturn400_OnIllegalArgumentException() throws Exception {
        CreateProjectRequest request = new CreateProjectRequest();
        request.setName("Duplicate Name");

        when(projectService.create(any(CreateProjectRequest.class)))
                .thenThrow(new IllegalArgumentException("Project name already exists"));

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Project name already exists"));
    }
}
