package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.RelativeDateInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import org.springframework.stereotype.Component;

@Component
class RelativeDateInstructionCopyVariant
        extends
            AbstractValueComputationInstructionCopyVariant<RelativeDateInstructionEntity> {

    RelativeDateInstructionCopyVariant() {
        super(RelativeDateInstructionEntity.class);
    }

    @Override
    protected ValueComputationInstructionEntity copyInstruction(RelativeDateInstructionEntity instruction) {
        return new RelativeDateInstructionEntity(instruction.getOffsetDays(), instruction.getFormatPattern());
    }

}
