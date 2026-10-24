package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.CoordinateClickDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class CoordinateClickDetailsCopyVariant extends AbstractActionDetailsCopyVariant<CoordinateClickDetailsEntity> {

    CoordinateClickDetailsCopyVariant() {
        super(CoordinateClickDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(CoordinateClickDetailsEntity details,
            ActionDetailsCopyContext context) {
        return Optional.of(new CoordinateClickDetailsEntity(details.getXCoordinate(), details.getYCoordinate()));
    }

}
