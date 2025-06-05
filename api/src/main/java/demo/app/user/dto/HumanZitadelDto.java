package demo.app.user.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class HumanZitadelDto {
    private ProfileZitadelDto profile;
    private EmailZitadelDto email;
    private PhoneZitadelDto phone;
    private OffsetDateTime passwordChanged;
}
