package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Retains source IDs alongside the new action instances until their cascading persist assigns IDs. The entity is new
 * (its ID is null), so Spring Data persists these same instances rather than merging copies.
 */
public final class TestPlanCopy {

    private static final String PLAN_NOT_PERSISTED = "The copied test plan must be persisted before resolving action links";
    private static final String ACTION_NOT_PERSISTED = "The copy of source action %s must be persisted before resolving action links";
    private static final String UNKNOWN_SOURCE_ACTION = "Unknown source action in copied next-action links: %s";
    private final TestPlanEntity entity;
    private final Map<Long, ActionEntity> actionsBySourceId;

    TestPlanCopy(TestPlanEntity entity, Map<Long, ActionEntity> actionsBySourceId) {
        this.entity = entity;
        this.actionsBySourceId = Map.copyOf(actionsBySourceId);
    }

    public TestPlanEntity entity() {
        return entity;
    }

    public void resolveNextActions() {
        Long testPlanId = entity.getId();
        if (testPlanId == null) {
            throw new IllegalStateException(PLAN_NOT_PERSISTED);
        }
        Map<Long, Long> persistedIdsBySourceId = indexPersistedActionIds();
        for (ActionEntity action : entity.getTestSteps()) {
            List<Long> nextActionIds = resolveNextActionIds(action, persistedIdsBySourceId);
            action.setTestPlanId(testPlanId);
            action.setNextActions(nextActionIds);
        }
    }

    private Map<Long, Long> indexPersistedActionIds() {
        Map<Long, Long> persistedIdsBySourceId = new HashMap<>();
        actionsBySourceId.forEach((sourceId, copy) -> {
            Long persistedId = copy.getId();
            if (persistedId == null) {
                String message = ACTION_NOT_PERSISTED.formatted(sourceId);
                throw new IllegalStateException(message);
            }
            persistedIdsBySourceId.put(sourceId, persistedId);
        });
        return persistedIdsBySourceId;
    }

    private List<Long> resolveNextActionIds(ActionEntity action, Map<Long, Long> persistedIdsBySourceId) {
        List<Long> nextActionIds = new ArrayList<>();
        for (Long sourceId : action.getNextActions()) {
            Long persistedId = persistedIdsBySourceId.get(sourceId);
            if (persistedId == null) {
                String message = UNKNOWN_SOURCE_ACTION.formatted(sourceId);
                throw new IllegalStateException(message);
            }
            nextActionIds.add(persistedId);
        }
        return nextActionIds;
    }
}
