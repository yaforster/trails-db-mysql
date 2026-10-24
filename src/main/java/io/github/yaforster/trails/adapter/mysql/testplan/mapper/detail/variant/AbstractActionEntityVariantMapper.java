package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;

import java.util.function.UnaryOperator;

abstract class AbstractActionEntityVariantMapper<A extends Action, D extends ActionDetailsEntity>
        implements
            ActionEntityVariantMapper {

    private static final String MISSING_ACTION_CONTEXT = "Action type %s requires application and stage context";
    private final Class<A> actionType;
    private final Class<D> detailsType;

    protected AbstractActionEntityVariantMapper(Class<A> actionType, Class<D> detailsType) {
        this.actionType = actionType;
        this.detailsType = detailsType;
    }

    @Override
    public Class<? extends Action> actionType() {
        return actionType;
    }

    @Override
    public Class<? extends ActionDetailsEntity> detailsType() {
        return detailsType;
    }

    @Override
    public ActionDetailsEntity toEntity(Action action, Long applicationId, Long stageId) {
        A typedAction = actionType.cast(action);
        return toVariantEntity(typedAction, applicationId, stageId);
    }

    @Override
    public Action fromEntity(ActionEntity entity, ActionDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        D typedDetails = detailsType.cast(details);
        return fromVariantEntity(entity, typedDetails, instructionResolver);
    }

    protected ActionDetailsEntity toVariantEntity(A action, Long applicationId, Long stageId) {
        return toVariantEntity(action);
    }

    protected ActionDetailsEntity toVariantEntity(A action) {
        String message = MISSING_ACTION_CONTEXT.formatted(actionType.getName());
        throw new UnsupportedOperationException(message);
    }

    protected abstract Action fromVariantEntity(ActionEntity entity, D details,
            UnaryOperator<ValueComputationInstruction> instructionResolver);

}
