package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.domain.BugSeverity;
import com.arthur.easy_qa.domain.BugStatus;
import com.arthur.easy_qa.dto.bug.BugResponse;
import com.arthur.easy_qa.dto.bug.CreateBugRequest;
import com.arthur.easy_qa.service.BugService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectKey}/bugs")
public class BugController {

    private final BugService service;

    public BugController(BugService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BugResponse> create(@PathVariable("projectKey") String projectKey,
                                              @Valid @RequestBody CreateBugRequest request) {
        BugResponse response = service.create(projectKey, request);
        return ResponseEntity
                .created(URI.create(String.format("/api/v1/projects/%s/bugs/%d", projectKey, response.getBugNumber())))
                .body(response);
    }

    @GetMapping("/{bugNumber}")
    public ResponseEntity<BugResponse> getByNumber(@PathVariable("projectKey") String projectKey,
                                                   @PathVariable("bugNumber") Long bugNumber) {
        return service.getByProjectAndNumber(projectKey, bugNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<BugResponse>> getAll(
            @PathVariable("projectKey") String projectKey,
            @RequestParam(value = "status", required = false) BugStatus status,
            @RequestParam(value = "severity", required = false) BugSeverity severity) {

        return ResponseEntity.ok(service.getAllByProject(projectKey, status, severity));
    }

    @PatchMapping("/{bugNumber}")
    public ResponseEntity<BugResponse> update(@PathVariable("projectKey") String projectKey,
                                              @PathVariable("bugNumber") Long bugNumber,
                                              @Valid @RequestBody CreateBugRequest request) {
        return service.update(projectKey, bugNumber, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{bugNumber}")
    public ResponseEntity<Void> delete(@PathVariable("projectKey") String projectKey,
                                       @PathVariable("bugNumber") Long bugNumber) {
        boolean deleted = service.delete(projectKey, bugNumber);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}