package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ClickActionDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class ClickDetailsCopyVariant extends AbstractActionDetailsCopyVariant<ClickActionDetailsEntity> {

    ClickDetailsCopyVariant() {
        super(ClickActionDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(ClickActionDetailsEntity details,
            ActionDetailsCopyContext context) {
        return context.targetElementId(details.getElementID())
                      .map(ClickActionDetailsEntity::new);
    }

}
