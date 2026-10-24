package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TestPlanActionReferenceResolver {

    private static final String MISSING_ACTION_REFERENCE = "Action referenceID must be set when storing a test plan";
    private static final String UNKNOWN_NEXT_ACTION_REFERENCE = "Unknown next action referenceID: %s";

    public void resolveReferences(TestPlanEntity storedEntity) {
        // Next-action links arrive as definition reference IDs, but the database stores generated action IDs.
        Map<Long, Long> referenceIdToPersistedId = indexPersistedActionIds(storedEntity);
        storedEntity.getTestSteps()
                    .forEach(action -> resolveActionReferences(storedEntity, action, referenceIdToPersistedId));
    }

    private Map<Long, Long> indexPersistedActionIds(TestPlanEntity storedEntity) {
        Map<Long, Long> referenceIdToPersistedId = new HashMap<>();
        storedEntity.getTestSteps()
                    .forEach(action -> {
                        if (action.getActionId() == null) {
                            throw new IllegalStateException(MISSING_ACTION_REFERENCE);
                        }
                        referenceIdToPersistedId.put(action.getActionId(), action.getId());
                    });
        return referenceIdToPersistedId;
    }

    private void resolveActionReferences(TestPlanEntity storedEntity, ActionEntity action,
            Map<Long, Long> referenceIdToPersistedId) {
        action.setTestPlanId(storedEntity.getId());
        if (action.getNextActions() == null) {
            action.setNextActions(new ArrayList<>());
            return;
        }
        List<Long> persistedNextActionIds = action.getNextActions()
                                                  .stream()
                                                  .map(nextReferenceId -> resolveReference(referenceIdToPersistedId,
                                                          nextReferenceId))
                                                  .collect(Collectors.toCollection(ArrayList::new));
        action.setNextActions(persistedNextActionIds);
    }

    private Long resolveReference(Map<Long, Long> referenceIdToPersistedId, Long nextReferenceId) {
        Long resolved = referenceIdToPersistedId.get(nextReferenceId);
        if (resolved == null) {
            String message = UNKNOWN_NEXT_ACTION_REFERENCE.formatted(nextReferenceId);
            throw new IllegalStateException(message);
        }
        return resolved;
    }

}
