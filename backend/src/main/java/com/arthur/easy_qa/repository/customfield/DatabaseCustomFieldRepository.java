package com.arthur.easy_qa.repository.customfield;

import com.arthur.easy_qa.domain.customfield.CustomField;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class DatabaseCustomFieldRepository implements CustomFieldRepository {

    private final JpaCustomFieldRepository jpaRepository;

    public DatabaseCustomFieldRepository(JpaCustomFieldRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CustomField save(CustomField customField) {
        return jpaRepository.save(customField);
    }

    @Override
    public Optional<CustomField> findByProjectKeyAndFieldNumber(String projectKey, Long fieldNumber) {
        return jpaRepository.findByProject_KeyAndFieldNumber(projectKey, fieldNumber);
    }

    @Override
    public List<CustomField> findAllByProjectKey(String projectKey) {
        return jpaRepository.findAllByProject_Key(projectKey);
    }

    @Override
    @Transactional
    public boolean deleteByProjectKeyAndFieldNumber(String projectKey, Long fieldNumber) {
        if (jpaRepository.findByProject_KeyAndFieldNumber(projectKey, fieldNumber).isPresent()) {
            jpaRepository.deleteByProject_KeyAndFieldNumber(projectKey, fieldNumber);
            return true;
        }
        return false;
    }

    @Override
    public Optional<Long> findMaxFieldNumberByProjectKey(String projectKey) {
        return jpaRepository.findMaxFieldNumberByProjectKey(projectKey);
    }
}