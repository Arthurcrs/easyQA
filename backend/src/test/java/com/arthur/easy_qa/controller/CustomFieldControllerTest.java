package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.CustomFieldType;
import com.arthur.easy_qa.dto.customfield.CreateCustomFieldRequest;
import com.arthur.easy_qa.dto.customfield.CustomFieldResponse;
import com.arthur.easy_qa.service.CustomFieldService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomFieldController.class)
class CustomFieldControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomFieldService customFieldService;

    private CustomFieldResponse defaultResponse;
    private final String PROJECT_KEY = "EASYQA";
    private final Long FIELD_NUMBER = 1L;

    @BeforeEach
    void setUp() {
        defaultResponse = new CustomFieldResponse(
                PROJECT_KEY, FIELD_NUMBER, "Browser", CustomFieldType.DROPDOWN, "Chrome,Firefox"
        );
    }

    @Test
    void create_ShouldReturn201AndCustomFieldResponse() throws Exception {
        CreateCustomFieldRequest request = new CreateCustomFieldRequest();
        request.setName("Browser");
        request.setType(CustomFieldType.DROPDOWN);
        request.setOptions("Chrome,Firefox");

        when(customFieldService.create(eq(PROJECT_KEY), any(CreateCustomFieldRequest.class)))
                .thenReturn(defaultResponse);

        mockMvc.perform(post("/api/v1/projects/{projectKey}/custom-fields", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/projects/" + PROJECT_KEY + "/custom-fields/" + FIELD_NUMBER))
                .andExpect(jsonPath("$.fieldNumber").value(FIELD_NUMBER))
                .andExpect(jsonPath("$.name").value("Browser"));
    }

    @Test
    void create_ShouldReturn400_WhenNameIsBlank() throws Exception {
        CreateCustomFieldRequest request = new CreateCustomFieldRequest();
        request.setType(CustomFieldType.TEXT);
        // Name is left null to trigger @NotBlank

        mockMvc.perform(post("/api/v1/projects/{projectKey}/custom-fields", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getByNumber_ShouldReturn200_WhenFieldExists() throws Exception {
        when(customFieldService.getByProjectAndNumber(PROJECT_KEY, FIELD_NUMBER))
                .thenReturn(Optional.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/custom-fields/{fieldNumber}", PROJECT_KEY, FIELD_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Browser"));
    }

    @Test
    void getByNumber_ShouldReturn404_WhenFieldDoesNotExist() throws Exception {
        when(customFieldService.getByProjectAndNumber(PROJECT_KEY, FIELD_NUMBER))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/projects/{projectKey}/custom-fields/{fieldNumber}", PROJECT_KEY, FIELD_NUMBER))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturnListOfFields() throws Exception {
        when(customFieldService.getAllByProject(PROJECT_KEY)).thenReturn(List.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/custom-fields", PROJECT_KEY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    void delete_ShouldReturn204_WhenFieldExists() throws Exception {
        when(customFieldService.delete(PROJECT_KEY, FIELD_NUMBER)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/custom-fields/{fieldNumber}", PROJECT_KEY, FIELD_NUMBER))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenFieldDoesNotExist() throws Exception {
        when(customFieldService.delete(PROJECT_KEY, FIELD_NUMBER)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/custom-fields/{fieldNumber}", PROJECT_KEY, FIELD_NUMBER))
                .andExpect(status().isNotFound());
    }
}