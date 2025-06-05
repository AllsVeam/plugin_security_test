package demo.app.user.dto;

import lombok.Data;
import lombok.ToString;

import java.time.OffsetDateTime;

@Data
@ToString
public class DetailsDto {
    private String totalResult;
    private String viewTimestamp;
}
