package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.dto.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.TestCycleResponse;
import com.arthur.easy_qa.service.TestCaseService;
import com.arthur.easy_qa.service.TestCycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TestCycleController.class)
public class TestCycleControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TestCycleService service;

    @Test
    void create_validRequest_shouldCallServiceAndReturnResponse() throws Exception {

        String requestJson = """
                {
                  "name": "Test Cycle Name",
                  "version": "1.0.4",
                  "environment": "production",
                  "type": "US validation"
                }
                """;

        UUID newTestCycleId = UUID.randomUUID();

        TestCycleResponse response = new TestCycleResponse(
                newTestCycleId,
                "Test Cycle Name",
                "1.0.4",
                "production",
                "US validation"
        );

        when(service.create(any(CreateTestCycleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/test-cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/test-cycles/" + newTestCycleId))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(newTestCycleId.toString()))
                .andExpect(jsonPath("$.name").value("Test Cycle Name"))
                .andExpect(jsonPath("$.version").value("1.0.4"))
                .andExpect(jsonPath("$.environment").value("production"))
                .andExpect(jsonPath("$.type").value("US validation"));

        verify(service, times(1)).create(any(CreateTestCycleRequest.class));
    }

    @Test
    void create_missingName_shouldNotCallServiceAndReturnBadRequest() throws Exception {

        String requestJson = """
                {
                  "version": "1.0.4",
                  "environment": "production",
                  "type": "US validation"
                }
                """;

        mockMvc.perform(post("/api/v1/test-cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void create_invalidName_shouldNotCallServiceAndReturnBadRequest() throws Exception {

        String requestJson = """
                {
                  "name": "   ";
                  "version": "1.0.4",
                  "environment": "production",
                  "type": "US validation"
                }
                """;

        mockMvc.perform(post("/api/v1/test-cycles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}
