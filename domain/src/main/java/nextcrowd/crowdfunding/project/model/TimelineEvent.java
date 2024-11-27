package nextcrowd.crowdfunding.project.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Builder(toBuilder = true)
@Value
public class TimelineEvent {
    private TimelineEventId id;
    private LocalDate date;
    private String description;
    private String title;

}
