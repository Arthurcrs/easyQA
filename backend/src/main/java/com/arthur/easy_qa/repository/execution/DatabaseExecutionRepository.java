package com.arthur.easy_qa.repository.execution;

import com.arthur.easy_qa.domain.Execution;
import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.domain.TestCycle;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseExecutionRepository implements ExecutionRepository {

    private final JpaExecutionRepository jpaRepository;

    public DatabaseExecutionRepository(JpaExecutionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Execution save(Execution execution) {
        return jpaRepository.save(execution);
    }

    @Override
    public boolean existsByTestCycleAndTestCase(TestCycle testCycle, TestCase testCase) {
        return jpaRepository.existsByTestCycleAndTestCase(testCycle, testCase);
    }

    @Override
    public Optional<Long> findMaxExecutionNumberByProjectKey(String projectKey) {
        return jpaRepository.findMaxExecutionNumberByProjectKey(projectKey);
    }

    @Override
    @Transactional
    public void deleteByProjectKeyAndCycleNumberAndCaseNumber(String projectKey, Long testCycleNumber, Long testCaseNumber) {
        jpaRepository.deleteByProject_KeyAndTestCycle_TestCycleNumberAndTestCase_TestCaseNumber(
                projectKey, testCycleNumber, testCaseNumber);
    }

    @Override
    public List<Execution> findAllByTestCycle(TestCycle testCycle) {
        return jpaRepository.findAllByTestCycle(testCycle);
    }

    @Override
    public Optional<Execution> findByProjectKeyAndExecutionNumber(String projectKey, Long executionNumber) {
        return jpaRepository.findByProject_KeyAndExecutionNumber(projectKey, executionNumber);
    }
}