package com.arthur.easy_qa.repository.bug;

import com.arthur.easy_qa.domain.Bug;
import com.arthur.easy_qa.domain.BugSeverity;
import com.arthur.easy_qa.domain.BugStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaBugRepository extends JpaRepository<Bug, UUID> {

    Optional<Bug> findByProject_KeyAndBugNumber(String projectKey, Long bugNumber);

    List<Bug> findAllByProject_Key(String projectKey);

    void deleteByProject_KeyAndBugNumber(String projectKey, Long bugNumber);

    @Query("SELECT MAX(b.bugNumber) FROM Bug b WHERE b.project.key = :projectKey")
    Optional<Long> findMaxBugNumberByProjectKey(@Param("projectKey") String projectKey);

    @Query("SELECT b FROM Bug b WHERE b.project.key = :projectKey " +
            "AND (:status IS NULL OR b.status = :status) " +
            "AND (:severity IS NULL OR b.severity = :severity)")
    List<Bug> findAllByProjectKeyAndFilters(@Param("projectKey") String projectKey,
                                            @Param("status") BugStatus status,
                                            @Param("severity") BugSeverity severity);
}