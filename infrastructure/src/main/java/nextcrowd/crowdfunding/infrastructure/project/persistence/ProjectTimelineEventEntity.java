package nextcrowd.crowdfunding.infrastructure.project.persistence;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_timeline_event")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectTimelineEventEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "project_timeline_id")
    private UUID projectTimelineId;
    @Column(name = "event_date")
    private LocalDate eventDate;
    @Column(name = "event_description")
    private String description;
    @Column(name = "event_title")
    private String title;


}
