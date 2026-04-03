package com.arthur.easy_qa.repository.customfield;

import com.arthur.easy_qa.domain.CustomField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaCustomFieldRepository extends JpaRepository<CustomField, UUID> {

    Optional<CustomField> findByProject_KeyAndFieldNumber(String projectKey, Long fieldNumber);

    List<CustomField> findAllByProject_Key(String projectKey);

    void deleteByProject_KeyAndFieldNumber(String projectKey, Long fieldNumber);

    @Query("SELECT MAX(c.fieldNumber) FROM CustomField c WHERE c.project.key = :projectKey")
    Optional<Long> findMaxFieldNumberByProjectKey(@Param("projectKey") String projectKey);
}