package io.github.yaforster.trails.adapter.mysql.stage;

import io.github.yaforster.trails.core.definition.StageDefinition;
import io.github.yaforster.trails.core.persisted.PersistedStage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StageEntityMapperTest {

    private final StageEntityMapper mapper = new StageEntityMapper();

    @Test
    void toEntity_ShouldMapStageDefinition() {
        StageDefinition definition = new StageDefinition("stage-label", "https://example.org", List.of());

        var entity = mapper.toEntity(99L, definition);

        assertNull(entity.getId());
        assertEquals(99L, entity.getApplicationId());
        assertEquals("stage-label", entity.getLabel());
        assertEquals("https://example.org", entity.getUrl());
    }

    @Test
    void toPersisted_ShouldMapStageEntity() {
        var entity = mapper.toEntity(99L, new StageDefinition("stage-label", "https://example.org", List.of()));
        entity.setId(66L);

        PersistedStage persisted = mapper.toPersisted(entity);

        assertEquals(66L, persisted.id());
        assertEquals(99L, persisted.applicationId());
        assertEquals("stage-label", persisted.label());
        assertEquals("https://example.org", persisted.url());
    }

}
