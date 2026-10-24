package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.LocalStorageSearchDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class LocalStorageSearchDetailsCopyVariant extends AbstractActionDetailsCopyVariant<LocalStorageSearchDetailsEntity> {

    LocalStorageSearchDetailsCopyVariant() {
        super(LocalStorageSearchDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(LocalStorageSearchDetailsEntity details,
            ActionDetailsCopyContext context) {
        ValueComputationInstructionEntity instruction = context.copyValueComputation(details.getValueComputation());
        LocalStorageSearchDetailsEntity copy = new LocalStorageSearchDetailsEntity(details.getStorageKey(),
                instruction);
        return Optional.of(copy);
    }

}
