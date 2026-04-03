package com.arthur.easy_qa.repository.execution;

import com.arthur.easy_qa.domain.Execution;
import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.domain.TestCycle;

import java.util.List;
import java.util.Optional;

public interface ExecutionRepository {

    Execution save(Execution execution);

    boolean existsByTestCycleAndTestCase(TestCycle testCycle, TestCase testCase);

    Optional<Long> findMaxExecutionNumberByProjectKey(String projectKey);

    void deleteByProjectKeyAndCycleNumberAndCaseNumber(String projectKey, Long testCycleNumber, Long testCaseNumber);

    List<Execution> findAllByTestCycle(TestCycle testCycle);

    Optional<Execution> findByProjectKeyAndExecutionNumber(String projectKey, Long executionNumber);
}