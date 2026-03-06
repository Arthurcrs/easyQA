package com.arthur.easy_qa.service;

import com.arthur.easy_qa.repository.TestCycleRepository;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.Mockito.mock;

public class TestCycleServiceTest {

    private TestCycleRepository repository;
    private TestCycleService service;

    @BeforeEach
    void setup() {
        repository = mock(TestCycleRepository.class);
        service = new TestCycleService(repository);
    }
}
