package com.arthur.easy_qa.dto.project;

import java.time.Instant;
import java.util.UUID;

public class ProjectResponse {

    private UUID id;
    private String name;
    private String key;
    private Instant creationDate;
    private boolean archived;

    public ProjectResponse(UUID id, String name, String key, Instant creationDate, boolean archived) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.creationDate = creationDate;
        this.archived = archived;
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
