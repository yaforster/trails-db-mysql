package io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RANDOM")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RandomValueInstructionEntity extends ValueComputationInstructionEntity {

    private String prefix;
    private int randomStringLength;
    private String charPool;
    private String suffix;

}
