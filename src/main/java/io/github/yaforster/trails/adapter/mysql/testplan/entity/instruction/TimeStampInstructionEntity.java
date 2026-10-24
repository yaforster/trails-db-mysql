package io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("TIMESTAMP_NOW")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TimeStampInstructionEntity extends ValueComputationInstructionEntity {

    private String formatPattern;

}
