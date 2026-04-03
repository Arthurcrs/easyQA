package com.arthur.easy_qa.dto.customfield;

import com.arthur.easy_qa.domain.customfield.CustomFieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateCustomFieldRequest {

    @NotBlank(message = "Field name is required")
    private String name;

    @NotNull(message = "Field type is required")
    private CustomFieldType type;

    private String options;

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

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}