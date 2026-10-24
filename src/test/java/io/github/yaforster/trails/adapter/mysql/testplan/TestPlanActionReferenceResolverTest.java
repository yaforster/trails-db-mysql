package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestPlanActionReferenceResolverTest {

    private final TestPlanActionReferenceResolver resolver = new TestPlanActionReferenceResolver();

    @Test
    void resolveReferences_ShouldReplaceDefinitionReferencesWithPersistedActionIds() {
        ActionEntity firstAction = new ActionEntity().setId(100L)
                                                     .setActionId(10L)
                                                     .setNextActions(List.of(20L, 30L));
        ActionEntity secondAction = new ActionEntity().setId(200L)
                                                      .setActionId(20L)
                                                      .setNextActions(List.of());
        ActionEntity thirdAction = new ActionEntity().setId(300L)
                                                     .setActionId(30L)
                                                     .setNextActions(null);
        TestPlanEntity testPlan = new TestPlanEntity().setId(900L)
                                                      .setTestSteps(List.of(firstAction, secondAction, thirdAction));

        resolver.resolveReferences(testPlan);

        assertEquals(900L, firstAction.getTestPlanId());
        assertEquals(900L, secondAction.getTestPlanId());
        assertEquals(900L, thirdAction.getTestPlanId());
        assertEquals(List.of(200L, 300L), firstAction.getNextActions());
        assertEquals(List.of(), secondAction.getNextActions());
        assertEquals(List.of(), thirdAction.getNextActions());
    }

    @Test
    void resolveReferences_ShouldThrow_WhenActionReferenceIdIsMissing() {
        TestPlanEntity testPlan = new TestPlanEntity().setTestSteps(List.of(new ActionEntity().setId(100L)
                                                                                              .setActionId(null)
                                                                                              .setNextActions(
                                                                                                      List.of())));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> resolver.resolveReferences(testPlan));

        assertEquals("Action referenceID must be set when storing a test plan", exception.getMessage());
    }

    @Test
    void resolveReferences_ShouldThrow_WhenNextActionReferenceIsUnknown() {
        TestPlanEntity testPlan = new TestPlanEntity().setTestSteps(List.of(new ActionEntity().setId(100L)
                                                                                              .setActionId(10L)
                                                                                              .setNextActions(
                                                                                                      List.of(999L))));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> resolver.resolveReferences(testPlan));

        assertEquals("Unknown next action referenceID: 999", exception.getMessage());
    }

}
