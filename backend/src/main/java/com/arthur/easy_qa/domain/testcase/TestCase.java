package com.arthur.easy_qa.domain.testcase;

import com.arthur.easy_qa.domain.execution.Execution;
import com.arthur.easy_qa.domain.project.Project;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "test_cases")
public class TestCase {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private Long testCaseNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private String us;
    private String feature;
    private String scenario;
    private String description;

    @Enumerated(EnumType.STRING)
    private TestCasePriority priority;

    @Enumerated(EnumType.STRING)
    private TestCaseStatus status;

    @Enumerated(EnumType.STRING)
    private TestCaseType type;

    private Instant creationInstant;
    private Instant lastUpdateInstant;

    @OneToMany(mappedBy = "testCase", cascade = CascadeType.REMOVE)
    private List<TestCaseFieldValue> customFieldValues = new ArrayList<>();

    @OneToMany(mappedBy = "testCase", cascade = CascadeType.REMOVE)
    private List<Execution> executions = new ArrayList<>();

    protected TestCase() {
    }

    public TestCase(
            Project project,
            Long testCaseNumber,
            String us,
            TestCaseStatus status,
            String feature,
            String scenario,
            String description,
            TestCasePriority priority,
            TestCaseType type
    ) {
        this.project = project;
        this.testCaseNumber = testCaseNumber;
        this.us = us;
        this.status = status;
        this.feature = feature;
        this.scenario = scenario;
        this.description = description;
        this.priority = priority;
        this.type = type;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.creationInstant = now;
        this.lastUpdateInstant = now;
    }

    @PreUpdate
    void onUpdate() {
        this.lastUpdateInstant = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Long getTestCaseNumber() {
        return testCaseNumber;
    }

    public void setTestCaseNumber(Long testCaseNumber) {
        this.testCaseNumber = testCaseNumber;
    }

    public String getUs() {
        return us;
    }

    public void setUs(String us) {
        this.us = us;
    }

    public TestCaseStatus getStatus() {
        return status;
    }

    public void setStatus(TestCaseStatus status) {
        this.status = status;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TestCasePriority getPriority() {
        return priority;
    }

    public void setPriority(TestCasePriority priority) {
        this.priority = priority;
    }

    public TestCaseType getType() {
        return type;
    }

    public void setType(TestCaseType type) {
        this.type = type;
    }

    public Instant getCreationInstant() {
        return creationInstant;
    }

    public Instant getLastUpdateInstant() {
        return lastUpdateInstant;
    }

    public List<Execution> getExecutions() {
        return executions;
    }
}