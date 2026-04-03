package com.arthur.easy_qa.domain.testcase;

import com.arthur.easy_qa.domain.customfield.CustomField;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "test_case_field_values")
public class TestCaseFieldValue {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", nullable = false)
    private TestCase testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_field_id", nullable = false)
    private CustomField customField;

    @Column(name = "field_value", length = 2000)
    private String value;

    protected TestCaseFieldValue() {
    }

    public TestCaseFieldValue(TestCase testCase, CustomField customField, String value) {
        this.testCase = testCase;
        this.customField = customField;
        this.value = value;
    }

    public UUID getId() {
        return id;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public CustomField getCustomField() {
        return customField;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}