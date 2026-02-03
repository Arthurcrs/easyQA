package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.domain.TestCasePriority;
import com.arthur.easy_qa.domain.TestCaseStatus;
import com.arthur.easy_qa.domain.TestCaseType;
import com.arthur.easy_qa.dto.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.TestCaseResponse;
import com.arthur.easy_qa.repository.TestCaseRepository;
import com.sun.source.tree.AssertTree;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestCaseServiceTest {

    private TestCaseRepository repository;
    private TestCaseService service;

    @BeforeEach
    void setup() {
        repository = mock(TestCaseRepository.class);
        service = new TestCaseService(repository);
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

        when(repository.save(any(TestCase.class))).thenAnswer(invocation -> {
            TestCase tc = invocation.getArgument(0);
            simulateJpaPrePersist(tc); // simulate id + @PrePersist
            return tc;
        });

        // Act
        TestCaseResponse response = service.create(request);

        // Assert: repository interaction + captured saved entity
        ArgumentCaptor<TestCase> captor = ArgumentCaptor.forClass(TestCase.class);
        verify(repository, times(1)).save(captor.capture());
        TestCase saved = captor.getValue();

        // Assert: entity built from request
        assertNotNull(saved);
        assertNotNull(saved.getId());
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

        // Assert: response mapped from saved entity
        assertNotNull(response);
        assertEquals(saved.getId(), response.getId());
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
        UUID uuid = UUID.randomUUID();
        when(repository.deleteById(uuid)).thenReturn(true);

        //Act
        boolean result = service.delete(uuid);

        //Assert
        verify(repository, times(1)).deleteById(uuid);
        assertTrue(result);
    }

    @Test
    void getById_notFound_shouldReturnEmpty() {
        //Arrange
        UUID uuid = UUID.randomUUID();
        when(repository.findById(uuid)).thenReturn(Optional.empty());

        //Act
        Optional<TestCaseResponse> result = service.getById(uuid);

        //Assert
        verify(repository, times(1)).findById(uuid);
        assertTrue(result.isEmpty());
    }

    @Test
    void getById_found_shouldReturnResponse() {
        //Arrange
        UUID uuid = UUID.randomUUID();

        TestCase testCase = new TestCase(
                "US name",
                TestCaseStatus.FINISHED,
                "Feature name",
                "Scenario",
                "Description",
                TestCasePriority.HIGH,
                TestCaseType.FUNCTIONAL
        );

        when(repository.findById(uuid)).thenReturn(Optional.of(testCase));

        //Act
        Optional<TestCaseResponse> response = service.getById(uuid);

        //Assert
        verify(repository, times(1)).findById(uuid);
        assertTrue(response.isPresent());
        TestCaseResponse dto = response.get();

        assertEquals("US name", dto.getUs());
        assertEquals(TestCaseStatus.FINISHED, dto.getStatus());
        assertEquals("Feature name", dto.getFeature());
        assertEquals("Scenario", dto.getScenario());
        assertEquals("Description", dto.getDescription());
    }

    @Test
    void getAll_shouldReturnMappedList() {
        //Arrange

        TestCase testCase1 = new TestCase(
                "US name 1",
                TestCaseStatus.FINISHED,
                "Feature name 1",
                "Scenario 1",
                "Description 1",
                TestCasePriority.LOW,
                TestCaseType.UI
        );

        TestCase testCase2 = new TestCase(
                "US name 2",
                TestCaseStatus.DRAFT,
                "Feature name 2",
                "Scenario 2",
                "Description 2",
                TestCasePriority.HIGH,
                TestCaseType.FUNCTIONAL
        );

        when(repository.findAll()).thenReturn(List.of(testCase1, testCase2));

        //Act

        List<TestCaseResponse> result = service.getAll();

        //Assert

        assertEquals(2, result.size());

        verify(repository, times(1)).findAll();

        assertEquals("US name 1", result.get(0).getUs());
        assertEquals(TestCaseStatus.FINISHED, result.get(0).getStatus());
        assertEquals("Feature name 1", result.get(0).getFeature());
        assertEquals("Scenario 1", result.get(0).getScenario());
        assertEquals("Description 1", result.get(0).getDescription());
        assertEquals(TestCasePriority.LOW, result.get(0).getPriority());
        assertEquals(TestCaseType.UI, result.get(0).getType());

        assertEquals("US name 2", result.get(1).getUs());
        assertEquals(TestCaseStatus.DRAFT, result.get(1).getStatus());
        assertEquals("Feature name 2", result.get(1).getFeature());
        assertEquals("Scenario 2", result.get(1).getScenario());
        assertEquals("Description 2", result.get(1).getDescription());
        assertEquals(TestCasePriority.HIGH, result.get(1).getPriority());
        assertEquals(TestCaseType.FUNCTIONAL, result.get(1).getType());
    }

    @Test
    void update_notFound_shouldReturnEmpty() {
        //Arrange

        UUID uuid = UUID.randomUUID();

        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setUs("US Name");
        request.setStatus(TestCaseStatus.FINISHED);
        request.setFeature("Feature name");
        request.setScenario("Scenario");
        request.setPriority(TestCasePriority.LOW);
        request.setType(TestCaseType.UI);

        when(repository.findById(uuid)).thenReturn(Optional.empty());

        //Act
        Optional<TestCaseResponse> response = service.update(uuid, request);

        //Assert
        verify(repository, times(1)).findById(uuid);
        assertTrue(response.isEmpty());

    }

    @Test
    void update_found_shouldUpdateSaveAndReturnResponse() {
        //Arrange
        UUID uuid = UUID.randomUUID();

        CreateTestCaseRequest request = new CreateTestCaseRequest();
        request.setUs("Updated US Name");
        request.setStatus(TestCaseStatus.FINISHED);
        request.setFeature("Updated Feature name");
        request.setScenario("Updated Scenario");
        request.setDescription("Updated Description");
        request.setPriority(TestCasePriority.LOW);
        request.setType(TestCaseType.UI);

        TestCase existingTestCase = new TestCase(
                "Old US Name",
                TestCaseStatus.DRAFT,
                "Old Feature Name",
                "Old Scneario",
                "Old Description",
                TestCasePriority.MEDIUM,
                TestCaseType.FUNCTIONAL
        );

        when(repository.findById(uuid)).thenReturn(Optional.of(existingTestCase));
        when(repository.save(any(TestCase.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //Act

        Optional<TestCaseResponse> response = service.update(uuid, request);

        //Assert
        ArgumentCaptor<TestCase> captor = ArgumentCaptor.forClass(TestCase.class);
        TestCase updatedTestCase = captor.getValue();

        verify(repository, times(1)).findById(uuid);

        assertTrue(response.isPresent());
        assertEquals(response.get().getId(), updatedTestCase.getId());
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
