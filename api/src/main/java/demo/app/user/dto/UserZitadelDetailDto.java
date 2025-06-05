package demo.app.user.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserZitadelDetailDto {
    private String sequence;
    private OffsetDateTime creationDate;
    private OffsetDateTime changeDate;
    private String resourceOwner;
}
