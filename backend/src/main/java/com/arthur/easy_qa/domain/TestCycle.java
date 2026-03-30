package com.arthur.easy_qa.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "test_cycles")
public class TestCycle {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private Long testCycleNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    private String version;
    private String environment;
    private String type;

    private Instant creationInstant;
    private Instant lastUpdateInstant;

    protected TestCycle() {
    } // JPA requires default constructor

    public TestCycle(Project project, Long testCycleNumber, String name, String version, String environment, String type) {
        this.project = project;
        this.testCycleNumber = testCycleNumber;
        this.name = name;
        this.version = version;
        this.environment = environment;
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

    public Long getTestCycleNumber() {
        return testCycleNumber;
    }

    public void setTestCycleNumber(Long testCycleNumber) {
        this.testCycleNumber = testCycleNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getCreationInstant() {
        return creationInstant;
    }

    public Instant getLastUpdateInstant() {
        return lastUpdateInstant;
    }
}