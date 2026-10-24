package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.TestDataValueInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import org.springframework.stereotype.Component;

@Component
class TestDataValueInstructionCopyVariant
        extends
            AbstractValueComputationInstructionCopyVariant<TestDataValueInstructionEntity> {

    TestDataValueInstructionCopyVariant() {
        super(TestDataValueInstructionEntity.class);
    }

    @Override
    protected ValueComputationInstructionEntity copyInstruction(TestDataValueInstructionEntity instruction) {
        return new TestDataValueInstructionEntity(instruction.getTestDataId(), instruction.getTestDataKey());
    }

}
