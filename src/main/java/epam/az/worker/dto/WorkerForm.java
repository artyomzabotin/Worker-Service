package epam.az.worker.dto;

import epam.az.worker.domain.Position;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotNull;

@Builder
@Getter
@Setter
public class WorkerForm {

    @NotNull
    private Position position;

    @NotNull
    private String fullName;
    private String shop;
    private boolean isActive;

    @NotNull
    private String zone;
}
