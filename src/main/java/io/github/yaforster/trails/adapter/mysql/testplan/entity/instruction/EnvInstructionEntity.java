package io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("SYSTEM_VAR")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EnvInstructionEntity extends ValueComputationInstructionEntity {

    private String variableName;
    private String defaultValue;

}
