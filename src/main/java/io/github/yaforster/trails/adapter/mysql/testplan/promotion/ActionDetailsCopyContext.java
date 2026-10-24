package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.app.services.ElementDatabaseService;

import java.util.Optional;

public record ActionDetailsCopyContext(ElementDatabaseService elementDatabaseService,
        ValueComputationInstructionCopyService instructionCopyService, Long applicationId, Long targetStageId) {

    public Optional<Long> targetElementId(Long sourceElementId) {
        ElementDatabaseService.ElementPromotionTarget target = new ElementDatabaseService.ElementPromotionTarget(
                applicationId, sourceElementId, targetStageId);
        return elementDatabaseService.findPromotionTargetElementId(target);
    }

    public ValueComputationInstructionEntity copyValueComputation(ValueComputationInstructionEntity instruction) {
        return instructionCopyService.copy(instruction);
    }

}
