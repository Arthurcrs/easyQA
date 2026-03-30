package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.domain.TestCasePriority;
import com.arthur.easy_qa.domain.TestCaseStatus;
import com.arthur.easy_qa.domain.TestCaseType;
import com.arthur.easy_qa.dto.testcase.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.testcase.TestCaseResponse;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import com.arthur.easy_qa.repository.testcase.TestCaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestCaseServiceTest {

    private TestCaseRepository testCaseRepository;
    private ProjectRepository projectRepository;
    private TestCaseService service;

    private final String PROJECT_KEY = "EASYQA";
    private Project project;

    @BeforeEach
    void setup() {
        testCaseRepository = mock(TestCaseRepository.class);
        projectRepository = mock(ProjectRepository.class);
        service = new TestCaseService(testCaseRepository, projectRepository);

        // Fixed: Added Instant.now() and false to match the constructor
        project = new Project("EasyQA", PROJECT_KEY, Instant.now(), false);
    }

    @Test
    void create_validRequest_shouldSaveAndReturnResponse() {

        // Arrange
        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setUs("User Story Name");
        request.setStatus(TestCaseStatus.DRAFT);
        request.setFeature("Login");
        request.setScenario("Login com email");
        request.setDescription("Dado.. Quando.. Então");
        request.setPriority(TestCasePriority.HIGH);
        request.setType(TestCaseType.FUNCTIONAL);

        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.of(project));
        when(testCaseRepository.findMaxTestCaseNumberByProjectKey(PROJECT_KEY)).thenReturn(Optional.of(5L)); // Next should be 6

        when(testCaseRepository.save(any(TestCase.class))).thenAnswer(invocation -> {
            TestCase tc = invocation.getArgument(0);
            simulateJpaPrePersist(tc);
            return tc;
        });

        // Act
        TestCaseResponse response = service.create(PROJECT_KEY, request);

        // Assert
        ArgumentCaptor<TestCase> captor = ArgumentCaptor.forClass(TestCase.class);
        verify(testCaseRepository, times(1)).save(captor.capture());
        TestCase saved = captor.getValue();

        assertNotNull(saved);
        assertEquals(project, saved.getProject());
        assertEquals(6L, saved.getTestCaseNumber());
        assertEquals("User Story Name", saved.getUs());
        assertEquals(TestCaseStatus.DRAFT, saved.getStatus());
        assertEquals("Login", saved.getFeature());
        assertEquals("Login com email", saved.getScenario());
        assertEquals("Dado.. Quando.. Então", saved.getDescription());
        assertEquals(TestCasePriority.HIGH, saved.getPriority());
        assertEquals(TestCaseType.FUNCTIONAL, saved.getType());

        assertNotNull(saved.getCreationInstant());
        assertNotNull(saved.getLastUpdateInstant());
        assertEquals(saved.getCreationInstant(), saved.getLastUpdateInstant());

        assertNotNull(response);
        assertEquals(PROJECT_KEY, response.getProjectKey());
        assertEquals(6L, response.getTestCaseNumber());
        assertEquals(saved.getUs(), response.getUs());
        assertEquals(saved.getStatus(), response.getStatus());
        assertEquals(saved.getFeature(), response.getFeature());
        assertEquals(saved.getScenario(), response.getScenario());
        assertEquals(saved.getDescription(), response.getDescription());
        assertEquals(saved.getPriority(), response.getPriority());
        assertEquals(saved.getType(), response.getType());
        assertEquals(saved.getCreationInstant(), response.getCreationInstant());
        assertEquals(saved.getLastUpdateInstant(), response.getLastUpdateInstant());
    }

    @Test
    void create_projectNotFound_shouldThrowException() {
        // Arrange
        CreateTestCaseRequest request = new CreateTestCaseRequest();
        when(projectRepository.findByKey(PROJECT_KEY)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                service.create(PROJECT_KEY, request));

        assertEquals("Project not found: " + PROJECT_KEY, exception.getMessage());
        verify(testCaseRepository, never()).save(any());
    }

    private static void simulateJpaPrePersist(TestCase testCase) {
        if (testCase.getId() == null) {
            setPrivateField(testCase, "id", UUID.randomUUID());
        }
        invokeNoArgMethod(testCase, "onCreate");
    }

    private static void invokeNoArgMethod(Object target, String methodName) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName);
            method.setAccessible(true);
            method.invoke(target);
        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke method: " + methodName, e);
        }
    }

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }

    @Test
    void delete_shouldReturnRepositoryResult() {

        //Arrange
        Long testCaseNumber = 1L;
        when(testCaseRepository.deleteByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber)).thenReturn(true);

        //Act
        boolean result = service.delete(PROJECT_KEY, testCaseNumber);

        //Assert
        verify(testCaseRepository, times(1)).deleteByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber);
        assertTrue(result);
    }

    @Test
    void getByProjectAndNumber_notFound_shouldReturnEmpty() {

        //Arrange
        Long testCaseNumber = 1L;
        when(testCaseRepository.findByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber)).thenReturn(Optional.empty());

        //Act
        Optional<TestCaseResponse> result = service.getByProjectAndNumber(PROJECT_KEY, testCaseNumber);

        //Assert
        verify(testCaseRepository, times(1)).findByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber);
        assertTrue(result.isEmpty());
    }

    @Test
    void getByProjectAndNumber_found_shouldReturnResponse() {

        //Arrange
        Long testCaseNumber = 1L;
        TestCase testCase = new TestCase(
                project,
                testCaseNumber,
                "US name",
                TestCaseStatus.FINISHED,
                "Feature name",
                "Scenario",
                "Description",
                TestCasePriority.HIGH,
                TestCaseType.FUNCTIONAL
        );

        when(testCaseRepository.findByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber)).thenReturn(Optional.of(testCase));

        //Act
        Optional<TestCaseResponse> response = service.getByProjectAndNumber(PROJECT_KEY, testCaseNumber);

        //Assert
        verify(testCaseRepository, times(1)).findByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber);
        assertTrue(response.isPresent());
        TestCaseResponse dto = response.get();

        assertEquals(PROJECT_KEY, dto.getProjectKey());
        assertEquals(testCaseNumber, dto.getTestCaseNumber());
        assertEquals("US name", dto.getUs());
        assertEquals(TestCaseStatus.FINISHED, dto.getStatus());
        assertEquals("Feature name", dto.getFeature());
        assertEquals("Scenario", dto.getScenario());
        assertEquals("Description", dto.getDescription());
    }

    @Test
    void getAllByProject_shouldReturnMappedList() {

        //Arrange
        TestCase testCase1 = new TestCase(
                project,
                1L,
                "US name 1",
                TestCaseStatus.FINISHED,
                "Feature name 1",
                "Scenario 1",
                "Description 1",
                TestCasePriority.LOW,
                TestCaseType.UI
        );

        TestCase testCase2 = new TestCase(
                project,
                2L,
                "US name 2",
                TestCaseStatus.DRAFT,
                "Feature name 2",
                "Scenario 2",
                "Description 2",
                TestCasePriority.HIGH,
                TestCaseType.FUNCTIONAL
        );

        when(testCaseRepository.findAllByProjectKey(PROJECT_KEY)).thenReturn(List.of(testCase1, testCase2));

        //Act
        List<TestCaseResponse> result = service.getAllByProject(PROJECT_KEY);

        //Assert
        assertEquals(2, result.size());
        verify(testCaseRepository, times(1)).findAllByProjectKey(PROJECT_KEY);

        assertEquals(PROJECT_KEY, result.get(0).getProjectKey());
        assertEquals(1L, result.get(0).getTestCaseNumber());
        assertEquals("US name 1", result.get(0).getUs());
        assertEquals(TestCaseStatus.FINISHED, result.get(0).getStatus());
        assertEquals("Feature name 1", result.get(0).getFeature());

        assertEquals(PROJECT_KEY, result.get(1).getProjectKey());
        assertEquals(2L, result.get(1).getTestCaseNumber());
        assertEquals("US name 2", result.get(1).getUs());
        assertEquals(TestCaseStatus.DRAFT, result.get(1).getStatus());
        assertEquals(TestCasePriority.HIGH, result.get(1).getPriority());
    }

    @Test
    void update_notFound_shouldReturnEmpty() {

        //Arrange
        Long testCaseNumber = 1L;
        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setUs("US Name");
        request.setStatus(TestCaseStatus.FINISHED);
        request.setFeature("Feature name");
        request.setScenario("Scenario");
        request.setPriority(TestCasePriority.LOW);
        request.setType(TestCaseType.UI);

        when(testCaseRepository.findByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber)).thenReturn(Optional.empty());

        //Act
        Optional<TestCaseResponse> response = service.update(PROJECT_KEY, testCaseNumber, request);

        //Assert
        verify(testCaseRepository, times(1)).findByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber);
        assertTrue(response.isEmpty());
    }

    @Test
    void update_found_shouldUpdateSaveAndReturnResponse() {

        //Arrange
        Long testCaseNumber = 1L;
        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setUs("Updated US Name");
        request.setStatus(TestCaseStatus.FINISHED);
        request.setFeature("Updated Feature name");
        request.setScenario("Updated Scenario");
        request.setDescription("Updated Description");
        request.setPriority(TestCasePriority.LOW);
        request.setType(TestCaseType.UI);

        TestCase existingTestCase = new TestCase(
                project,
                testCaseNumber,
                "Old US Name",
                TestCaseStatus.DRAFT,
                "Old Feature Name",
                "Old Scneario",
                "Old Description",
                TestCasePriority.MEDIUM,
                TestCaseType.FUNCTIONAL
        );

        when(testCaseRepository.findByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber)).thenReturn(Optional.of(existingTestCase));
        when(testCaseRepository.save(any(TestCase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act
        Optional<TestCaseResponse> response = service.update(PROJECT_KEY, testCaseNumber, request);

        //Assert
        ArgumentCaptor<TestCase> captor = ArgumentCaptor.forClass(TestCase.class);

        verify(testCaseRepository, times(1)).findByProjectKeyAndTestCaseNumber(PROJECT_KEY, testCaseNumber);
        verify(testCaseRepository, times(1)).save(captor.capture());

        TestCase updatedTestCase = captor.getValue();

        assertTrue(response.isPresent());
        assertEquals(PROJECT_KEY, response.get().getProjectKey());
        assertEquals(testCaseNumber, response.get().getTestCaseNumber());
        assertEquals(response.get().getUs(), updatedTestCase.getUs());
        assertEquals(response.get().getStatus(), updatedTestCase.getStatus());
        assertEquals(response.get().getFeature(), updatedTestCase.getFeature());
        assertEquals(response.get().getScenario(), updatedTestCase.getScenario());
        assertEquals(response.get().getDescription(), updatedTestCase.getDescription());
        assertEquals(response.get().getPriority(), updatedTestCase.getPriority());
        assertEquals(response.get().getType(), updatedTestCase.getType());
        assertEquals(response.get().getCreationInstant(), updatedTestCase.getCreationInstant());
        assertEquals(response.get().getLastUpdateInstant(), updatedTestCase.getLastUpdateInstant());
    }
}