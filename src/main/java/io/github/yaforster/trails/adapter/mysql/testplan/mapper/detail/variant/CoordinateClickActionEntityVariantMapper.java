package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.CoordinateClickDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.CoordinateClickDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.browser.CoordinateClickAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class CoordinateClickActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<CoordinateClickAction, CoordinateClickDetailsEntity> {

    private final CoordinateClickDetailsMapper mapper;

    CoordinateClickActionEntityVariantMapper(CoordinateClickDetailsMapper mapper) {
        super(CoordinateClickAction.class, CoordinateClickDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(CoordinateClickAction action) {
        return mapper.toEntity(action);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, CoordinateClickDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details);
    }

}
