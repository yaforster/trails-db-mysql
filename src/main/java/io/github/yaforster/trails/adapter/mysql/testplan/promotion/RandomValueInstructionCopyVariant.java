package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.RandomValueInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import org.springframework.stereotype.Component;

@Component
class RandomValueInstructionCopyVariant
        extends
            AbstractValueComputationInstructionCopyVariant<RandomValueInstructionEntity> {

    RandomValueInstructionCopyVariant() {
        super(RandomValueInstructionEntity.class);
    }

    @Override
    protected ValueComputationInstructionEntity copyInstruction(RandomValueInstructionEntity instruction) {
        return new RandomValueInstructionEntity(instruction.getPrefix(), instruction.getRandomStringLength(),
                instruction.getCharPool(), instruction.getSuffix());
    }

}
