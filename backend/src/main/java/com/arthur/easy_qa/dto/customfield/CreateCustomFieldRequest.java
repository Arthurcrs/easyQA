package com.arthur.easy_qa.dto.customfield;

import com.arthur.easy_qa.domain.customfield.CustomFieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateCustomFieldRequest {

    @NotBlank(message = "Field name is required")
    private String name;

    @NotNull(message = "Field type is required")
    private CustomFieldType type;

    private List<String> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomFieldType getType() {
        return type;
    }

    public void setType(CustomFieldType type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }
}