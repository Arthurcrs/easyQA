package com.arthur.easy_qa.repository.customfield;

import com.arthur.easy_qa.domain.customfield.CustomField;
import com.arthur.easy_qa.domain.testcase.TestCase;
import com.arthur.easy_qa.domain.testcase.TestCaseFieldValue;

import java.util.List;
import java.util.Optional;

public interface TestCaseFieldValueRepository {

    TestCaseFieldValue save(TestCaseFieldValue fieldValue);

    List<TestCaseFieldValue> saveAll(List<TestCaseFieldValue> fieldValues);

    List<TestCaseFieldValue> findAllByTestCase(TestCase testCase);

    Optional<TestCaseFieldValue> findByTestCaseAndCustomField(TestCase testCase, CustomField customField);

    void deleteAllByTestCase(TestCase testCase);
}