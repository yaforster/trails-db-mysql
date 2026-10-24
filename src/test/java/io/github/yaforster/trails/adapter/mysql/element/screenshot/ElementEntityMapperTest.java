package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.core.data.ElementType;
import io.github.yaforster.trails.core.definition.ElementDefinition;
import io.github.yaforster.trails.core.persisted.PersistedElement;
import io.github.yaforster.trails.core.test.action.element.LocatorType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ElementEntityMapperTest {

    private final ElementEntityMapper mapper = new ElementEntityMapper();

    @Test
    void toEntity_ShouldMapElementDefinition() {
        ElementDefinition definition = new ElementDefinition(ElementType.TEXT, "label", "#locator", LocatorType.CSS);

        var entity = mapper.toEntity(11L, 22L, definition);

        assertNull(entity.getId());
        assertEquals(11L, entity.getApplicationId());
        assertEquals(22L, entity.getStageId());
        assertEquals("label", entity.getLabel());
        assertEquals("#locator", entity.getLocator());
        assertEquals(LocatorType.CSS, entity.getLocatorType());
        assertEquals(ElementType.TEXT, entity.getType());
    }

    @Test
    void toPersisted_ShouldMapElementEntity() {
        var entity = mapper.toEntity(11L, 22L,
                new ElementDefinition(ElementType.SELECT, "x", "//a", LocatorType.XPATH));
        entity.setId(123L);

        PersistedElement persisted = mapper.toPersisted(entity);

        assertEquals(123L, persisted.id());
        assertEquals(11L, persisted.applicationId());
        assertEquals(22L, persisted.stageId());
        assertEquals("x", persisted.label());
        assertEquals("//a", persisted.locator());
        assertEquals(LocatorType.XPATH, persisted.locatorType());
        assertEquals(ElementType.SELECT, persisted.type());
    }

}
