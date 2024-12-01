package nextcrowd.crowdfunding.infrastructure.project.persistence.adapter;

import java.util.UUID;

import nextcrowd.crowdfunding.infrastructure.project.persistence.ProjectTimelineEventEntity;
import nextcrowd.crowdfunding.project.model.TimelineEvent;
import nextcrowd.crowdfunding.project.model.TimelineEventId;

public class TimelineEventAdapter {

    public static TimelineEvent toDomain(ProjectTimelineEventEntity projectTimelineEventEntity) {
        return new TimelineEvent(
                new TimelineEventId(projectTimelineEventEntity.getId().toString()),
                projectTimelineEventEntity.getEventDate(),
                projectTimelineEventEntity.getDescription(),
                projectTimelineEventEntity.getTitle()
        );
    }
    public static ProjectTimelineEventEntity toEntity(TimelineEvent timelineEvent, UUID timelineId) {
        return new ProjectTimelineEventEntity(
                UUID.fromString(timelineEvent.getId().id()),
                timelineId,
                timelineEvent.getDate(),
                timelineEvent.getDescription(),
                timelineEvent.getTitle()
        );
    }

}
