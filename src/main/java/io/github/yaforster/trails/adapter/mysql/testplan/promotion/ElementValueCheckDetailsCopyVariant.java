package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ElementValueCheckDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class ElementValueCheckDetailsCopyVariant extends AbstractActionDetailsCopyVariant<ElementValueCheckDetailsEntity> {

    ElementValueCheckDetailsCopyVariant() {
        super(ElementValueCheckDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(ElementValueCheckDetailsEntity details,
            ActionDetailsCopyContext context) {
        return context.targetElementId(details.getElementID())
                      .map(targetElementId -> {
                          ValueComputationInstructionEntity instruction = context.copyValueComputation(
                                  details.getValueComputation());
                          return new ElementValueCheckDetailsEntity(targetElementId, details.getValueSource(),
                                  details.getValueName(), instruction);
                      });
    }

}
