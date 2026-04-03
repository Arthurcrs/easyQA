package com.arthur.easy_qa.repository.testcase;

import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.domain.TestCasePriority;
import com.arthur.easy_qa.domain.TestCaseStatus;
import com.arthur.easy_qa.domain.TestCaseType;

import java.util.List;
import java.util.Optional;

public interface TestCaseRepository {

    TestCase save(TestCase testCase);

    Optional<TestCase> findByProjectKeyAndTestCaseNumber(String projectKey, Long testCaseNumber);

    List<TestCase> findAllByProjectKey(String projectKey);

    List<TestCase> findAllByProjectKeyAndFilters(String projectKey, TestCaseStatus status, TestCaseType type, TestCasePriority priority, String q);

    boolean deleteByProjectKeyAndTestCaseNumber(String projectKey, Long testCaseNumber);

    Optional<Long> findMaxTestCaseNumberByProjectKey(String projectKey);
}