package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SessionStorageSearchDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class SessionStorageSearchDetailsCopyVariant
        extends
            AbstractActionDetailsCopyVariant<SessionStorageSearchDetailsEntity> {

    SessionStorageSearchDetailsCopyVariant() {
        super(SessionStorageSearchDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(SessionStorageSearchDetailsEntity details,
            ActionDetailsCopyContext context) {
        ValueComputationInstructionEntity instruction = context.copyValueComputation(details.getValueComputation());
        SessionStorageSearchDetailsEntity copy = new SessionStorageSearchDetailsEntity(details.getStorageKey(),
                instruction);
        return Optional.of(copy);
    }

}
