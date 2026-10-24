package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ViewportMoveDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class ViewportMoveDetailsCopyVariant extends AbstractActionDetailsCopyVariant<ViewportMoveDetailsEntity> {

    ViewportMoveDetailsCopyVariant() {
        super(ViewportMoveDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(ViewportMoveDetailsEntity details,
            ActionDetailsCopyContext context) {
        return Optional.of(new ViewportMoveDetailsEntity(details.getMovement(), details.getDirection(),
                details.getAmount(), details.getUnit(), details.getDelayAfterMoveMillis()));
    }

}
