package nextcrowd.crowdfunding.project.service;

import nextcrowd.crowdfunding.project.event.CrowdfundingProjectRejectedEvent;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;

public class ProjectRejectionService {
    private final EventPublisher eventPublisher;
    private final CrowdfundingProjectRepository repository;

    public ProjectRejectionService(EventPublisher eventPublisher, CrowdfundingProjectRepository repository) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }

    private CrowdfundingProject updateStatusRejected(CrowdfundingProject project) {
        return project.toBuilder()
                      .status(CrowdfundingProject.Status.REJECTED)
                      .build();
    }

    public void reject(CrowdfundingProject project) {
        if (project.getStatus() == CrowdfundingProject.Status.REJECTED) {
            return;
        }
        checkStatus(project);
        CrowdfundingProject rejected = updateStatusRejected(project);
        repository.save(rejected);
        eventPublisher.publish(CrowdfundingProjectRejectedEvent.builder()
                                                                              .projectId(rejected.getId())
                                                                              .build());
    }
    private static void checkStatus(CrowdfundingProject project) {
        if (project.getStatus() != CrowdfundingProject.Status.SUBMITTED) {
            throw new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }
    }

}
