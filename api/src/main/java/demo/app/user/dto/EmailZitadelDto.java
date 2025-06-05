package demo.app.user.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmailZitadelDto {
    private String email;
    private boolean isEmailVerified;
}
