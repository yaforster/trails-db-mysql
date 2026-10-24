package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant.ActionEntityVariantMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

@Component
public class ActionEntityMapper {

    private final PositionEntityMapper positionEntityMapper;
    private final ActionEntityMapperRegistry registry;

    public ActionEntityMapper(PositionEntityMapper positionEntityMapper,
            List<ActionEntityVariantMapper> variantMappers) {
        this.positionEntityMapper = positionEntityMapper;
        this.registry = new ActionEntityMapperRegistry(variantMappers);
    }

    public ActionEntity toEntity(Action action, Long applicationId, Long stageId) {
        List<Long> nextActions = mapNextActions(action);
        PositionEntity position = positionEntityMapper.toEntity(action.getPosition());
        ActionDetailsEntity details = mapDetails(action, applicationId, stageId);
        ActionEntity entity = new ActionEntity();
        entity.setActionId(action.getActionID());
        entity.setNextActions(nextActions);
        entity.setLabel(action.getLabel());
        entity.setPosition(position);
        entity.setDetails(details);
        return entity;
    }

    private List<Long> mapNextActions(Action action) {
        if (action.getNextActions() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(action.getNextActions());
    }

    private ActionDetailsEntity mapDetails(Action action, Long applicationId, Long stageId) {
        ActionEntityVariantMapper mapper = registry.forAction(action.getClass());
        return mapper.toEntity(action, applicationId, stageId);
    }

    public Action fromEntity(ActionEntity entity) {
        return fromEntity(entity, UnaryOperator.identity());
    }

    public Action fromEntity(ActionEntity entity, UnaryOperator<ValueComputationInstruction> instructionResolver) {
        ActionEntity detachedEntity = toDetachedEntity(entity);
        ActionDetailsEntity details = detachedEntity.getDetails();
        ActionEntityVariantMapper mapper = registry.forDetails(details.getClass());
        return mapper.fromEntity(detachedEntity, details, instructionResolver);
    }

    private ActionEntity toDetachedEntity(ActionEntity entity) {
        // Domain mapping must not replace next-action links on a JPA-managed entity.
        List<Long> nextActions;
        if (entity.getNextActions() == null) {
            nextActions = List.of();
        } else {
            nextActions = new ArrayList<>(entity.getNextActions());
        }
        ActionEntity detachedEntity = new ActionEntity();
        detachedEntity.setId(entity.getId());
        detachedEntity.setActionId(entity.getActionId());
        detachedEntity.setTestPlanId(entity.getTestPlanId());
        detachedEntity.setLabel(entity.getLabel());
        detachedEntity.setDetails(entity.getDetails());
        detachedEntity.setPosition(entity.getPosition());
        detachedEntity.setNextActions(nextActions);
        return detachedEntity;
    }

}
