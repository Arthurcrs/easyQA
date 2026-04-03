package com.arthur.easy_qa.repository.customfield;

import com.arthur.easy_qa.domain.CustomField;
import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.domain.TestCaseFieldValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaTestCaseFieldValueRepository extends JpaRepository<TestCaseFieldValue, UUID> {

    List<TestCaseFieldValue> findAllByTestCase(TestCase testCase);

    Optional<TestCaseFieldValue> findByTestCaseAndCustomField(TestCase testCase, CustomField customField);

    void deleteAllByTestCase(TestCase testCase);
}