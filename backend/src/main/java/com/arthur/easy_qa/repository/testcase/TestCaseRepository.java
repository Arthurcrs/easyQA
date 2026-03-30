package com.arthur.easy_qa.repository.testcase;

import com.arthur.easy_qa.domain.TestCase;

import java.util.List;
import java.util.Optional;

public interface TestCaseRepository {

    TestCase save(TestCase testCase);

    Optional<TestCase> findByProjectKeyAndTestCaseNumber(String projectKey, Long testCaseNumber);

    List<TestCase> findAllByProjectKey(String projectKey);

    boolean deleteByProjectKeyAndTestCaseNumber(String projectKey, Long testCaseNumber);

    Optional<Long> findMaxTestCaseNumberByProjectKey(String projectKey);
}