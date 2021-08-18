package epam.az.worker.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.validation.constraints.NotNull;

@Entity
@Data
@ToString(exclude = {"id", "shop", "isActive"})
@NoArgsConstructor
@AllArgsConstructor
public class Worker {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Position position;

    @NotNull
    private String fullName;

    @NotNull
    private boolean isActive;

    @NotNull
    private String zone;
    private String shop;
}
