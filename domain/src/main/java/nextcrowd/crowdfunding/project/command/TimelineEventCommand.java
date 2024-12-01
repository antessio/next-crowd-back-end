package nextcrowd.crowdfunding.project.command;

import java.time.LocalDate;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import nextcrowd.crowdfunding.project.model.TimelineEventId;

@Value
@Builder
@AllArgsConstructor
public class TimelineEventCommand {
    TimelineEventId timelineEventId;
    String title;
    String description;
    LocalDate date;

    public Optional<TimelineEventId> getId(){
        return Optional.ofNullable(timelineEventId);
    }

}
