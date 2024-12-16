package nextcrowd.crowdfunding.infrastructure.security.persistence;


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
@Table(name = "roles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    public static final String PROJECT_OWNER = "ROLE_PROJECT";
    public static final String BAKER = "ROLE_BAKER";
    public static final String ADMIN = "ROLE_ADMIN";

    @Id
    @Column(name="id", nullable = false)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String role;

}