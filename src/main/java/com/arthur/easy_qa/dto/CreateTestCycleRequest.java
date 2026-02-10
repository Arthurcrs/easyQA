package com.arthur.easy_qa.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateTestCycleRequest {

    @NotBlank
    String name;

}
