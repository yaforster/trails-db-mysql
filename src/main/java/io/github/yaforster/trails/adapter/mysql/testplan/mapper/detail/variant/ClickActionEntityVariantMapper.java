package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ClickActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.ClickDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.element.ClickAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class ClickActionEntityVariantMapper extends AbstractActionEntityVariantMapper<ClickAction, ClickActionDetailsEntity> {

    private final ClickDetailsMapper mapper;

    ClickActionEntityVariantMapper(ClickDetailsMapper mapper) {
        super(ClickAction.class, ClickActionDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(ClickAction action, Long applicationId, Long stageId) {
        return mapper.toEntity(action, applicationId, stageId);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, ClickActionDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details);
    }

}
