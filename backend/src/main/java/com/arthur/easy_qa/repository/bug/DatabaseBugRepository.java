package com.arthur.easy_qa.repository.bug;

import com.arthur.easy_qa.domain.Bug;
import com.arthur.easy_qa.domain.BugSeverity;
import com.arthur.easy_qa.domain.BugStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseBugRepository implements BugRepository {

    private final JpaBugRepository jpaRepository;

    public DatabaseBugRepository(JpaBugRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Bug save(Bug bug) {
        return jpaRepository.save(bug);
    }

    @Override
    public Optional<Bug> findByProjectKeyAndBugNumber(String projectKey, Long bugNumber) {
        return jpaRepository.findByProject_KeyAndBugNumber(projectKey, bugNumber);
    }

    @Override
    public List<Bug> findAllByProjectKey(String projectKey) {
        return jpaRepository.findAllByProject_Key(projectKey);
    }

    @Override
    public List<Bug> findAllByProjectKeyAndFilters(String projectKey, BugStatus status, BugSeverity severity) {
        return jpaRepository.findAllByProjectKeyAndFilters(projectKey, status, severity);
    }

    @Override
    @Transactional
    public boolean deleteByProjectKeyAndBugNumber(String projectKey, Long bugNumber) {
        if (jpaRepository.findByProject_KeyAndBugNumber(projectKey, bugNumber).isPresent()) {
            jpaRepository.deleteByProject_KeyAndBugNumber(projectKey, bugNumber);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Long> findMaxBugNumberByProjectKey(String projectKey) {
        return jpaRepository.findMaxBugNumberByProjectKey(projectKey);
    }
}