package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ElementValueCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.ElementValueCheckDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.element.ElementValueCheckAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class ElementValueCheckActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<ElementValueCheckAction, ElementValueCheckDetailsEntity> {

    private final ElementValueCheckDetailsMapper mapper;

    ElementValueCheckActionEntityVariantMapper(ElementValueCheckDetailsMapper mapper) {
        super(ElementValueCheckAction.class, ElementValueCheckDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(ElementValueCheckAction action, Long applicationId, Long stageId) {
        return mapper.toEntity(action, applicationId, stageId);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, ElementValueCheckDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details, instructionResolver);
    }

}
