package io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("FIXED")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FixedValueEntity extends ValueComputationInstructionEntity {

    @Column(name = "fixed_value")
    private String value;

}
