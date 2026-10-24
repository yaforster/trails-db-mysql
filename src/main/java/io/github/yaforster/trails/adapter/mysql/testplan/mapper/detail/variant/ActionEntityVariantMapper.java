package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;

import java.util.function.UnaryOperator;

public interface ActionEntityVariantMapper {

    Class<? extends Action> actionType();

    Class<? extends ActionDetailsEntity> detailsType();

    default boolean supports(Action action) {
        return actionType().isInstance(action);
    }

    default boolean supports(ActionDetailsEntity details) {
        return detailsType().isInstance(details);
    }

    ActionDetailsEntity toEntity(Action action, Long applicationId, Long stageId);

    Action fromEntity(ActionEntity entity, ActionDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver);

}
