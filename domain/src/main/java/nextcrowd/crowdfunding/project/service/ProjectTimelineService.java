package nextcrowd.crowdfunding.project.service;

import java.util.Comparator;
import java.util.List;

import nextcrowd.crowdfunding.project.command.TimelineEventCommand;
import nextcrowd.crowdfunding.project.model.Timeline;
import nextcrowd.crowdfunding.project.model.TimelineEvent;
import nextcrowd.crowdfunding.project.model.TimelineEventId;
import nextcrowd.crowdfunding.project.port.CrowdfundingProjectRepository;

public class ProjectTimelineService {
    private final CrowdfundingProjectRepository repository;

    public ProjectTimelineService(CrowdfundingProjectRepository repository) {
        this.repository = repository;
    }

    public void updateTimeline(Timeline existingTimeline, List<TimelineEvent> events) {
        repository.saveTimelineEvents(
                existingTimeline.getProjectId(),
                events);
    }

    public  TimelineEvent createNewEvent(TimelineEventCommand event) {
        return new TimelineEvent(
                event.getId().orElseGet(TimelineEventId::generate),
                event.getDate(),
                event.getDescription(),
                event.getTitle());
    }

    public List<TimelineEvent> remove(TimelineEventId timelineEventId, Timeline existingTimeline) {
        return existingTimeline.getEvents()
                               .stream()
                               .filter(e -> !e.getId().equals(timelineEventId))
                               .sorted(Comparator.comparing(TimelineEvent::getId).reversed())
                               .toList();
    }
    public List<TimelineEvent> replace(TimelineEventId timelineEventId, TimelineEventCommand event, Timeline existingTimeline) {
        return existingTimeline.getEvents()
                               .stream()
                               .map(e -> e.getId().equals(timelineEventId) ? this.updateEvent(e.getId(), event) : e)
                               .sorted(Comparator.comparing(TimelineEvent::getId).reversed())
                               .toList();
    }

    private TimelineEvent updateEvent(TimelineEventId id, TimelineEventCommand event) {
        return new TimelineEvent(
                id,
                event.getDate(),
                event.getDescription(),
                event.getTitle());
    }
}
