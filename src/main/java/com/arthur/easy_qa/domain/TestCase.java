package com.arthur.easy_qa.domain;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "test_cases")
public class TestCase {

    @Id
    @GeneratedValue
    private final UUID id;

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

    private final Instant creationInstant;
    private Instant lastUpdateInstant;

    public TestCase(UUID id,
                    String us,
                    TestCaseStatus status,
                    String feature,
                    String scenario,
                    String description,
                    TestCasePriority priority,
                    TestCaseType type,
                    Instant creationInstant,
                    Instant lastUpdateInstant) {
        this.id = id;
        this.us = us;
        this.status = status;
        this.feature = feature;
        this.scenario = scenario;
        this.description = description;
        this.priority = priority;
        this.type = type;
        this.creationInstant = creationInstant;
        this.lastUpdateInstant = lastUpdateInstant;
    }

    public UUID getId() {
        return id;
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

    public void setLastUpdateInstant(Instant lastUpdateInstant) {
        this.lastUpdateInstant = lastUpdateInstant;
    }
}
