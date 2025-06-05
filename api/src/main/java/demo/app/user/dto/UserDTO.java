package demo.app.user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserDTO {
    @NotBlank(message = "Username is required")
    public String username;

    @Email(message = "Invalid email")
    public String email;

    @NotBlank(message = "givenName is required")
    public String givenName;

    @NotBlank(message = "familyName is required")
    public String familyName;

    @NotBlank(message = "password is required")
    @Size(min = 8, message = "Password must be at least 6 characters")
    public String password;

    public UserDTO() {}

    public UserDTO(String email, String familyName, String givenName, String password, String username) {
        this.email = email;
        this.familyName = familyName;
        this.givenName = givenName;
        this.password = password;
        this.username = username;
    }

}
