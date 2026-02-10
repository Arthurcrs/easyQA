package com.arthur.easy_qa.dto;


import com.arthur.easy_qa.service.TestCycleService;

import java.util.UUID;

public class TestCycleResponse {

    private UUID id;
    private String name;
    private String version;
    private String environment;
    private String type;


    public TestCycleResponse(UUID id, String name, String version, String environment, String type) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.environment = environment;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getType() {
        return type;
    }

    public String getEnvironment() {
        return environment;
    }
}
