package com.arthur.easy_qa.service;

import com.arthur.easy_qa.dto.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.TestCycleResponse;
import com.arthur.easy_qa.repository.TestCycleRepository;
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
