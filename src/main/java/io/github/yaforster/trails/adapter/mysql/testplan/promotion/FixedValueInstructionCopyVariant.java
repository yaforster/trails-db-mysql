package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.FixedValueEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import org.springframework.stereotype.Component;

@Component
class FixedValueInstructionCopyVariant extends AbstractValueComputationInstructionCopyVariant<FixedValueEntity> {

    FixedValueInstructionCopyVariant() {
        super(FixedValueEntity.class);
    }

    @Override
    protected ValueComputationInstructionEntity copyInstruction(FixedValueEntity instruction) {
        return new FixedValueEntity(instruction.getValue());
    }

}
