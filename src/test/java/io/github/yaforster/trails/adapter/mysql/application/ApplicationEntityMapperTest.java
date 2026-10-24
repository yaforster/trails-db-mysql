package io.github.yaforster.trails.adapter.mysql.application;

import io.github.yaforster.trails.core.definition.ApplicationDefinition;
import io.github.yaforster.trails.core.persisted.PersistedApplication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ApplicationEntityMapperTest {

    private final ApplicationEntityMapper mapper = new ApplicationEntityMapper();

    @Test
    void toEntity_ShouldMapApplicationDefinition() {
        ApplicationDefinition definition = new ApplicationDefinition("my-app");

        var entity = mapper.toEntity(definition);

        assertNull(entity.getId());
        assertEquals("my-app", entity.getLabel());
    }

    @Test
    void toPersisted_ShouldMapApplicationEntity() {
        var entity = mapper.toEntity(new ApplicationDefinition("my-app"));
        entity.setId(55L);

        PersistedApplication persisted = mapper.toPersisted(entity);

        assertEquals(55L, persisted.id());
        assertEquals("my-app", persisted.label());
    }

}
