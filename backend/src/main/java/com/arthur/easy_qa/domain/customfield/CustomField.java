package com.arthur.easy_qa.domain.customfield;

import com.arthur.easy_qa.domain.project.Project;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "customField", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomFieldOption> optionList = new ArrayList<>();

    protected CustomField() {
    }

    public CustomField(Project project, Long fieldNumber, String name, CustomFieldType type) {
        this.project = project;
        this.fieldNumber = fieldNumber;
        this.name = name;
        this.type = type;
    }

    public void addOption(CustomFieldOption option) {
        this.optionList.add(option);
        option.setCustomField(this);
    }

    public void removeOption(CustomFieldOption option) {
        this.optionList.remove(option);
        option.setCustomField(null);
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

    public List<CustomFieldOption> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<CustomFieldOption> optionList) {
        this.optionList = optionList;
    }
}