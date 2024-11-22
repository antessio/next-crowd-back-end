package nextcrowd.crowdfunding.infrastructure.api.admin;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private String id;
    private String fullName;
    private String email;
    private Set<String> roles;
}
