package nextcrowd.crowdfunding.infrastructure.project.persistence;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "project_timeline")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProjectTimelineEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "project_id")
    private UUID projectId;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_timeline_id")  // Define the foreign key in the investment table
    //@JsonIgnoreProperties(value = {"crowdfundingProject" }, allowSetters = true)
    private Set<ProjectTimelineEventEntity> events;

}
