package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.dto.customfield.CreateCustomFieldRequest;
import com.arthur.easy_qa.dto.customfield.CustomFieldResponse;
import com.arthur.easy_qa.service.CustomFieldService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/projects/{projectKey}/custom-fields")
public class CustomFieldController {

    private final CustomFieldService service;

    public CustomFieldController(CustomFieldService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CustomFieldResponse> create(@PathVariable("projectKey") String projectKey,
                                                      @Valid @RequestBody CreateCustomFieldRequest request) {
        CustomFieldResponse response = service.create(projectKey, request);
        return ResponseEntity
                .created(URI.create(String.format("/api/v1/projects/%s/custom-fields/%d", projectKey, response.getFieldNumber())))
                .body(response);
    }

    @GetMapping("/{fieldNumber}")
    public ResponseEntity<CustomFieldResponse> getByNumber(@PathVariable("projectKey") String projectKey,
                                                           @PathVariable("fieldNumber") Long fieldNumber) {
        return service.getByProjectAndNumber(projectKey, fieldNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CustomFieldResponse>> getAll(@PathVariable("projectKey") String projectKey) {
        return ResponseEntity.ok(service.getAllByProject(projectKey));
    }

    @DeleteMapping("/{fieldNumber}")
    public ResponseEntity<Void> delete(@PathVariable("projectKey") String projectKey,
                                       @PathVariable("fieldNumber") Long fieldNumber) {
        boolean deleted = service.delete(projectKey, fieldNumber);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}