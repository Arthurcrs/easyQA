package com.arthur.easy_qa.domain.project;

import com.arthur.easy_qa.domain.bug.Bug;
import com.arthur.easy_qa.domain.customfield.CustomField;
import com.arthur.easy_qa.domain.execution.Execution;
import com.arthur.easy_qa.domain.testcase.TestCase;
import com.arthur.easy_qa.domain.testcycle.TestCycle;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String name;
    private String key;
    private Instant creationDate;
    private boolean archived;

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<TestCase> testCases = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<TestCycle> testCycles = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<Execution> executions = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<Bug> bugs = new ArrayList<>();

    @OneToMany(mappedBy = "project", cascade = CascadeType.REMOVE)
    private List<CustomField> customFields = new ArrayList<>();

    protected Project() {
    }

    public Project(String name, String key, Instant creationDate, boolean archived) {
        this.name = name;
        this.key = key;
        this.creationDate = creationDate;
        this.archived = archived;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void archive() {
        this.archived = true;
    }

    public void restore() {
        this.archived = false;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public boolean isArchived() {
        return archived;
    }
}