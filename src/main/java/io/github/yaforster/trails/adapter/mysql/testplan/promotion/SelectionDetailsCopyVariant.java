package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SelectionDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class SelectionDetailsCopyVariant extends AbstractActionDetailsCopyVariant<SelectionDetailsEntity> {

    SelectionDetailsCopyVariant() {
        super(SelectionDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(SelectionDetailsEntity details,
            ActionDetailsCopyContext context) {
        return context.targetElementId(details.getElementID())
                      .map(targetElementId -> {
                          ValueComputationInstructionEntity instruction = context.copyValueComputation(
                                  details.getValueComputation());
                          return new SelectionDetailsEntity(targetElementId, instruction);
                      });
    }

}
