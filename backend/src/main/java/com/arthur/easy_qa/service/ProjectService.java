package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.dto.project.CreateProjectRequest;
import com.arthur.easy_qa.dto.project.ProjectResponse;
import com.arthur.easy_qa.dto.project.UpdateProjectRequest;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import java.time.Instant;

@Service
public class ProjectService {

    private final ProjectRepository repository;

    public ProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    public ProjectResponse create(CreateProjectRequest request) {
        validateName(request == null ? null : request.getName());

        String normalizedName = request.getName().trim();

        if (repository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Project name already exists");
        }

        String key = generateKeyFromName(normalizedName);
        Instant creationDate = Instant.now();

        Project project = new Project(normalizedName, key, creationDate, false);
        Project savedProject = repository.save(project);

        return toResponse(savedProject);
    }

    public List<ProjectResponse> getAll(boolean includeArchived) {
        List<Project> projects = includeArchived
                ? repository.findAll()
                : repository.findAllByArchivedFalse();

        return projects.stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ProjectResponse> getByKey(String key) {
        return repository.findByKey(key)
                .map(this::toResponse);
    }

    public Optional<ProjectResponse> updateName(String key, UpdateProjectRequest request) {
        validateName(request == null ? null : request.getName());

        Optional<Project> maybeProject = repository.findByKey(key);
        if (maybeProject.isEmpty()) {
            return Optional.empty();
        }

        String normalizedName = request.getName().trim();
        Project project = maybeProject.get();

        if (!project.getName().equalsIgnoreCase(normalizedName)
                && repository.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("Project name already exists");
        }

        project.rename(normalizedName);
        Project savedProject = repository.save(project);

        return Optional.of(toResponse(savedProject));
    }

    public Optional<ProjectResponse> archive(String key) {
        Optional<Project> maybeProject = repository.findByKey(key);
        if (maybeProject.isEmpty()) {
            return Optional.empty();
        }

        Project project = maybeProject.get();
        project.archive();
        Project savedProject = repository.save(project);

        return Optional.of(toResponse(savedProject));
    }

    public Optional<ProjectResponse> restore(String key) {
        Optional<Project> maybeProject = repository.findByKey(key);
        if (maybeProject.isEmpty()) {
            return Optional.empty();
        }

        Project project = maybeProject.get();
        project.restore();
        Project savedProject = repository.save(project);

        return Optional.of(toResponse(savedProject));
    }

    public boolean delete(String key) {
        return repository.deleteByKey(key) > 0;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Project name must not be blank");
        }
    }

    private String generateKeyFromName(String name) {
        return name.trim()
                .toUpperCase()
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }

    private ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getKey(),
                project.getCreationDate(),
                project.isArchived()
        );
    }
}
