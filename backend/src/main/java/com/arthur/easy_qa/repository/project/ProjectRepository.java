package com.arthur.easy_qa.repository.project;

import com.arthur.easy_qa.domain.project.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {

    Project save(Project project);

    Optional<Project> findByKey(String key);

    List<Project> findAll(boolean includeArchived);

    boolean deleteByKey(String key);

    boolean existsByNameIgnoreCase(String name);
}