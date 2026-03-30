package com.arthur.easy_qa.dto.testcycle;

import jakarta.validation.constraints.NotBlank;

public class CreateTestCycleRequest {

    @NotBlank(message = "Test cycle name is required")
    private String name;

    private String version;
    private String environment;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}