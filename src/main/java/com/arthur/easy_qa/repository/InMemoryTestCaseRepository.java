package com.arthur.easy_qa.repository;

import com.arthur.easy_qa.domain.TestCase;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryTestCaseRepository implements TestCaseRepository {

    private final Map<UUID, TestCase> repository = new ConcurrentHashMap<>();

    @Override
    public TestCase save(TestCase testCase) {
        repository.put(testCase.getId(), testCase);
        return testCase;
    }

    @Override
    public Optional<TestCase> findById(UUID id) {
        return Optional.ofNullable(repository.get(id));
    }

    @Override
    public List<TestCase> findAll() {
        return new ArrayList<>(repository.values());
    }

    @Override
    public boolean deleteById(UUID id) {
        return repository.remove(id) != null;
    }
}
