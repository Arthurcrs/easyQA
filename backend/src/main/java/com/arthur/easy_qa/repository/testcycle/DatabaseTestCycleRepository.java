package com.arthur.easy_qa.repository.testcycle;

import com.arthur.easy_qa.domain.testcycle.TestCycle;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseTestCycleRepository implements TestCycleRepository {

    private final JpaTestCycleRepository jpaRepository;

    public DatabaseTestCycleRepository(JpaTestCycleRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TestCycle save(TestCycle testCycle) {
        return jpaRepository.save(testCycle);
    }

    @Override
    public Optional<TestCycle> findByProjectKeyAndTestCycleNumber(String projectKey, Long testCycleNumber) {
        return jpaRepository.findByProject_KeyAndTestCycleNumber(projectKey, testCycleNumber);
    }

    @Override
    public List<TestCycle> findAllByProjectKey(String projectKey) {
        return jpaRepository.findAllByProject_Key(projectKey);
    }

    @Override
    @Transactional
    public boolean deleteByProjectKeyAndTestCycleNumber(String projectKey, Long testCycleNumber) {
        if (jpaRepository.findByProject_KeyAndTestCycleNumber(projectKey, testCycleNumber).isPresent()) {
            jpaRepository.deleteByProject_KeyAndTestCycleNumber(projectKey, testCycleNumber);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Long> findMaxTestCycleNumberByProjectKey(String projectKey) {
        return jpaRepository.findMaxTestCycleNumberByProjectKey(projectKey);
    }
}