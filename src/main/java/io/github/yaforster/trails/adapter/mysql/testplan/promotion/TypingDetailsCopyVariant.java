package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.TypingDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class TypingDetailsCopyVariant extends AbstractActionDetailsCopyVariant<TypingDetailsEntity> {

    TypingDetailsCopyVariant() {
        super(TypingDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(TypingDetailsEntity details, ActionDetailsCopyContext context) {
        return context.targetElementId(details.getElementID())
                      .map(targetElementId -> {
                          ValueComputationInstructionEntity instruction = context.copyValueComputation(
                                  details.getValueComputation());
                          return new TypingDetailsEntity(targetElementId, details.isClearBeforeTyping(),
                                  details.getDelayAfterClearMillis(), instruction);
                      });
    }

}
