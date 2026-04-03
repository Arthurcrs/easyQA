package com.arthur.easy_qa.repository.testcycle;

import com.arthur.easy_qa.domain.testcycle.TestCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaTestCycleRepository extends JpaRepository<TestCycle, UUID> {
    Optional<TestCycle> findByProject_KeyAndTestCycleNumber(String projectKey, Long testCycleNumber);

    List<TestCycle> findAllByProject_Key(String projectKey);

    void deleteByProject_KeyAndTestCycleNumber(String projectKey, Long testCycleNumber);

    @Query("SELECT MAX(tc.testCycleNumber) FROM TestCycle tc WHERE tc.project.key = :projectKey")
    Optional<Long> findMaxTestCycleNumberByProjectKey(@Param("projectKey") String projectKey);
}