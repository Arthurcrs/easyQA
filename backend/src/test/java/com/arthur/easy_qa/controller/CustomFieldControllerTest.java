package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.customfield.CustomFieldType;
import com.arthur.easy_qa.dto.customfield.*;
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
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private CustomFieldService service;

    private CustomFieldResponse defaultResponse;
    private final String PROJECT_KEY = "EASYQA";
    private final Long FIELD_NUMBER = 1L;

    @BeforeEach
    void setUp() {
        CustomFieldOptionResponse option = new CustomFieldOptionResponse(UUID.randomUUID(), "Chrome", true, 0);

        defaultResponse = new CustomFieldResponse(
                PROJECT_KEY,
                FIELD_NUMBER,
                "Browser",
                CustomFieldType.DROPDOWN,
                List.of(option)
        );
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        CreateCustomFieldRequest request = new CreateCustomFieldRequest();
        request.setName("Browser");
        request.setType(CustomFieldType.DROPDOWN);
        request.setOptions(List.of("Chrome"));

        when(service.create(eq(PROJECT_KEY), any(CreateCustomFieldRequest.class)))
                .thenReturn(defaultResponse);

        mockMvc.perform(post("/api/v1/projects/{projectKey}/custom-fields", PROJECT_KEY)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Browser"))
                .andExpect(jsonPath("$.options[0].value").value("Chrome"));
    }

    @Test
    void getByNumber_ShouldReturn200() throws Exception {
        when(service.getByProjectAndNumber(PROJECT_KEY, FIELD_NUMBER))
                .thenReturn(Optional.of(defaultResponse));

        mockMvc.perform(get("/api/v1/projects/{projectKey}/custom-fields/{number}", PROJECT_KEY, FIELD_NUMBER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Browser"))
                .andExpect(jsonPath("$.options").isArray());
    }

    @Test
    void update_ShouldReturn200() throws Exception {
        CreateCustomFieldRequest request = new CreateCustomFieldRequest();
        request.setName("Updated Browser");

        CustomFieldResponse updatedResponse = new CustomFieldResponse(
                PROJECT_KEY, FIELD_NUMBER, "Updated Browser", CustomFieldType.DROPDOWN, defaultResponse.getOptions()
        );

        when(service.update(eq(PROJECT_KEY), eq(FIELD_NUMBER), any(CreateCustomFieldRequest.class)))
                .thenReturn(Optional.of(updatedResponse));

        mockMvc.perform(patch("/api/v1/projects/{projectKey}/custom-fields/{number}", PROJECT_KEY, FIELD_NUMBER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Browser"));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        when(service.delete(PROJECT_KEY, FIELD_NUMBER)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/projects/{projectKey}/custom-fields/{number}", PROJECT_KEY, FIELD_NUMBER))
                .andExpect(status().isNoContent());
    }
}