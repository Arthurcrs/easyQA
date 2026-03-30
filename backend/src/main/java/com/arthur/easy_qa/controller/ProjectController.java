package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.dto.project.CreateProjectRequest;
import com.arthur.easy_qa.dto.project.ProjectResponse;
import com.arthur.easy_qa.dto.project.UpdateProjectRequest;
import com.arthur.easy_qa.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody CreateProjectRequest request) {
        ProjectResponse response = service.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/projects/" + response.getKey()))
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll(
            @RequestParam(value = "includeArchived", defaultValue = "false") boolean includeArchived) {
        return ResponseEntity.ok(service.getAll(includeArchived));
    }

    @GetMapping("/{projectKey}")
    public ResponseEntity<ProjectResponse> getByKey(@PathVariable("projectKey") String projectKey) {
        return service.getByKey(projectKey)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{projectKey}")
    public ResponseEntity<ProjectResponse> updateName(@PathVariable("projectKey") String projectKey,
                                                      @Valid @RequestBody UpdateProjectRequest request) {
        return service.updateName(projectKey, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{projectKey}/archive")
    public ResponseEntity<ProjectResponse> archive(@PathVariable("projectKey") String projectKey) {
        return service.archive(projectKey)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{projectKey}/restore")
    public ResponseEntity<ProjectResponse> restore(@PathVariable("projectKey") String projectKey) {
        return service.restore(projectKey)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{projectKey}")
    public ResponseEntity<Void> delete(@PathVariable("projectKey") String projectKey) {
        boolean deleted = service.delete(projectKey);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
