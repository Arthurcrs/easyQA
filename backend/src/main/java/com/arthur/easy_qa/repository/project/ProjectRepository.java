package com.arthur.easy_qa.repository.project;

import com.arthur.easy_qa.domain.Project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    boolean existsByNameIgnoreCase(String name);

    Optional<Project> findByKey(String key);

    List<Project> findAllByArchivedFalse();

    long deleteByKey(String key);
}
