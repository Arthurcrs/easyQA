package com.arthur.easy_qa.domain.execution;

import com.arthur.easy_qa.domain.bug.Bug;
import com.arthur.easy_qa.domain.project.Project;
import com.arthur.easy_qa.domain.testcycle.TestCycle;
import com.arthur.easy_qa.domain.testcase.TestCase;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "executions")
public class Execution {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private Long executionNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_cycle_id", nullable = false)
    private TestCycle testCycle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", nullable = false)
    private TestCase testCase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExecutionStatus status;

    @ManyToMany
    @JoinTable(
            name = "execution_bugs",
            joinColumns = @JoinColumn(name = "execution_id"),
            inverseJoinColumns = @JoinColumn(name = "bug_id")
    )
    private Set<Bug> linkedBugs = new HashSet<>();

    protected Execution() {
    }

    public Execution(Project project, Long executionNumber, TestCycle testCycle, TestCase testCase) {
        this.project = project;
        this.executionNumber = executionNumber;
        this.testCycle = testCycle;
        this.testCase = testCase;
        this.status = ExecutionStatus.NOT_EXECUTED;
    }

    public UUID getId() {
        return id;
    }

    public Long getExecutionNumber() {
        return executionNumber;
    }

    public void setExecutionNumber(Long executionNumber) {
        this.executionNumber = executionNumber;
    }

    public Project getProject() {
        return project;
    }

    public TestCycle getTestCycle() {
        return testCycle;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public Set<Bug> getLinkedBugs() {
        return linkedBugs;
    }

    public void addBug(Bug bug) {
        this.linkedBugs.add(bug);
        bug.getLinkedExecutions().add(this);
    }

    public void removeBug(Bug bug) {
        this.linkedBugs.remove(bug);
        bug.getLinkedExecutions().remove(this);
    }
}