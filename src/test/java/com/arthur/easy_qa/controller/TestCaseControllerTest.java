package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.TestCasePriority;
import com.arthur.easy_qa.domain.TestCaseStatus;
import com.arthur.easy_qa.domain.TestCaseType;
import com.arthur.easy_qa.dto.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.TestCaseResponse;
import com.arthur.easy_qa.service.TestCaseService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TestCaseController.class)
class TestCaseControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TestCaseService service;

    @Test
    void create_validRequest_shouldCallServiceAndReturnCreatedResponse() throws Exception {
        //Arrange
        UUID uuid = UUID.randomUUID();

        String requestJson = """
                {
                  "status": "DRAFT",
                  "us": "US-1",
                  "feature": "Feature A",
                  "scenario": "Scenario A",
                  "description": "Description A",
                  "priority": "MEDIUM",
                  "type": "FUNCTIONAL"
                }
                """;

        TestCaseResponse serviceResponse = new TestCaseResponse(
                uuid,
                "US-1",
                TestCaseStatus.DRAFT,
                "Feature A",
                "Scenario A",
                "Description A",
                TestCasePriority.MEDIUM,
                TestCaseType.FUNCTIONAL,
                Instant.parse("2026-02-03T10:00:00Z"),
                Instant.parse("2026-02-03T10:00:00Z")
        );

        when(service.create(any(CreateTestCaseRequest.class))).thenReturn(serviceResponse);

        //Act
        mockMvc.perform(post("/api/v1/test-cases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/test-cases/" + uuid))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(uuid.toString()))
                .andExpect(jsonPath("$.us").value("US-1"))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        //Assert
        ArgumentCaptor<CreateTestCaseRequest> captor =
                ArgumentCaptor.forClass(CreateTestCaseRequest.class);

        verify(service, times(1)).create(captor.capture());

        CreateTestCaseRequest serviceRequest = captor.getValue();
        assertEquals("US-1", serviceRequest.getUs());
        assertEquals(TestCaseStatus.DRAFT, serviceRequest.getStatus());
        assertEquals("Feature A", serviceRequest.getFeature());
        assertEquals("Scenario A", serviceRequest.getScenario());
        assertEquals("Description A", serviceRequest.getDescription());
        assertEquals(TestCasePriority.MEDIUM, serviceRequest.getPriority());
        assertEquals(TestCaseType.FUNCTIONAL, serviceRequest.getType());

        verifyNoMoreInteractions(service);
    }
}
