package com.arthur.easy_qa.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "bugs")
public class Bug {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private Long bugNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BugSeverity severity;

    @Column(nullable = false, updatable = false)
    private Instant openDate;

    private Instant closeDate;

    protected Bug() {
    }

    public Bug(Project project, Long bugNumber, String title, String description, BugSeverity severity) {
        this.project = project;
        this.bugNumber = bugNumber;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.status = BugStatus.OPEN;
    }

    @PrePersist
    void onPersist() {
        this.openDate = Instant.now();
        checkCloseDate();
    }

    @PreUpdate
    void onUpdate() {
        checkCloseDate();
    }

    private void checkCloseDate() {
        if ((this.status == BugStatus.CLOSED || this.status == BugStatus.RESOLVED) && this.closeDate == null) {
            this.closeDate = Instant.now();
        } else if (this.status != BugStatus.CLOSED && this.status != BugStatus.RESOLVED) {
            this.closeDate = null; // Re-opens the bug if status changes back
        }
    }

    public UUID getId() {
        return id;
    }

    public Long getBugNumber() {
        return bugNumber;
    }

    public Project getProject() {
        return project;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BugStatus getStatus() {
        return status;
    }

    public void setStatus(BugStatus status) {
        this.status = status;
    }

    public BugSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(BugSeverity severity) {
        this.severity = severity;
    }

    public Instant getOpenDate() {
        return openDate;
    }

    public Instant getCloseDate() {
        return closeDate;
    }
}