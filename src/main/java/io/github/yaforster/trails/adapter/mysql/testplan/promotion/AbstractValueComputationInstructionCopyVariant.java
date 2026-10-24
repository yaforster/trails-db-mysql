package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;

abstract class AbstractValueComputationInstructionCopyVariant<I extends ValueComputationInstructionEntity>
        implements
            ValueComputationInstructionCopyVariant {

    private final Class<I> instructionType;

    protected AbstractValueComputationInstructionCopyVariant(Class<I> instructionType) {
        this.instructionType = instructionType;
    }

    @Override
    public Class<? extends ValueComputationInstructionEntity> instructionType() {
        return instructionType;
    }

    @Override
    public ValueComputationInstructionEntity copy(ValueComputationInstructionEntity instruction) {
        return copyInstruction(instructionType.cast(instruction));
    }

    protected abstract ValueComputationInstructionEntity copyInstruction(I instruction);

}
