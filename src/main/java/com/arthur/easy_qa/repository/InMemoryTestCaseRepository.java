package com.arthur.easy_qa.repository;

import com.arthur.easy_qa.domain.TestCase;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTestCaseRepository implements TestCaseRepository {

    private final Map<UUID, TestCase> db = new ConcurrentHashMap<>();

    @Override
    public TestCase save(TestCase testCase) {
        db.put(testCase.getId(), testCase);
        return testCase;
    }

    @Override
    public Optional<TestCase> findById(UUID id) {
        return Optional.ofNullable(db.get(id));
    }

    @Override
    public List<TestCase> findAll() {
        return new ArrayList<>(db.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        return db.remove(id) != null;
    }
}
