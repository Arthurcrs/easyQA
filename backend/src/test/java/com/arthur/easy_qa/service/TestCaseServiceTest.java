package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.*;
import com.arthur.easy_qa.dto.testcase.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.testcase.TestCaseResponse;
import com.arthur.easy_qa.repository.customfield.CustomFieldRepository;
import com.arthur.easy_qa.repository.customfield.TestCaseFieldValueRepository;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import com.arthur.easy_qa.repository.testcase.TestCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestCaseServiceTest {

    private TestCaseRepository testCaseRepository;
    private ProjectRepository projectRepository;
    private CustomFieldRepository customFieldRepository;
    private TestCaseFieldValueRepository fieldValueRepository;

    private TestCaseService service;

    private final String PROJECT_KEY = "EASYQA";
    private Project project;
    private TestCase defaultTestCase;
    private CustomField browserField;

    @BeforeEach
    void setup() {
        testCaseRepository = mock(TestCaseRepository.class);
        projectRepository = mock(ProjectRepository.class);
        customFieldRepository = mock(CustomFieldRepository.class);
        fieldValueRepository = mock(TestCaseFieldValueRepository.class);

        service = new TestCaseService(
                testCaseRepository,
                projectRepository,
                customFieldRepository,
                fieldValueRepository
        );

        project = new Project("EasyQA", PROJECT_KEY, Instant.now(), false);

        defaultTestCase = new TestCase(
                project, 1L, "US-100", TestCaseStatus.DRAFT,
                "Authentication", "Successful Login", "User logs in with valid credentials",
                TestCasePriority.HIGH, TestCaseType.FUNCTIONAL
        );

        browserField = new CustomField(project, 10L, "Browser", CustomFieldType.DROPDOWN, "Chrome,Firefox");
    }

    @Test
    void create_validRequest_shouldSaveTestCaseAndCustomFields() {
        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setUs("US-100");
        request.setFeature("Authentication");
        request.setScenario("Successful Login");
        request.setDescription("User logs in with valid credentials");
        request.setStatus(TestCaseStatus.DRAFT);
        request.setPriority(TestCasePriority.HIGH);
        request.setType(TestCaseType.FUNCTIONAL);
        request.setCustomFields(Map.of(10L, "Chrome"));

        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(project));
        when(testCaseRepository.findMaxTestCaseNumberByProjectKey(PROJECT_KEY)).thenReturn(Optional.of(0L));
        when(testCaseRepository.save(any(TestCase.class))).thenAnswer(i -> i.getArgument(0));

        when(customFieldRepository.findByProjectKeyAndFieldNumber(PROJECT_KEY, 10L))
                .thenReturn(Optional.of(browserField));
        when(fieldValueRepository.findByTestCaseAndCustomField(any(), eq(browserField)))
                .thenReturn(Optional.empty());

        TestCaseResponse response = service.create(PROJECT_KEY, request);

        assertNotNull(response);
        assertEquals(1L, response.getTestCaseNumber());
        assertEquals("Successful Login", response.getScenario());

        verify(testCaseRepository, times(1)).save(any(TestCase.class));

        ArgumentCaptor<TestCaseFieldValue> fieldCaptor = ArgumentCaptor.forClass(TestCaseFieldValue.class);
        verify(fieldValueRepository, times(1)).save(fieldCaptor.capture());

        assertEquals("Chrome", fieldCaptor.getValue().getValue());
    }

    @Test
    void getByProjectAndNumber_shouldReturnTestCaseWithCustomFieldsMapped() {
        when(testCaseRepository.findByProjectKeyAndTestCaseNumber(PROJECT_KEY, 1L))
                .thenReturn(Optional.of(defaultTestCase));

        TestCaseFieldValue fieldValue = new TestCaseFieldValue(defaultTestCase, browserField, "Firefox");
        when(fieldValueRepository.findAllByTestCase(defaultTestCase)).thenReturn(List.of(fieldValue));

        Optional<TestCaseResponse> response = service.getByProjectAndNumber(PROJECT_KEY, 1L);

        assertTrue(response.isPresent());
        assertEquals("Successful Login", response.get().getScenario());

        Map<String, String> customFieldsMap = response.get().getCustomFields();
        assertNotNull(customFieldsMap);
        assertEquals(1, customFieldsMap.size());
        assertEquals("Firefox", customFieldsMap.get("Browser"));
    }

    @Test
    void update_shouldUpdateExistingCustomFieldValues() {
        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setUs("US-100");
        request.setFeature("Authentication");
        request.setScenario("Updated Scenario");
        request.setDescription("Desc");
        request.setStatus(TestCaseStatus.DRAFT);
        request.setPriority(TestCasePriority.HIGH);
        request.setType(TestCaseType.FUNCTIONAL);
        request.setCustomFields(Map.of(10L, "Safari"));

        when(testCaseRepository.findByProjectKeyAndTestCaseNumber(PROJECT_KEY, 1L))
                .thenReturn(Optional.of(defaultTestCase));
        when(testCaseRepository.save(any(TestCase.class))).thenAnswer(i -> i.getArgument(0));

        when(customFieldRepository.findByProjectKeyAndFieldNumber(PROJECT_KEY, 10L))
                .thenReturn(Optional.of(browserField));

        TestCaseFieldValue existingValue = new TestCaseFieldValue(defaultTestCase, browserField, "Chrome");
        when(fieldValueRepository.findByTestCaseAndCustomField(defaultTestCase, browserField))
                .thenReturn(Optional.of(existingValue));

        Optional<TestCaseResponse> response = service.update(PROJECT_KEY, 1L, request);

        assertTrue(response.isPresent());
        assertEquals("Updated Scenario", response.get().getScenario());

        ArgumentCaptor<TestCaseFieldValue> fieldCaptor = ArgumentCaptor.forClass(TestCaseFieldValue.class);
        verify(fieldValueRepository, times(1)).save(fieldCaptor.capture());

        assertEquals("Safari", fieldCaptor.getValue().getValue());
    }

    @Test
    void delete_shouldReturnRepositoryResult() {
        when(testCaseRepository.deleteByProjectKeyAndTestCaseNumber(PROJECT_KEY, 1L)).thenReturn(true);
        assertTrue(service.delete(PROJECT_KEY, 1L));
        verify(testCaseRepository).deleteByProjectKeyAndTestCaseNumber(PROJECT_KEY, 1L);
    }
}