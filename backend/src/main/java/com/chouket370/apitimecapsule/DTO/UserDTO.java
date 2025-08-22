package com.chouket370.apitimecapsule.DTO;





import com.chouket370.apitimecapsule.models.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String email;
    private String username;
    private UserRole role;
}