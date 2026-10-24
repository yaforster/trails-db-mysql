package io.github.yaforster.trails.adapter.mysql.testplan.mapper;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanGroupEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.ActionEntityMapper;
import io.github.yaforster.trails.core.data.TestPlan;
import io.github.yaforster.trails.core.definition.TestPlanDefinition;
import io.github.yaforster.trails.core.definition.TestPlanGroup;
import io.github.yaforster.trails.core.persisted.PersistedTestPlan;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class TestPlanEntityMapper {

    private final ActionEntityMapper actionMapper;

    public TestPlan fromEntity(TestPlanEntity entity) {
        LinkedList<Action> actions = entity.getTestSteps()
                                           .stream()
                                           .map(actionMapper::fromEntity)
                                           .collect(Collectors.toCollection(LinkedList::new));
        List<TestPlanGroup> groups = mapGroups(entity.getGroups());
        return new TestPlan(entity.getId(), entity.getApplicationId(), entity.getStageId(), entity.getLabel(), actions,
                groups);
    }

    public TestPlan fromEntity(TestPlanEntity entity, UnaryOperator<ValueComputationInstruction> instructionResolver) {
        LinkedList<Action> actions = entity.getTestSteps()
                                           .stream()
                                           .map(action -> actionMapper.fromEntity(action, instructionResolver))
                                           .collect(Collectors.toCollection(LinkedList::new));
        List<TestPlanGroup> groups = mapGroups(entity.getGroups());
        return new TestPlan(entity.getId(), entity.getApplicationId(), entity.getStageId(), entity.getLabel(), actions,
                groups);
    }

    public TestPlanEntity toEntity(TestPlan testPlan) {
        TestPlanEntity entity = new TestPlanEntity();
        entity.setId(null);
        entity.setApplicationId(testPlan.getApplicationID());
        entity.setStageId(testPlan.getStageID());
        entity.setLabel(testPlan.getLabel());
        List<ActionEntity> actionEntities = testPlan.getActions()
                                                    .stream()
                                                    .map(action -> actionMapper.toEntity(action,
                                                            testPlan.getApplicationID(), testPlan.getStageID()))
                                                    .collect(Collectors.toCollection(LinkedList::new));
        entity.setTestSteps(actionEntities);
        List<TestPlanGroupEntity> groups = mapGroupsToEntities(testPlan.getGroups());
        entity.setGroups(groups);
        return entity;
    }

    public TestPlanEntity toEntity(TestPlanDefinition definition) {
        TestPlanEntity entity = new TestPlanEntity();
        entity.setId(null);
        entity.setApplicationId(definition.applicationID());
        entity.setStageId(definition.stageID());
        entity.setLabel(definition.label());
        List<ActionEntity> actionEntities = definition.actions()
                                                      .stream()
                                                      .map(action -> actionMapper.toEntity(action,
                                                              definition.applicationID(), definition.stageID()))
                                                      .collect(Collectors.toCollection(ArrayList::new));
        entity.setTestSteps(actionEntities);
        List<TestPlanGroupEntity> groups = mapGroupsToEntities(definition.groups());
        entity.setGroups(groups);
        return entity;
    }

    private List<TestPlanGroup> mapGroups(List<TestPlanGroupEntity> groups) {
        if (groups == null) {
            return List.of();
        }
        return groups.stream()
                     .map(this::toGroup)
                     .toList();
    }

    private TestPlanGroup toGroup(TestPlanGroupEntity group) {
        List<Long> references = Optional.ofNullable(group.getActionReferenceIds())
                                        .orElseGet(List::of);
        return new TestPlanGroup(group.getLabel(), group.getXCoordinatePixels(), group.getYCoordinatePixels(),
                group.getWidthPixels(), group.getHeightPixels(), references);
    }

    private List<TestPlanGroupEntity> mapGroupsToEntities(List<TestPlanGroup> groups) {
        if (groups == null) {
            return new ArrayList<>();
        }
        return groups.stream()
                     .map(this::toGroupEntity)
                     .collect(Collectors.toCollection(ArrayList::new));
    }

    private TestPlanGroupEntity toGroupEntity(TestPlanGroup group) {
        List<Long> references = Optional.ofNullable(group.actionReferenceIds())
                                        .map(ArrayList::new)
                                        .orElseGet(ArrayList::new);
        return new TestPlanGroupEntity().setLabel(group.label())
                                        .setXCoordinatePixels(group.xCoordinate())
                                        .setYCoordinatePixels(group.yCoordinate())
                                        .setWidthPixels(group.widthPixels())
                                        .setHeightPixels(group.heightPixels())
                                        .setActionReferenceIds(references);
    }

    public PersistedTestPlan mapEntityToPersistedTestPlan(TestPlanEntity entity) {
        List<TestPlanGroup> groups = mapGroups(entity.getGroups());
        return new PersistedTestPlan(entity.getId(), entity.getApplicationId(), entity.getStageId(), entity.getLabel(),
                entity.isRetired(), groups);
    }

}
