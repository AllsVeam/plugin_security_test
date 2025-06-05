package demo.app.user.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserZitadelDetailDto {
    private String sequence;
    private String creationDate;
    private String changeDate;
    private String resourceOwner;
}
