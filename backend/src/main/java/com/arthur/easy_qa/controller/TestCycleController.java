package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.dto.testcycle.CreateTestCycleRequest;
import com.arthur.easy_qa.dto.testcycle.TestCycleDetailsResponse;
import com.arthur.easy_qa.dto.testcycle.TestCycleResponse;
import com.arthur.easy_qa.service.TestCycleService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectKey}/test-cycles")
public class TestCycleController {

    private final TestCycleService service;

    public TestCycleController(TestCycleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TestCycleResponse> create(@PathVariable("projectKey") String projectKey,
                                                    @Valid @RequestBody CreateTestCycleRequest request) {
        TestCycleResponse response = service.create(projectKey, request);
        return ResponseEntity
                .created(URI.create(String.format("/api/v1/projects/%s/test-cycles/%d", projectKey, response.getTestCycleNumber())))
                .body(response);
    }

    @GetMapping("/{testCycleNumber}")
    public ResponseEntity<TestCycleDetailsResponse> getByNumber(@PathVariable("projectKey") String projectKey,
                                                                @PathVariable("testCycleNumber") Long testCycleNumber) {
        return service.getDetailsByProjectAndNumber(projectKey, testCycleNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<TestCycleResponse>> getAll(@PathVariable("projectKey") String projectKey) {
        return ResponseEntity.ok(service.getAllByProject(projectKey));
    }

    @PatchMapping("/{testCycleNumber}")
    public ResponseEntity<TestCycleResponse> update(@PathVariable("projectKey") String projectKey,
                                                    @PathVariable("testCycleNumber") Long testCycleNumber,
                                                    @Valid @RequestBody CreateTestCycleRequest request) {
        return service.update(projectKey, testCycleNumber, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{testCycleNumber}")
    public ResponseEntity<Void> delete(@PathVariable("projectKey") String projectKey,
                                       @PathVariable("testCycleNumber") Long testCycleNumber) {
        boolean deleted = service.delete(projectKey, testCycleNumber);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/{testCycleNumber}/test-cases")
    public ResponseEntity<Void> addTestCases(@PathVariable("projectKey") String projectKey,
                                             @PathVariable("testCycleNumber") Long testCycleNumber,
                                             @RequestBody List<Long> testCaseNumbers) {
        service.addTestCasesToCycle(projectKey, testCycleNumber, testCaseNumbers);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{testCycleNumber}/test-cases/{testCaseNumber}")
    public ResponseEntity<Void> removeTestCase(@PathVariable("projectKey") String projectKey,
                                               @PathVariable("testCycleNumber") Long testCycleNumber,
                                               @PathVariable("testCaseNumber") Long testCaseNumber) {
        service.removeTestCaseFromCycle(projectKey, testCycleNumber, testCaseNumber);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{testCycleNumber}/duplicate")
    public ResponseEntity<TestCycleResponse> duplicate(@PathVariable("projectKey") String projectKey,
                                                       @PathVariable("testCycleNumber") Long testCycleNumber) {
        TestCycleResponse duplicatedCycle = service.duplicate(projectKey, testCycleNumber);
        return ResponseEntity.status(201).body(duplicatedCycle);
    }
}