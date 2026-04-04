package com.arthur.easy_qa.dto.customfield;

import java.util.UUID;

public class CustomFieldOptionResponse {
    private UUID id;
    private String value;
    private boolean active;
    private Integer sortOrder;

    public CustomFieldOptionResponse(UUID id, String value, boolean active, Integer sortOrder) {
        this.id = id;
        this.value = value;
        this.active = active;
        this.sortOrder = sortOrder;
    }

    public UUID getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }
}