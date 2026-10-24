package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SelectionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.SelectDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.element.SelectAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class SelectionActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<SelectAction, SelectionDetailsEntity> {

    private final SelectDetailsMapper mapper;

    SelectionActionEntityVariantMapper(SelectDetailsMapper mapper) {
        super(SelectAction.class, SelectionDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(SelectAction action, Long applicationId, Long stageId) {
        return mapper.toEntity(action, applicationId, stageId);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, SelectionDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details, instructionResolver);
    }

}
