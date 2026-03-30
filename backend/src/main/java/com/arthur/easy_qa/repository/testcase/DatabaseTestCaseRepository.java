package com.arthur.easy_qa.repository.testcase;

import com.arthur.easy_qa.domain.TestCase;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseTestCaseRepository implements TestCaseRepository {

    private final JpaTestCaseRepository jpaRepository;

    public DatabaseTestCaseRepository(JpaTestCaseRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TestCase save(TestCase testCase) {
        return jpaRepository.save(testCase);
    }

    @Override
    public Optional<TestCase> findByProjectKeyAndTestCaseNumber(String projectKey, Long testCaseNumber) {
        return jpaRepository.findByProject_KeyAndTestCaseNumber(projectKey, testCaseNumber);
    }

    @Override
    public List<TestCase> findAllByProjectKey(String projectKey) {
        return jpaRepository.findAllByProject_Key(projectKey);
    }

    @Override
    @Transactional
    public boolean deleteByProjectKeyAndTestCaseNumber(String projectKey, Long testCaseNumber) {
        if (jpaRepository.findByProject_KeyAndTestCaseNumber(projectKey, testCaseNumber).isPresent()) {
            jpaRepository.deleteByProject_KeyAndTestCaseNumber(projectKey, testCaseNumber);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Long> findMaxTestCaseNumberByProjectKey(String projectKey) {
        return jpaRepository.findMaxTestCaseNumberByProjectKey(projectKey);
    }
}