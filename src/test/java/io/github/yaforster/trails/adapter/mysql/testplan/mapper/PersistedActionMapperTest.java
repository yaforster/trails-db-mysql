package io.github.yaforster.trails.adapter.mysql.testplan.mapper;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.ActionEntityMapper;
import io.github.yaforster.trails.core.test.Action;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PersistedActionMapperTest {

    private final ActionEntityMapper actionEntityMapper = Mockito.mock(ActionEntityMapper.class);
    private final PersistedActionMapper mapper = new PersistedActionMapper(actionEntityMapper);

    @Test
    void toPersisted_ShouldMapActionEntity() {
        ActionEntity entity = new ActionEntity();
        entity.setId(1L);
        entity.setTestPlanId(2L);
        entity.setActionId(3L);
        Action action = Mockito.mock(Action.class);
        when(actionEntityMapper.fromEntity(entity)).thenReturn(action);

        var result = mapper.toPersisted(entity);

        assertEquals(1L, result.id());
        assertEquals(2L, result.testPlanId());
        assertEquals(3L, result.referenceId());
        assertEquals(action, result.action());
    }

}
