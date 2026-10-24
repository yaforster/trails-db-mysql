package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanGroupEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.promotion.ActionDetailsCopyService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class TestPlanPromotionService {

    private static final String DUPLICATE_SOURCE_ACTION = "Duplicate source action ID in promoted test plan: %s";
    private final ActionDetailsCopyService actionDetailsCopyService;

    public Optional<TestPlanCopy> copyToStage(TestPlanEntity source, Long targetStageId) {
        if (!hasOnlySourceActionReferences(source)) {
            return Optional.empty();
        }
        Optional<Map<Long, ActionEntity>> copiedActions = copyActions(source, targetStageId);
        if (copiedActions.isEmpty()) {
            return Optional.empty();
        }
        List<TestPlanGroupEntity> copiedGroups = copyGroups(source.getGroups());
        Map<Long, ActionEntity> actionsBySourceId = copiedActions.get();
        List<ActionEntity> testSteps = new ArrayList<>(actionsBySourceId.values());
        TestPlanEntity copy = new TestPlanEntity().setApplicationId(source.getApplicationId())
                                                  .setStageId(targetStageId)
                                                  .setLabel(source.getLabel())
                                                  .setRetired(false)
                                                  .setTestSteps(testSteps)
                                                  .setGroups(copiedGroups);
        TestPlanCopy testPlanCopy = new TestPlanCopy(copy, actionsBySourceId);
        return Optional.of(testPlanCopy);
    }

    private Optional<Map<Long, ActionEntity>> copyActions(TestPlanEntity source, Long targetStageId) {
        Map<Long, ActionEntity> copiedActions = new LinkedHashMap<>();
        for (ActionEntity action : source.getTestSteps()) {
            Optional<ActionEntity> copiedAction = copyAction(action, source.getApplicationId(), targetStageId);
            if (copiedAction.isEmpty()) {
                return Optional.empty();
            }
            Long sourceId = action.getId();
            ActionEntity previous = copiedActions.putIfAbsent(sourceId, copiedAction.get());
            if (previous != null) {
                String message = DUPLICATE_SOURCE_ACTION.formatted(sourceId);
                throw new IllegalStateException(message);
            }
        }
        return Optional.of(copiedActions);
    }

    private List<TestPlanGroupEntity> copyGroups(List<TestPlanGroupEntity> groups) {
        if (groups == null) {
            return new ArrayList<>();
        }
        return groups.stream()
                     .map(this::copyGroup)
                     .collect(Collectors.toCollection(ArrayList::new));
    }

    private boolean hasOnlySourceActionReferences(TestPlanEntity source) {
        Set<Long> sourceActionIds = source.getTestSteps()
                                          .stream()
                                          .map(ActionEntity::getId)
                                          .collect(Collectors.toSet());
        for (ActionEntity action : source.getTestSteps()) {
            List<Long> nextActionIds = action.getNextActions();
            if (nextActionIds != null && !sourceActionIds.containsAll(nextActionIds)) {
                return false;
            }
        }
        return true;
    }

    private Optional<ActionEntity> copyAction(ActionEntity action, Long applicationId, Long targetStageId) {
        ActionDetailsEntity sourceDetails = action.getDetails();
        Optional<ActionDetailsEntity> copiedDetails = actionDetailsCopyService.copy(sourceDetails, applicationId,
                targetStageId);
        if (copiedDetails.isEmpty()) {
            return Optional.empty();
        }
        PositionEntity copiedPosition = copyPosition(action.getPosition());
        List<Long> copiedNextActions = copyReferences(action.getNextActions());
        ActionEntity copy = new ActionEntity().setActionId(action.getActionId())
                                              .setLabel(action.getLabel())
                                              .setDetails(copiedDetails.get())
                                              .setPosition(copiedPosition)
                                              .setNextActions(copiedNextActions);
        return Optional.of(copy);
    }

    private List<Long> copyReferences(List<Long> references) {
        if (references == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(references);
    }

    private PositionEntity copyPosition(PositionEntity position) {
        return new PositionEntity().setXCoordinatePixels(position.getXCoordinatePixels())
                                   .setYCoordinatePixels(position.getYCoordinatePixels());
    }

    private TestPlanGroupEntity copyGroup(TestPlanGroupEntity group) {
        List<Long> copiedReferences = copyReferences(group.getActionReferenceIds());
        return new TestPlanGroupEntity().setLabel(group.getLabel())
                                        .setXCoordinatePixels(group.getXCoordinatePixels())
                                        .setYCoordinatePixels(group.getYCoordinatePixels())
                                        .setWidthPixels(group.getWidthPixels())
                                        .setHeightPixels(group.getHeightPixels())
                                        .setActionReferenceIds(copiedReferences);
    }

}
