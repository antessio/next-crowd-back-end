package nextcrowd.crowdfunding.project.command;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TimelineEventCommand {
    String title;
    String description;
    LocalDate date;

}
