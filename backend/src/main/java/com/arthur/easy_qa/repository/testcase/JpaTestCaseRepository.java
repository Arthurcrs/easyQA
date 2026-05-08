package com.arthur.easy_qa.repository.testcase;

import com.arthur.easy_qa.domain.testcase.TestCase;
import com.arthur.easy_qa.domain.testcase.TestCasePriority;
import com.arthur.easy_qa.domain.testcase.TestCaseStatus;
import com.arthur.easy_qa.domain.testcase.TestCaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaTestCaseRepository extends JpaRepository<TestCase, UUID> {

    Optional<TestCase> findByProject_KeyAndTestCaseNumber(String projectKey, Long testCaseNumber);

    List<TestCase> findAllByProject_Key(String projectKey);

    @Query("SELECT tc FROM TestCase tc WHERE tc.project.key = :projectKey " +
            "AND (:status IS NULL OR tc.status = :status) " +
            "AND (:type IS NULL OR tc.type = :type) " +
            "AND (:priority IS NULL OR tc.priority = :priority) " +
            "AND (:q IS NULL OR :q = '' " +
            "  OR LOWER(tc.feature) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "  OR LOWER(tc.scenario) LIKE LOWER(CONCAT('%', :q, '%')) " +
            "  OR LOWER(tc.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    List<TestCase> findAllWithFilters(
            @Param("projectKey") String projectKey,
            @Param("status") TestCaseStatus status,
            @Param("type") TestCaseType type,
            @Param("priority") TestCasePriority priority,
            @Param("q") String q);

    void deleteByProject_KeyAndTestCaseNumber(String projectKey, Long testCaseNumber);

    @Query("SELECT MAX(tc.testCaseNumber) FROM TestCase tc WHERE tc.project.key = :projectKey")
    Optional<Long> findMaxTestCaseNumberByProjectKey(@Param("projectKey") String projectKey);
}