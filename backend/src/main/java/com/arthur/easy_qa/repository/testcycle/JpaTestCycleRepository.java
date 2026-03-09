package com.arthur.easy_qa.repository.testcycle;

import com.arthur.easy_qa.domain.TestCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaTestCycleRepository extends JpaRepository<TestCycle, UUID> {
}
