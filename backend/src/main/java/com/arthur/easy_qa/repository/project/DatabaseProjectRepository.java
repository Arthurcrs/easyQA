package com.arthur.easy_qa.repository.project;

import com.arthur.easy_qa.domain.project.Project;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseProjectRepository implements ProjectRepository {

    private final JpaProjectRepository jpaRepository;

    public DatabaseProjectRepository(JpaProjectRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Project save(Project project) {
        return jpaRepository.save(project);
    }

    @Override
    public Optional<Project> findByKey(String key) {
        return jpaRepository.findByKey(key);
    }

    @Override
    public List<Project> findAll(boolean includeArchived) {
        if (includeArchived) {
            return jpaRepository.findAll();
        }
        return jpaRepository.findByArchivedFalse();
    }

    @Override
    @Transactional
    public boolean deleteByKey(String key) {
        if (jpaRepository.findByKey(key).isPresent()) {
            jpaRepository.deleteByKey(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean existsByNameIgnoreCase(String name) {
        return jpaRepository.existsByNameIgnoreCase(name);
    }
}