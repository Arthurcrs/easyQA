package com.arthur.easy_qa.repository.customfield;

import com.arthur.easy_qa.domain.customfield.CustomField;
import com.arthur.easy_qa.domain.testcase.TestCase;
import com.arthur.easy_qa.domain.testcase.TestCaseFieldValue;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseTestCaseFieldValueRepository implements TestCaseFieldValueRepository {

    private final JpaTestCaseFieldValueRepository jpaRepository;

    public DatabaseTestCaseFieldValueRepository(JpaTestCaseFieldValueRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TestCaseFieldValue save(TestCaseFieldValue fieldValue) {
        return jpaRepository.save(fieldValue);
    }

    @Override
    public List<TestCaseFieldValue> saveAll(List<TestCaseFieldValue> fieldValues) {
        return jpaRepository.saveAll(fieldValues);
    }

    @Override
    public List<TestCaseFieldValue> findAllByTestCase(TestCase testCase) {
        return jpaRepository.findAllByTestCase(testCase);
    }

    @Override
    public Optional<TestCaseFieldValue> findByTestCaseAndCustomField(TestCase testCase, CustomField customField) {
        return jpaRepository.findByTestCaseAndCustomField(testCase, customField);
    }

    @Override
    @Transactional
    public void deleteAllByTestCase(TestCase testCase) {
        jpaRepository.deleteAllByTestCase(testCase);
    }

    @Override
    public void delete(TestCaseFieldValue fieldValue) {
        jpaRepository.delete(fieldValue);
    }
}