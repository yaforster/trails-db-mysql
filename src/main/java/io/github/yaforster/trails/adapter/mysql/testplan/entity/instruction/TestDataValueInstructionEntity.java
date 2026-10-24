package io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("TEST_DATA")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TestDataValueInstructionEntity extends ValueComputationInstructionEntity {

    private Long testDataId;
    private String testDataKey;

}
