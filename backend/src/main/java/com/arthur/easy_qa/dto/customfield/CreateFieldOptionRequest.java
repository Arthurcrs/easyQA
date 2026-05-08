package com.arthur.easy_qa.dto.customfield;

import jakarta.validation.constraints.NotBlank;

public class CreateFieldOptionRequest {
    @NotBlank(message = "Value is required")
    private String value;
    private Integer sortOrder = 0;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}