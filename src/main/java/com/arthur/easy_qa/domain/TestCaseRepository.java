package com.arthur.easy_qa.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {
}

