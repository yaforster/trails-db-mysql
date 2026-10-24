package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.core.definition.ElementDefinition;
import io.github.yaforster.trails.core.persisted.PersistedElement;
import org.springframework.stereotype.Component;

@Component
public class ElementEntityMapper {

    public ElementEntity toEntity(Long applicationId, Long stageId, ElementDefinition elementDefinition) {
        ElementEntity entity = new ElementEntity();
        entity.setApplicationId(applicationId);
        entity.setStageId(stageId);
        return updateEntity(entity, elementDefinition);
    }

    public ElementEntity updateEntity(ElementEntity entity, ElementDefinition elementDefinition) {
        entity.setLabel(elementDefinition.label());
        entity.setLocator(elementDefinition.locatorString());
        entity.setLocatorType(elementDefinition.locatorType());
        entity.setType(elementDefinition.type());
        return entity;
    }

    public PersistedElement toPersisted(ElementEntity entity) {
        return new PersistedElement(entity.getId(), entity.getLabel(), entity.getLocatorType(),
                entity.getApplicationId(), entity.getStageId(), entity.getLocator(), entity.getType(),
                entity.isRetired());
    }

}
