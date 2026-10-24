package io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("RELATIVE_DATE")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RelativeDateInstructionEntity extends ValueComputationInstructionEntity {

    private int offsetDays;
    private String formatPattern;

}
