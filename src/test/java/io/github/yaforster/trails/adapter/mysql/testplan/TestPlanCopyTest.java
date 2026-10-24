package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TestPlanCopyTest {

    @Test
    void shouldAssignOwningPlanId_whenResolvingCopiedActions() {
        ActionEntity action = new ActionEntity().setId(200L)
                                                .setNextActions(List.of());
        TestPlanEntity entity = new TestPlanEntity().setId(300L)
                                                    .setTestSteps(List.of(action));
        Map<Long, ActionEntity> copies = Map.of(100L, action);
        TestPlanCopy copy = new TestPlanCopy(entity, copies);

        copy.resolveNextActions();

        assertThat(action.getTestPlanId()).isEqualTo(300L);
    }

    @Test
    void shouldPreserveLinkOrderAndCycles_whenResolvingCopiedActions() {
        ActionEntity first = new ActionEntity().setId(200L)
                                               .setNextActions(List.of(101L, 100L, 101L));
        ActionEntity second = new ActionEntity().setId(201L)
                                                .setNextActions(List.of(100L));
        TestPlanEntity entity = new TestPlanEntity().setId(300L)
                                                    .setTestSteps(List.of(first, second));
        Map<Long, ActionEntity> copies = Map.of(100L, first, 101L, second);
        TestPlanCopy copy = new TestPlanCopy(entity, copies);

        copy.resolveNextActions();

        assertThat(List.of(first.getNextActions(), second.getNextActions())).containsExactly(List.of(201L, 200L, 201L),
                List.of(200L));
    }

    @Test
    void shouldRejectResolution_whenPlanHasNotBeenPersisted() {
        TestPlanEntity entity = new TestPlanEntity().setTestSteps(List.of());
        TestPlanCopy copy = new TestPlanCopy(entity, Map.of());

        assertThatThrownBy(copy::resolveNextActions).isInstanceOf(IllegalStateException.class)
                                                    .hasMessage(
                                                            "The copied test plan must be persisted before resolving action links");
    }

    @Test
    void shouldRejectResolution_whenCopiedActionHasNoGeneratedId() {
        ActionEntity action = new ActionEntity().setNextActions(List.of());
        TestPlanEntity entity = new TestPlanEntity().setId(300L)
                                                    .setTestSteps(List.of(action));
        Map<Long, ActionEntity> copies = Map.of(100L, action);
        TestPlanCopy copy = new TestPlanCopy(entity, copies);

        assertThatThrownBy(copy::resolveNextActions).isInstanceOf(IllegalStateException.class)
                                                    .hasMessage(
                                                            "The copy of source action 100 must be persisted before resolving action links");
    }

    @Test
    void shouldRejectResolution_whenNextActionHasNoCopy() {
        ActionEntity action = new ActionEntity().setId(200L)
                                                .setNextActions(List.of(999L));
        TestPlanEntity entity = new TestPlanEntity().setId(300L)
                                                    .setTestSteps(List.of(action));
        Map<Long, ActionEntity> copies = Map.of(100L, action);
        TestPlanCopy copy = new TestPlanCopy(entity, copies);

        assertThatThrownBy(copy::resolveNextActions).isInstanceOf(IllegalStateException.class)
                                                    .hasMessage(
                                                            "Unknown source action in copied next-action links: 999");
    }
}
