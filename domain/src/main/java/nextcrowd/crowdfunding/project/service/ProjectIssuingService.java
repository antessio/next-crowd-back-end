package nextcrowd.crowdfunding.project.service;

import nextcrowd.crowdfunding.project.event.CrowdfundingProjectIssuedEvent;
import nextcrowd.crowdfunding.project.exception.CrowdfundingProjectException;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;
import nextcrowd.crowdfunding.project.port.EventPublisher;

public class ProjectIssuingService {

    private final EventPublisher eventPublisher;
    private final CrowdfundingProjectRepository repository;

    public ProjectIssuingService(EventPublisher eventPublisher, CrowdfundingProjectRepository repository) {
        this.eventPublisher = eventPublisher;
        this.repository = repository;
    }



    public void issue(CrowdfundingProject project) {
        if (project.getStatus() == CrowdfundingProject.Status.COMPLETED) {
            return;
        }
        checkStatus(project);
        CrowdfundingProject issuedProject = updateStatusCompleted(project);
        repository.save(issuedProject);
        eventPublisher.publish(CrowdfundingProjectIssuedEvent.builder()
                                                                            .projectId(issuedProject.getId())
                                                                            .build());
    }
    private static void checkStatus(CrowdfundingProject project) {
        if (project.getStatus() != CrowdfundingProject.Status.APPROVED) {
            throw new CrowdfundingProjectException(CrowdfundingProjectException.Reason.INVALID_PROJECT_STATUS);
        }
    }
    private CrowdfundingProject updateStatusCompleted(CrowdfundingProject project) {
        return project.toBuilder()
                      .status(CrowdfundingProject.Status.COMPLETED)
                      .build();
    }

}
