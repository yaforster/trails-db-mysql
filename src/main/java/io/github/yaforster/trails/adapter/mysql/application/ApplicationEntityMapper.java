package io.github.yaforster.trails.adapter.mysql.application;

import io.github.yaforster.trails.core.definition.ApplicationDefinition;
import io.github.yaforster.trails.core.persisted.PersistedApplication;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEntityMapper {

    public ApplicationEntity toEntity(ApplicationDefinition definition) {
        ApplicationEntity entity = new ApplicationEntity();
        entity.setLabel(definition.label());
        return entity;
    }

    public PersistedApplication toPersisted(ApplicationEntity entity) {
        return new PersistedApplication(entity.getId(), entity.getLabel(), entity.isRetired());
    }

}
