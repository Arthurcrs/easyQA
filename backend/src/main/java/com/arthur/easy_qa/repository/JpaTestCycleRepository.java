package com.arthur.easy_qa.repository;

import com.arthur.easy_qa.domain.TestCase;
import com.arthur.easy_qa.domain.TestCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaTestCycleRepository extends JpaRepository<TestCycle, UUID> {
}
