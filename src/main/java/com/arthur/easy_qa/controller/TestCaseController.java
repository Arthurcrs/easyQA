package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.dto.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.TestCaseResponse;
import com.arthur.easy_qa.service.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test-cases")
public class TestCaseController {

    private final TestCaseService service;

    public TestCaseController(TestCaseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TestCaseResponse> create(@Valid @RequestBody CreateTestCaseRequest request) {
        TestCaseResponse response = service.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/test-cases/" + response.getId()))
                .body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TestCaseResponse> getById(@PathVariable("id") UUID id) {
        return service.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TestCaseResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TestCaseResponse> update(@PathVariable("id") UUID id,
                                                   @Valid @RequestBody CreateTestCaseRequest request) {
        return service.update(id, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        boolean deleted = service.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
