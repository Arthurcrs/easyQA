package com.arthur.easy_qa.repository.execution;

import com.arthur.easy_qa.domain.execution.Execution;
import com.arthur.easy_qa.domain.execution.ExecutionStatus;
import com.arthur.easy_qa.domain.testcase.TestCase;
import com.arthur.easy_qa.domain.testcycle.TestCycle;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaExecutionRepository extends JpaRepository<Execution, UUID> {

    boolean existsByTestCycleAndTestCase(TestCycle testCycle, TestCase testCase);

    @Query("SELECT MAX(e.executionNumber) FROM Execution e WHERE e.project.key = :projectKey")
    Optional<Long> findMaxExecutionNumberByProjectKey(@Param("projectKey") String projectKey);

    void deleteByProject_KeyAndTestCycle_TestCycleNumberAndTestCase_TestCaseNumber(
            String projectKey, Long testCycleNumber, Long testCaseNumber);

    List<Execution> findAllByTestCycle(TestCycle testCycle);

    @Query("SELECT e FROM Execution e WHERE e.testCycle = :testCycle " +
            "AND (:status IS NULL OR e.status = :status)")
    List<Execution> findAllWithFilters(@Param("testCycle") TestCycle testCycle,
                                       @Param("status") ExecutionStatus status,
                                       Sort sort);

    Optional<Execution> findByProject_KeyAndExecutionNumber(String projectKey, Long executionNumber);
}