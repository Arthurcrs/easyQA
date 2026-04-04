package com.arthur.easy_qa.controller;

import com.arthur.easy_qa.dto.customfield.*;
import com.arthur.easy_qa.service.CustomFieldService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

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

    @PatchMapping("/{fieldNumber}")
    public ResponseEntity<CustomFieldResponse> update(@PathVariable("projectKey") String projectKey,
                                                      @PathVariable("fieldNumber") Long fieldNumber,
                                                      @RequestBody CreateCustomFieldRequest request) {
        return service.update(projectKey, fieldNumber, request)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{fieldNumber}/options")
    public ResponseEntity<CustomFieldOptionResponse> createOption(@PathVariable("projectKey") String projectKey,
                                                                  @PathVariable("fieldNumber") Long fieldNumber,
                                                                  @Valid @RequestBody CreateFieldOptionRequest request) {
        return ResponseEntity.ok(service.addOption(projectKey, fieldNumber, request));
    }

    @PatchMapping("/{fieldNumber}/options/{optionId}")
    public ResponseEntity<CustomFieldOptionResponse> updateOption(@PathVariable("projectKey") String projectKey,
                                                                  @PathVariable("fieldNumber") Long fieldNumber,
                                                                  @PathVariable("optionId") UUID optionId,
                                                                  @Valid @RequestBody UpdateFieldOptionRequest request) {
        return ResponseEntity.ok(service.updateOption(projectKey, fieldNumber, optionId, request));
    }

    @DeleteMapping("/{fieldNumber}/options/{optionId}")
    public ResponseEntity<Void> deleteOption(@PathVariable("projectKey") String projectKey,
                                             @PathVariable("fieldNumber") Long fieldNumber,
                                             @PathVariable("optionId") UUID optionId) {
        service.deleteOption(projectKey, fieldNumber, optionId);
        return ResponseEntity.noContent().build();
    }
}