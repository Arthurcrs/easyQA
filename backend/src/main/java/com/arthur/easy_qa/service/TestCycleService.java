package com.arthur.easy_qa.service;

import com.arthur.easy_qa.dto.testcycle.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.testcycle.TestCycleResponse;
import com.arthur.easy_qa.repository.testcycle.TestCycleRepository;
import org.springframework.stereotype.Service;

@Service
public class TestCycleService {

    private final TestCycleRepository repository;

    public TestCycleService(TestCycleRepository repository) {
        this.repository = repository;
    }

    public TestCycleResponse create(CreateTestCycleRequest request) {
        return null;
    }

}
