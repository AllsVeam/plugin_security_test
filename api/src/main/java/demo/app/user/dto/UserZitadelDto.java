package demo.app.user.dto;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class UserZitadelDto {
    private String id;
    private UserZitadelDetailDto userZitadelDetailDto;
    private String state;
    private String userName;
    private String[] loginNames;
    private String preferredLoginName;
    private HumanZitadelDto human;

}
