package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.CheckExistenceActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.CheckExistenceDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.element.CheckExistenceAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class CheckExistenceActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<CheckExistenceAction, CheckExistenceActionDetailsEntity> {

    private final CheckExistenceDetailsMapper mapper;

    CheckExistenceActionEntityVariantMapper(CheckExistenceDetailsMapper mapper) {
        super(CheckExistenceAction.class, CheckExistenceActionDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(CheckExistenceAction action, Long applicationId, Long stageId) {
        return mapper.toEntity(action, applicationId, stageId);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, CheckExistenceActionDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details);
    }

}
