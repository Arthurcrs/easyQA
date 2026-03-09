package com.arthur.easy_qa.dto.project;

import jakarta.validation.constraints.NotBlank;

public class CreateProjectRequest {

    @NotBlank
    private String name;

    public CreateProjectRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
