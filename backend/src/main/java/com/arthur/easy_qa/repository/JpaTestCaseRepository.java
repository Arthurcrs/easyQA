package com.arthur.easy_qa.repository;

import com.arthur.easy_qa.domain.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JpaTestCaseRepository extends JpaRepository<TestCase, UUID> {
}
