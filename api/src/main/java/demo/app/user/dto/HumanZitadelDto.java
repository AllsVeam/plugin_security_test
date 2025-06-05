package demo.app.user.dto;

import lombok.Data;
import lombok.ToString;

import java.time.OffsetDateTime;

@Data
@ToString
public class HumanZitadelDto {
    private ProfileZitadelDto profile;
    private EmailZitadelDto email;
    private PhoneZitadelDto phone;
    private String passwordChanged;
}
