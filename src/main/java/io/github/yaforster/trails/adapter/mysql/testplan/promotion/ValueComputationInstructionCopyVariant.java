package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;

public interface ValueComputationInstructionCopyVariant {

    Class<? extends ValueComputationInstructionEntity> instructionType();

    ValueComputationInstructionEntity copy(ValueComputationInstructionEntity instruction);

}
