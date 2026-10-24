package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.adapter.mysql.result.action.variant.SkippedResultEntityVariantMapper;
import io.github.yaforster.trails.adapter.mysql.result.action.variant.SuccessResultEntityVariantMapper;
import io.github.yaforster.trails.adapter.mysql.result.action.variant.TechnicalFailureResultEntityVariantMapper;
import io.github.yaforster.trails.adapter.mysql.result.action.variant.ValidationFailureResultEntityVariantMapper;
import io.github.yaforster.trails.core.test.result.SkippedResult;
import io.github.yaforster.trails.core.test.result.TechnicalFailure;
import io.github.yaforster.trails.core.test.result.ValidationFailure;
import io.github.yaforster.trails.core.test.result.Success;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultEntityMapperTest {

    private final ResultEntityMapper mapper = new ResultEntityMapper(
            List.of(new SuccessResultEntityVariantMapper(), new SkippedResultEntityVariantMapper(),
                    new ValidationFailureResultEntityVariantMapper(), new TechnicalFailureResultEntityVariantMapper()));

    @Test
    void toEntity_ShouldMapSuccess() {
        Success result = Success.builder()
                                .actionID(5L)
                                .label("Click")
                                .resultMessage("OK")
                                .build();

        SuccessEntity entity = assertInstanceOf(SuccessEntity.class, mapper.toEntity(10L, 2, result));

        assertEquals(10L, entity.getTestPathResultId());
        assertEquals(2, entity.getExecutionOrder());
        assertEquals(5L, entity.getActionId());
        assertEquals("Click", entity.getLabel());
        assertEquals("OK", entity.getMessage());
        assertTrue(entity.isSuccess());
    }

    @Test
    void toEntity_ShouldMapSkippedResult() {
        SkippedResult result = SkippedResult.builder()
                                            .build();

        SkippedResultEntity entity = assertInstanceOf(SkippedResultEntity.class, mapper.toEntity(10L, 2, result));

        assertFalse(entity.isSuccess());
    }

    @Test
    void toEntity_ShouldMapValidationFailure() {
        ValidationFailure result = ValidationFailure.builder()
                                                    .build();

        ValidationFailureEntity entity = assertInstanceOf(ValidationFailureEntity.class,
                mapper.toEntity(10L, 2, result));

        assertFalse(entity.isSuccess());
    }

    @Test
    void toEntity_ShouldPersistViewportResizeTechnicalFailureDiagnostic() {
        String diagnostic = "Requested viewport: 800 x 600 CSS pixels. Last measured viewport: unavailable. Cause: Node rejected resize.";
        TechnicalFailure result = TechnicalFailure.builder()
                                                  .exceptionMessageFromAction(diagnostic)
                                                  .build();

        TechnicalFailureEntity entity = assertInstanceOf(TechnicalFailureEntity.class, mapper.toEntity(10L, 2, result));

        assertEquals(diagnostic, entity.getExceptionMessageFromAction());
        assertFalse(entity.isSuccess());
    }

}
