package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.testcase.TestCasePriority;
import com.arthur.easy_qa.domain.testcase.TestCaseStatus;
import com.arthur.easy_qa.domain.testcase.TestCaseType;
import com.arthur.easy_qa.dto.testcase.CreateTestCaseRequest;
import com.arthur.easy_qa.dto.testcase.TestCaseResponse;
import com.arthur.easy_qa.service.TestCaseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectKey}/test-cases")
public class TestCaseController {

    private final TestCaseService service;

    public TestCaseController(TestCaseService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TestCaseResponse> create(@PathVariable("projectKey") String projectKey,
                                                   @Valid @RequestBody CreateTestCaseRequest request) {
        TestCaseResponse response = service.create(projectKey, request);
        return ResponseEntity
                .created(URI.create(String.format("/api/v1/projects/%s/test-cases/%d", projectKey, response.getTestCaseNumber())))
                .body(response);
    }

    @GetMapping("/{testCaseNumber}")
    public ResponseEntity<TestCaseResponse> getByNumber(@PathVariable("projectKey") String projectKey,
                                                        @PathVariable("testCaseNumber") Long testCaseNumber) {
        return service.getByProjectAndNumber(projectKey, testCaseNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TestCaseResponse>> getAll(
            @PathVariable("projectKey") String projectKey,
            @RequestParam(value = "status", required = false) TestCaseStatus status,
            @RequestParam(value = "type", required = false) TestCaseType type,
            @RequestParam(value = "priority", required = false) TestCasePriority priority,
            @RequestParam(value = "q", required = false) String q) {
        return ResponseEntity.ok(service.getAllByProject(projectKey, status, type, priority, q));
    }

    @PatchMapping("/{testCaseNumber}")
    public ResponseEntity<TestCaseResponse> update(@PathVariable("projectKey") String projectKey,
                                                   @PathVariable("testCaseNumber") Long testCaseNumber,
                                                   @Valid @RequestBody CreateTestCaseRequest request) {
        return service.update(projectKey, testCaseNumber, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{testCaseNumber}")
    public ResponseEntity<Void> delete(@PathVariable("projectKey") String projectKey,
                                       @PathVariable("testCaseNumber") Long testCaseNumber) {
        boolean deleted = service.delete(projectKey, testCaseNumber);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}