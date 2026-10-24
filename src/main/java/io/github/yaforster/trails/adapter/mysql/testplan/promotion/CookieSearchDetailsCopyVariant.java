package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.CookieSearchDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class CookieSearchDetailsCopyVariant extends AbstractActionDetailsCopyVariant<CookieSearchDetailsEntity> {

    CookieSearchDetailsCopyVariant() {
        super(CookieSearchDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(CookieSearchDetailsEntity details,
            ActionDetailsCopyContext context) {
        ValueComputationInstructionEntity instruction = context.copyValueComputation(details.getValueComputation());
        CookieSearchDetailsEntity copy = new CookieSearchDetailsEntity(details.getCookieName(), instruction);
        return Optional.of(copy);
    }

}
