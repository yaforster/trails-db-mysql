package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ResizeViewportDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class ResizeViewportDetailsCopyVariant extends AbstractActionDetailsCopyVariant<ResizeViewportDetailsEntity> {

    ResizeViewportDetailsCopyVariant() {
        super(ResizeViewportDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(ResizeViewportDetailsEntity details,
            ActionDetailsCopyContext context) {
        return Optional.of(new ResizeViewportDetailsEntity(details.getViewportWidth(), details.getViewportHeight()));
    }

}
