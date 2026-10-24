package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ExplicitWaitDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class ExplicitWaitDetailsCopyVariant extends AbstractActionDetailsCopyVariant<ExplicitWaitDetailsEntity> {

    ExplicitWaitDetailsCopyVariant() {
        super(ExplicitWaitDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(ExplicitWaitDetailsEntity details,
            ActionDetailsCopyContext context) {
        return Optional.of(new ExplicitWaitDetailsEntity(details.getDelayMillis()));
    }

}
