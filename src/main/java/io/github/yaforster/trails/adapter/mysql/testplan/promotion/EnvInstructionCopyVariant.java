package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.EnvInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import org.springframework.stereotype.Component;

@Component
class EnvInstructionCopyVariant extends AbstractValueComputationInstructionCopyVariant<EnvInstructionEntity> {

    EnvInstructionCopyVariant() {
        super(EnvInstructionEntity.class);
    }

    @Override
    protected ValueComputationInstructionEntity copyInstruction(EnvInstructionEntity instruction) {
        return new EnvInstructionEntity(instruction.getVariableName(), instruction.getDefaultValue());
    }

}
