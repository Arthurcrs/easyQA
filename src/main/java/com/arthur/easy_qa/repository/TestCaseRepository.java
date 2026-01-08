package com.arthur.easy_qa.repository;

import com.arthur.easy_qa.domain.TestCase;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestCaseRepository {

    TestCase save(TestCase testCase);

    Optional<TestCase> findById(UUID id);

    List<TestCase> findAll();

    boolean deleteById(UUID id);
}
