package com.arthur.easy_qa.domain;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
