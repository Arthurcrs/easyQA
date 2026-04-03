package com.arthur.easy_qa.domain.customfield;

import com.arthur.easy_qa.domain.project.Project;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "custom_fields")
public class CustomField {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private Long fieldNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomFieldType type;

    @Column(length = 1000)
    private String options;

    protected CustomField() {
    }

    public CustomField(Project project, Long fieldNumber, String name, CustomFieldType type, String options) {
        this.project = project;
        this.fieldNumber = fieldNumber;
        this.name = name;
        this.type = type;
        this.options = options;
    }

    public UUID getId() {
        return id;
    }

    public Long getFieldNumber() {
        return fieldNumber;
    }

    public Project getProject() {
        return project;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CustomFieldType getType() {
        return type;
    }

    public void setType(CustomFieldType type) {
        this.type = type;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }
}