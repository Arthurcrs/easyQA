package com.arthur.easy_qa.repository;

import com.arthur.easy_qa.domain.TestCase;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Primary
@Repository
public class DatabaseTestCaseRepository implements TestCaseRepository {

    private final JpaTestCaseRepository repository;

    public DatabaseTestCaseRepository(JpaTestCaseRepository repository) {
        this.repository = repository;
    }

    @Override
    public TestCase save(TestCase testCase) {
        return repository.save(testCase);
    }

    @Override
    public Optional<TestCase> findById(UUID id) {
        return repository.findById(id);
    }

    @Override
    public List<TestCase> findAll() {
        return repository.findAll();
    }

    @Override
    public boolean deleteById(UUID id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
