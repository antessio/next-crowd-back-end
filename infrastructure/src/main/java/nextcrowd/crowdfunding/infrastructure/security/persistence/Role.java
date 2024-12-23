package nextcrowd.crowdfunding.infrastructure.security.persistence;


import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Role {

    public static final String PROJECT_OWNER = "ROLE_PROJECT";
    public static final String BAKER = "ROLE_BAKER";
    public static final String ADMIN = "ROLE_ADMIN";

    @Id
    @Column(name="id", nullable = false)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore // To prevent circular serialization
    private User user;

    @Column(nullable = false)
    private String role;

}