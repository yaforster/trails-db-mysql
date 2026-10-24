package io.github.yaforster.trails.adapter.mysql.testplan.mapper;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.ActionEntityMapper;
import io.github.yaforster.trails.core.persisted.PersistedAction;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PersistedActionMapper {

    private final ActionEntityMapper actionEntityMapper;

    public PersistedAction toPersisted(ActionEntity entity) {
        return new PersistedAction(entity.getId(), entity.getTestPlanId(), entity.getActionId(),
                actionEntityMapper.fromEntity(entity));
    }

}
