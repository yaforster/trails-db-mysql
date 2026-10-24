package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.TextCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.TextCheckDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.element.TextCheckAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class TextCheckActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<TextCheckAction, TextCheckDetailsEntity> {

    private final TextCheckDetailsMapper mapper;

    TextCheckActionEntityVariantMapper(TextCheckDetailsMapper mapper) {
        super(TextCheckAction.class, TextCheckDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(TextCheckAction action, Long applicationId, Long stageId) {
        return mapper.toEntity(action, applicationId, stageId);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, TextCheckDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details, instructionResolver);
    }

}
