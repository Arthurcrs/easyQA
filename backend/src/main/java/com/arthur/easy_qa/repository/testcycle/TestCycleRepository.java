package com.arthur.easy_qa.repository.testcycle;

import com.arthur.easy_qa.domain.testcycle.TestCycle;

import java.util.List;
import java.util.Optional;

public interface TestCycleRepository {
    TestCycle save(TestCycle testCycle);

    Optional<TestCycle> findByProjectKeyAndTestCycleNumber(String projectKey, Long testCycleNumber);

    List<TestCycle> findAllByProjectKey(String projectKey);

    boolean deleteByProjectKeyAndTestCycleNumber(String projectKey, Long testCycleNumber);

    Optional<Long> findMaxTestCycleNumberByProjectKey(String projectKey);
}