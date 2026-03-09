package com.arthur.easy_qa.repository.project;

import com.arthur.easy_qa.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaProjectRepository extends JpaRepository<Project, UUID> {
}
