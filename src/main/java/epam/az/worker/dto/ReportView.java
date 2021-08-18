package epam.az.worker.dto;

import lombok.*;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "createdAt")
public class ReportView {

    private Long workerId;
    private LocalDate date;
    private String workDescription;
    private Integer hours;
    private String createdAt;
}
