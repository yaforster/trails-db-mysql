package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SwitchWebsiteDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class SwitchWebsiteDetailsCopyVariant extends AbstractActionDetailsCopyVariant<SwitchWebsiteDetailsEntity> {

    SwitchWebsiteDetailsCopyVariant() {
        super(SwitchWebsiteDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(SwitchWebsiteDetailsEntity details,
            ActionDetailsCopyContext context) {
        ValueComputationInstructionEntity instruction = context.copyValueComputation(details.getValueComputation());
        SwitchWebsiteDetailsEntity copy = new SwitchWebsiteDetailsEntity(instruction);
        return Optional.of(copy);
    }

}
