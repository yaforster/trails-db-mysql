package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.core.persisted.ActionResultType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ActionResultTypeMapperTest {

    private final ActionResultTypeMapper mapper = new ActionResultTypeMapper();

    @Test
    void fromEntity_ShouldMapSuccessEntity() {
        assertEquals(ActionResultType.SUCCESS, mapper.fromEntity(new SuccessEntity()));
    }

    @Test
    void fromEntity_ShouldMapSkippedResultEntity() {
        assertEquals(ActionResultType.SKIPPED, mapper.fromEntity(new SkippedResultEntity()));
    }

    @Test
    void fromEntity_ShouldMapValidationFailureEntity() {
        assertEquals(ActionResultType.VALIDATION_FAILURE, mapper.fromEntity(new ValidationFailureEntity()));
    }

    @Test
    void fromEntity_ShouldMapTechnicalFailureEntity() {
        assertEquals(ActionResultType.TECHNICAL_FAILURE, mapper.fromEntity(new TechnicalFailureEntity()));
    }

    @Test
    void fromEntity_ShouldThrowForUnknownResultEntityType() {
        ResultEntity entity = Mockito.mock(ResultEntity.class);
        assertThrows(IllegalStateException.class, () -> mapper.fromEntity(entity));
    }

}
