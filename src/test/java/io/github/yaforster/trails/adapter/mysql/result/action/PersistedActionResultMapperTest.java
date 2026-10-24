package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.core.persisted.ActionResultType;
import io.github.yaforster.trails.core.persisted.PersistedActionResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class PersistedActionResultMapperTest {

    private final PersistedActionResultMapper mapper = new PersistedActionResultMapper(new ActionResultTypeMapper());

    @Test
    void fromEntity_ShouldMapSuccessEntity() {
        SuccessEntity entity = new SuccessEntity();
        entity.setId(1L);
        entity.setTestPathResultId(2L);
        entity.setActionId(3L);
        entity.setLabel("label");
        entity.setMessage("ok");

        PersistedActionResult result = mapper.fromEntity(entity);

        assertEquals(entity.getId(), result.id());
        assertEquals(entity.getTestPathResultId(), result.testPathResultId());
        assertEquals(entity.getActionId(), result.actionId());
        assertEquals(entity.getLabel(), result.label());
        assertEquals(entity.getMessage(), result.message());
        assertEquals(ActionResultType.SUCCESS, result.resultType());
        assertNull(result.exceptionMessageFromAction());
    }

    @Test
    void fromEntity_ShouldMapTechnicalFailureWithExceptionMessage() {
        TechnicalFailureEntity entity = new TechnicalFailureEntity();
        entity.setId(11L);
        entity.setTestPathResultId(12L);
        entity.setActionId(13L);
        entity.setLabel("technical");
        entity.setMessage("failed");
        entity.setExceptionMessageFromAction("Stacktrace root cause");

        PersistedActionResult result = mapper.fromEntity(entity);

        assertEquals(ActionResultType.TECHNICAL_FAILURE, result.resultType());
        assertEquals("Stacktrace root cause", result.exceptionMessageFromAction());
    }

    @Test
    void fromEntity_ShouldUseFallbackMessageWhenTechnicalFailureExceptionIsMissing() {
        TechnicalFailureEntity entity = new TechnicalFailureEntity();
        entity.setExceptionMessageFromAction(null);

        PersistedActionResult result = mapper.fromEntity(entity);

        assertEquals("The cause of the error could not be identified.", result.exceptionMessageFromAction());
    }

    @Test
    void fromEntity_ShouldUseFallbackMessageWhenTechnicalFailureExceptionIsBlank() {
        TechnicalFailureEntity entity = new TechnicalFailureEntity();
        entity.setExceptionMessageFromAction("  ");

        PersistedActionResult result = mapper.fromEntity(entity);

        assertEquals("The cause of the error could not be identified.", result.exceptionMessageFromAction());
    }

    @Test
    void fromEntity_ShouldThrowForUnknownResultEntityType() {
        ResultEntity entity = Mockito.mock(ResultEntity.class);
        assertThrows(IllegalStateException.class, () -> mapper.fromEntity(entity));
    }

}
