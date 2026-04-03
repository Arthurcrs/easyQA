package com.arthur.easy_qa.service;

import com.arthur.easy_qa.domain.Bug;
import com.arthur.easy_qa.domain.BugSeverity;
import com.arthur.easy_qa.domain.BugStatus;
import com.arthur.easy_qa.domain.Project;
import com.arthur.easy_qa.dto.bug.BugResponse;
import com.arthur.easy_qa.dto.bug.CreateBugRequest;
import com.arthur.easy_qa.repository.bug.BugRepository;
import com.arthur.easy_qa.repository.project.ProjectRepository;
import org.springframework.stereotype.Service;
import com.arthur.easy_qa.dto.bug.BugDetailsResponse;
import com.arthur.easy_qa.dto.execution.ExecutionResponse;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BugService {

    private final BugRepository bugRepository;
    private final ProjectRepository projectRepository;

    public BugService(BugRepository bugRepository, ProjectRepository projectRepository) {
        this.bugRepository = bugRepository;
        this.projectRepository = projectRepository;
    }

    public BugResponse create(String projectKey, CreateBugRequest request) {
        Project project = projectRepository.findByKey(projectKey)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectKey));

        Long nextNumber = bugRepository.findMaxBugNumberByProjectKey(projectKey).orElse(0L) + 1L;

        Bug bug = new Bug(
                project,
                nextNumber,
                request.getTitle(),
                request.getDescription(),
                request.getSeverity()
        );

        if (request.getStatus() != null) {
            bug.setStatus(request.getStatus());
        }

        bugRepository.save(bug);
        return toResponse(bug);
    }

    @Transactional(readOnly = true)
    public Optional<BugDetailsResponse> getByProjectAndNumber(String projectKey, Long bugNumber) {
        return bugRepository.findByProjectKeyAndBugNumber(projectKey, bugNumber)
                .map(bug -> {
                    List<ExecutionResponse> executionResponses = bug.getLinkedExecutions().stream()
                            .map(e -> new ExecutionResponse(
                                    e.getProject().getKey(),
                                    e.getTestCycle().getTestCycleNumber(),
                                    e.getTestCase().getTestCaseNumber(),
                                    e.getExecutionNumber(),
                                    e.getTestCase().getUs(),
                                    e.getStatus()
                            )).toList();

                    return new BugDetailsResponse(
                            bug.getProject().getKey(),
                            bug.getBugNumber(),
                            bug.getTitle(),
                            bug.getDescription(),
                            bug.getStatus(),
                            bug.getSeverity(),
                            bug.getOpenDate(),
                            bug.getCloseDate(),
                            executionResponses
                    );
                });
    }

    public List<BugResponse> getAllByProject(String projectKey, BugStatus status, BugSeverity severity) {
        return bugRepository.findAllByProjectKeyAndFilters(projectKey, status, severity)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<BugResponse> update(String projectKey, Long bugNumber, CreateBugRequest request) {
        return bugRepository.findByProjectKeyAndBugNumber(projectKey, bugNumber)
                .map(existing -> {
                    existing.setTitle(request.getTitle());
                    existing.setDescription(request.getDescription());
                    existing.setSeverity(request.getSeverity());

                    if (request.getStatus() != null) {
                        existing.setStatus(request.getStatus());
                    }

                    bugRepository.save(existing);
                    return toResponse(existing);
                });
    }

    public boolean delete(String projectKey, Long bugNumber) {
        return bugRepository.deleteByProjectKeyAndBugNumber(projectKey, bugNumber);
    }

    private BugResponse toResponse(Bug bug) {
        return new BugResponse(
                bug.getProject().getKey(),
                bug.getBugNumber(),
                bug.getTitle(),
                bug.getDescription(),
                bug.getStatus(),
                bug.getSeverity(),
                bug.getOpenDate(),
                bug.getCloseDate()
        );
    }
}