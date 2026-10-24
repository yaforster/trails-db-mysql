package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ViewportMoveDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.ViewportMoveDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.browser.viewport.MoveViewportAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class MoveViewportActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<MoveViewportAction, ViewportMoveDetailsEntity> {

    private final ViewportMoveDetailsMapper mapper;

    MoveViewportActionEntityVariantMapper(ViewportMoveDetailsMapper mapper) {
        super(MoveViewportAction.class, ViewportMoveDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(MoveViewportAction action) {
        return mapper.toEntity(action);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, ViewportMoveDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details);
    }

}
