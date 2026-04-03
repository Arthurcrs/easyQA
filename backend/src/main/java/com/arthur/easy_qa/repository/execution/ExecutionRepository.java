package com.arthur.easy_qa.repository.execution;

import com.arthur.easy_qa.domain.execution.Execution;
import com.arthur.easy_qa.domain.testcase.TestCase;
import com.arthur.easy_qa.domain.testcycle.TestCycle;
import com.arthur.easy_qa.domain.execution.ExecutionStatus;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface ExecutionRepository {

    Execution save(Execution execution);

    boolean existsByTestCycleAndTestCase(TestCycle testCycle, TestCase testCase);

    Optional<Long> findMaxExecutionNumberByProjectKey(String projectKey);

    void deleteByProjectKeyAndCycleNumberAndCaseNumber(String projectKey, Long testCycleNumber, Long testCaseNumber);

    List<Execution> findAllByTestCycle(TestCycle testCycle);

    List<Execution> findAllByTestCycleAndFilters(TestCycle testCycle, ExecutionStatus status, Sort sort);

    Optional<Execution> findByProjectKeyAndExecutionNumber(String projectKey, Long executionNumber);
}