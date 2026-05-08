package com.arthur.easy_qa.repository.customfield;

import com.arthur.easy_qa.domain.customfield.CustomField;

import java.util.List;
import java.util.Optional;

public interface CustomFieldRepository {

    CustomField save(CustomField customField);

    Optional<CustomField> findByProjectKeyAndFieldNumber(String projectKey, Long fieldNumber);

    List<CustomField> findAllByProjectKey(String projectKey);

    boolean deleteByProjectKeyAndFieldNumber(String projectKey, Long fieldNumber);

    Optional<Long> findMaxFieldNumberByProjectKey(String projectKey);
}