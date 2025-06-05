package demo.app.user.dto;

import lombok.Data;

@Data
public class EmailZitadelDto {
    private String email;
    private boolean isEmailVerified;
}
