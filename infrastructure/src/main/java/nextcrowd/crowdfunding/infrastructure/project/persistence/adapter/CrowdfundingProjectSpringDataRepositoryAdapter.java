package nextcrowd.crowdfunding.infrastructure.project.persistence.adapter;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import nextcrowd.crowdfunding.infrastructure.project.persistence.InvestmentRepository;
import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectOwnerRepository;
import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectTimelineEntity;
import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectTimelineRepository;
import nextcrowd.crowdfunding.project.model.CrowdfundingProject;
import nextcrowd.crowdfunding.project.model.Investment;
import nextcrowd.crowdfunding.project.model.InvestmentId;
import nextcrowd.crowdfunding.project.model.InvestmentStatus;
import nextcrowd.crowdfunding.project.model.ProjectId;
import nextcrowd.crowdfunding.project.model.ProjectOwner;
import nextcrowd.crowdfunding.project.model.TimelineEvent;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;

@Component
public class CrowdfundingProjectSpringDataRepositoryAdapter implements CrowdfundingProjectRepository {

    private final nextcrowd.crowdfunding.infrastructure.project.persistence.CrowdfundingProjectRepository crowdfundingProjectRepository;
    private final ProjectOwnerRepository projectOwnerRepository;
    private final InvestmentRepository investmentRepository;
    private final ProjectTimelineRepository projectTimelineRepository;

    public CrowdfundingProjectSpringDataRepositoryAdapter(
            nextcrowd.crowdfunding.infrastructure.project.persistence.CrowdfundingProjectRepository crowdfundingProjectRepository,
            ProjectOwnerRepository projectOwnerRepository,
            InvestmentRepository investmentRepository,
            ProjectTimelineRepository projectTimelineRepository) {
        this.crowdfundingProjectRepository = crowdfundingProjectRepository;
        this.projectOwnerRepository = projectOwnerRepository;
        this.investmentRepository = investmentRepository;
        this.projectTimelineRepository = projectTimelineRepository;
    }


    @Override
    public CrowdfundingProject save(CrowdfundingProject project) {
        return Optional.of(project)
                       .map(CrowdfundingProjectAdapter::toEntity)
                       .map(crowdfundingProjectRepository::save)
                       .map(CrowdfundingProjectAdapter::toDomain)
                       .orElseThrow();
    }

    @Override
    public ProjectOwner createProjectOwner(ProjectOwner projectOwner) {
        return ProjectOwnerAdapter.toDomain(projectOwnerRepository.save(ProjectOwnerAdapter.toEntity(projectOwner)));

    }

    @Override
    public Optional<CrowdfundingProject> findById(ProjectId id) {
        return crowdfundingProjectRepository.findById(UUID.fromString(id.id()))
                                            .map(CrowdfundingProjectAdapter::toDomain);
    }

    @Override
    public Stream<CrowdfundingProject> findByStatusesOrderByAsc(Set<CrowdfundingProject.Status> statuses, ProjectId startingFrom) {
        final int batchSize = 20;
        List<String> statusesString = statuses.stream().map(Enum::name).toList();
        return Stream.iterate(
                             Optional.ofNullable(startingFrom)
                                     .map(ProjectId::id)
                                     .map(UUID::fromString)
                                     .map(cursor -> crowdfundingProjectRepository.findByStatusInOrderByIdAsc(statusesString, cursor, batchSize))
                                     .orElseGet(() -> crowdfundingProjectRepository.findByStatusInOrderByIdAsc(statusesString, batchSize)),
                             Predicate.not(List::isEmpty), l -> {
                                 UUID lastId = l.getLast().getId();
                                 return crowdfundingProjectRepository.findByStatusInOrderByIdAsc(statusesString, lastId, batchSize);
                             })
                     .flatMap(Collection::stream)
                     .map(CrowdfundingProjectAdapter::toDomain);

    }

    @Override
    public Stream<Investment> findInvestmentsByStatusesOrderByDesc(ProjectId projectId, InvestmentId startingFrom, Set<InvestmentStatus> investmentStatuses) {
        final int batchSize = 20;
        UUID projectUUID = UUID.fromString(projectId.id());
        List<String> statusesString = investmentStatuses.stream().map(Enum::name).toList();
        return Stream.iterate(
                             Optional.ofNullable(startingFrom)
                                     .map(InvestmentId::id)
                                     .map(UUID::fromString)
                                     .map(cursor -> investmentRepository.findInvestmentsByStatusesOrderByDesc(projectUUID, statusesString, cursor, batchSize))
                                     .orElseGet(() -> investmentRepository.findInvestmentsByStatusesOrderByDesc(projectUUID, statusesString, batchSize)),
                             l -> !l.isEmpty(),
                             l -> investmentRepository.findInvestmentsByStatusesOrderByDesc(
                                     projectUUID,
                                     statusesString,
                                     l.getLast().getId(),
                                     batchSize))
                     .flatMap(Collection::stream)
                     .map(InvestmentAdapter::toDomain);
    }


    @Override
    public Optional<ProjectOwner> findOwnerById(String id) {
        return projectOwnerRepository.findById(UUID.fromString(id))
                                     .map(ProjectOwnerAdapter::toDomain);
    }

    @Override
    public Set<TimelineEvent> findTimelineEvents(ProjectId projectId) {
        return projectTimelineRepository.findByProjectId(ProjectIdAdapter.toEntity(projectId))
                                        .map(ProjectTimelineEntity::getEvents)
                                        .map(events -> events
                                                .stream()
                                                .map(TimelineEventAdapter::toDomain)
                                                .collect(Collectors.toSet()))
                                        .orElseGet(Set::of);

    }

    @Override
    public void saveTimelineEvents(ProjectId projectId, List<TimelineEvent> events) {
        ProjectTimelineEntity timelineEntity = projectTimelineRepository.findByProjectId(ProjectIdAdapter.toEntity(projectId))
                                                                        .orElseGet(() -> ProjectTimelineEntity.builder().projectId(ProjectIdAdapter.toEntity(projectId)).build());
        timelineEntity.setEvents(events.stream().map(timelineEvent -> TimelineEventAdapter.toEntity(timelineEvent, timelineEntity.getId())).collect(Collectors.toSet()));
        projectTimelineRepository.save(timelineEntity);
    }

}
