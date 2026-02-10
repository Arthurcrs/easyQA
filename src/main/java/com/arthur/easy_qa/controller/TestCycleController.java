package com.arthur.easy_qa.controller;


import com.arthur.easy_qa.dto.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.TestCycleResponse;
import com.arthur.easy_qa.service.TestCycleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/test-cycles")
public class TestCycleController {

    private final TestCycleService service;

    public TestCycleController(TestCycleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TestCycleResponse> create(@Valid @RequestBody CreateTestCycleRequest request) {
        TestCycleResponse response = service.create(request);

        return ResponseEntity
                .created(URI.create("/api/v1/test-cycles/" + response.getId()))
                .body(response);

    }


}
