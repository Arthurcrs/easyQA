package com.arthur.easy_qa.repository.customfield;

import com.arthur.easy_qa.domain.CustomField;
import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.domain.TestCaseFieldValue;

import java.util.List;
import java.util.Optional;

public interface TestCaseFieldValueRepository {

    TestCaseFieldValue save(TestCaseFieldValue fieldValue);

    List<TestCaseFieldValue> saveAll(List<TestCaseFieldValue> fieldValues);

    List<TestCaseFieldValue> findAllByTestCase(TestCase testCase);

    Optional<TestCaseFieldValue> findByTestCaseAndCustomField(TestCase testCase, CustomField customField);

    void deleteAllByTestCase(TestCase testCase);
}