package com.arthur.easy_qa.repository.bug;

import com.arthur.easy_qa.domain.bug.Bug;
import com.arthur.easy_qa.domain.bug.BugSeverity;
import com.arthur.easy_qa.domain.bug.BugStatus;

import java.util.List;
import java.util.Optional;

public interface BugRepository {

    Bug save(Bug bug);

    Optional<Bug> findByProjectKeyAndBugNumber(String projectKey, Long bugNumber);

    List<Bug> findAllByProjectKey(String projectKey);

    // For optional filtering
    List<Bug> findAllByProjectKeyAndFilters(String projectKey, BugStatus status, BugSeverity severity);

    boolean deleteByProjectKeyAndBugNumber(String projectKey, Long bugNumber);

    Optional<Long> findMaxBugNumberByProjectKey(String projectKey);
}