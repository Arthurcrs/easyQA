package com.arthur.easy_qa.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateTestCycleRequest {

    @NotBlank
    private String name;
    private String version;
    private String type;
    private String environment;

    public String getEnvironment() {
        return environment;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVersion() {
        return version;
    }
}
