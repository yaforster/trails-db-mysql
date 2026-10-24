package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.TextCheckDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class TextCheckDetailsCopyVariant extends AbstractActionDetailsCopyVariant<TextCheckDetailsEntity> {

    TextCheckDetailsCopyVariant() {
        super(TextCheckDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(TextCheckDetailsEntity details,
            ActionDetailsCopyContext context) {
        return context.targetElementId(details.getElementID())
                      .map(targetElementId -> {
                          ValueComputationInstructionEntity instruction = context.copyValueComputation(
                                  details.getValueComputation());
                          return new TextCheckDetailsEntity(targetElementId, instruction);
                      });
    }

}
