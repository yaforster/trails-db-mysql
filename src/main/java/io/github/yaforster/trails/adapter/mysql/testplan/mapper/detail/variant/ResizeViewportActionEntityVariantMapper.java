package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ResizeViewportDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.ResizeViewportDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.browser.ResizeViewportAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class ResizeViewportActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<ResizeViewportAction, ResizeViewportDetailsEntity> {

    private final ResizeViewportDetailsMapper mapper;

    ResizeViewportActionEntityVariantMapper(ResizeViewportDetailsMapper mapper) {
        super(ResizeViewportAction.class, ResizeViewportDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(ResizeViewportAction action) {
        return mapper.toEntity(action);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, ResizeViewportDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details);
    }

}
