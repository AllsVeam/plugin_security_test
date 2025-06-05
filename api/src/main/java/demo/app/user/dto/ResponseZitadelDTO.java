package demo.app.user.dto;

import lombok.Data;

@Data
public class ResponseZitadelDTO {
    private DetailsDto details;
    private UserZitadelDto[] user;

}
