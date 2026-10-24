package io.github.yaforster.trails.adapter.mysql.stage;

import io.github.yaforster.trails.core.definition.StageDefinition;
import io.github.yaforster.trails.core.persisted.PersistedStage;
import org.springframework.stereotype.Component;

@Component
public class StageEntityMapper {

    public StageEntity toEntity(Long applicationId, StageDefinition stageDefinition) {
        StageEntity entity = new StageEntity();
        entity.setLabel(stageDefinition.label());
        entity.setUrl(stageDefinition.url());
        entity.setApplicationId(applicationId);
        return entity;
    }

    public PersistedStage toPersisted(StageEntity entity) {
        return new PersistedStage(entity.getId(), entity.getApplicationId(), entity.getLabel(), entity.getUrl(),
                entity.isRetired());
    }

}
