package nextcrowd.crowdfunding.project.model;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

@AllArgsConstructor
@Builder(toBuilder = true)
@Value
public class Timeline {
    private ProjectId projectId;
    private Set<TimelineEvent> events;
}
