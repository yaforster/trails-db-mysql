package io.github.yaforster.trails.adapter.mysql.testplan.mapper;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.ActionEntityMapper;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.data.TestPlan;
import io.github.yaforster.trails.core.definition.TestPlanDefinition;
import io.github.yaforster.trails.core.persisted.PersistedTestPlan;
import io.github.yaforster.trails.core.test.Action;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class TestPlanEntityMapperTest extends TrailsTest {

    private final ActionEntityMapper actionMapper = Mockito.mock(ActionEntityMapper.class);
    private final TestPlanEntityMapper testPlanEntityMapper = new TestPlanEntityMapper(actionMapper);

    @Test
    void fromEntity_ShouldMapTestPlanEntityToTestPlan() {
        ActionEntity actionEntity = getInstancioOf(ActionEntity.class).create();
        TestPlanEntity testPlanEntity = getInstancioOf(TestPlanEntity.class)
                                                                            .set(field(TestPlanEntity::getTestSteps),
                                                                                    List.of(actionEntity))
                                                                            .create();

        Action mockAction = Mockito.mock(Action.class);
        when(actionMapper.fromEntity(actionEntity)).thenReturn(mockAction);

        TestPlan result = testPlanEntityMapper.fromEntity(testPlanEntity);

        assertNotNull(result);
        assertEquals(testPlanEntity.getId(), result.getId());
        assertEquals(testPlanEntity.getApplicationId(), result.getApplicationID());
        assertEquals(testPlanEntity.getStageId(), result.getStageID());
        assertEquals(testPlanEntity.getLabel(), result.getLabel());
        assertNotNull(result.getActions());
        assertEquals(1, result.getActions()
                              .size());
        assertEquals(mockAction, result.getActions()
                                       .get(0));
    }

    @Test
    void fromEntity_ShouldHandleEmptyTestSteps() {
        TestPlanEntity emptyTestStepsEntity = getInstancioOf(TestPlanEntity.class).set(
                field(TestPlanEntity::getTestSteps), List.of())
                                                                                  .create();

        TestPlan result = testPlanEntityMapper.fromEntity(emptyTestStepsEntity);

        assertNotNull(result);
        assertEquals(emptyTestStepsEntity.getId(), result.getId());
        assertEquals(emptyTestStepsEntity.getApplicationId(), result.getApplicationID());
        assertEquals(emptyTestStepsEntity.getStageId(), result.getStageID());
        assertEquals(emptyTestStepsEntity.getLabel(), result.getLabel());
        assertNotNull(result.getActions());
        assertEquals(0, result.getActions()
                              .size());
    }

    @Test
    void fromEntity_ShouldThrowWhenTestStepsAreNull() {
        TestPlanEntity nullTestStepsEntity = Instancio.create(TestPlanEntity.class);
        nullTestStepsEntity.setTestSteps(null);

        assertThrows(NullPointerException.class, () -> testPlanEntityMapper.fromEntity(nullTestStepsEntity));
    }

    @Test
    void toEntity_ShouldMapTestPlanToTestPlanEntity() {
        Action action1 = Mockito.mock(Action.class);
        Action action2 = Mockito.mock(Action.class);
        TestPlan testPlan = getInstancioOf(TestPlan.class).set(field(TestPlan::getActions), List.of(action1, action2))
                                                          .create();

        ActionEntity actionEntity1 = getInstancioOf(ActionEntity.class).create();
        ActionEntity actionEntity2 = getInstancioOf(ActionEntity.class).create();
        when(actionMapper.toEntity(action1, testPlan.getApplicationID(), testPlan.getStageID())).thenReturn(
                actionEntity1);
        when(actionMapper.toEntity(action2, testPlan.getApplicationID(), testPlan.getStageID())).thenReturn(
                actionEntity2);

        TestPlanEntity result = testPlanEntityMapper.toEntity(testPlan);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(testPlan.getApplicationID(), result.getApplicationId());
        assertEquals(testPlan.getStageID(), result.getStageId());
        assertEquals(testPlan.getLabel(), result.getLabel());
        assertNotNull(result.getTestSteps());
        assertEquals(2, result.getTestSteps()
                              .size());
        assertEquals(actionEntity1, result.getTestSteps()
                                          .get(0));
        assertEquals(actionEntity2, result.getTestSteps()
                                          .get(1));
    }

    @Test
    void toEntity_ShouldMapTestPlanDefinitionToTestPlanEntity() {
        Action action1 = Mockito.mock(Action.class);
        Action action2 = Mockito.mock(Action.class);
        TestPlanDefinition definition = getInstancioOf(TestPlanDefinition.class)
                                                                                .set(field(TestPlanDefinition::actions),
                                                                                        List.of(action1, action2))
                                                                                .create();

        ActionEntity actionEntity1 = getInstancioOf(ActionEntity.class).create();
        ActionEntity actionEntity2 = getInstancioOf(ActionEntity.class).create();
        when(actionMapper.toEntity(action1, definition.applicationID(), definition.stageID())).thenReturn(
                actionEntity1);
        when(actionMapper.toEntity(action2, definition.applicationID(), definition.stageID())).thenReturn(
                actionEntity2);

        TestPlanEntity result = testPlanEntityMapper.toEntity(definition);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(definition.applicationID(), result.getApplicationId());
        assertEquals(definition.stageID(), result.getStageId());
        assertEquals(definition.label(), result.getLabel());
        assertNotNull(result.getTestSteps());
        assertEquals(2, result.getTestSteps()
                              .size());
        assertEquals(actionEntity1, result.getTestSteps()
                                          .get(0));
        assertEquals(actionEntity2, result.getTestSteps()
                                          .get(1));
    }

    @Test
    void mapEntityToPersistedTestPlan_ShouldMapFields() {
        TestPlanEntity entity = getInstancioOf(TestPlanEntity.class).create();

        PersistedTestPlan result = testPlanEntityMapper.mapEntityToPersistedTestPlan(entity);

        assertNotNull(result);
        assertEquals(entity.getId(), result.id());
        assertEquals(entity.getApplicationId(), result.applicationID());
        assertEquals(entity.getStageId(), result.stageID());
        assertEquals(entity.getLabel(), result.label());
    }

}
