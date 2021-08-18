package epam.az.worker.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
public class ReportForm {

    @Min(value = 1)
    private Long workerId;
    private String date;

    @NotBlank
    private String description;

    @Min(value = 1)
    @Max(value = 24)
    private Integer hours;
}
