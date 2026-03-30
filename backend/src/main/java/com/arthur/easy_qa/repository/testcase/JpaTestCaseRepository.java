package com.arthur.easy_qa.repository.testcase;

import com.arthur.easy_qa.domain.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaTestCaseRepository extends JpaRepository<TestCase, UUID> {

    Optional<TestCase> findByProject_KeyAndTestCaseNumber(String projectKey, Long testCaseNumber);

    List<TestCase> findAllByProject_Key(String projectKey);

    void deleteByProject_KeyAndTestCaseNumber(String projectKey, Long testCaseNumber);

    @Query("SELECT MAX(tc.testCaseNumber) FROM TestCase tc WHERE tc.project.key = :projectKey")
    Optional<Long> findMaxTestCaseNumberByProjectKey(@Param("projectKey") String projectKey);
}
