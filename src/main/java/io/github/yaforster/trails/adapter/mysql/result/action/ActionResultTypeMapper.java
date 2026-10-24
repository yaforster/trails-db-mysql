package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.core.persisted.ActionResultType;
import org.springframework.stereotype.Component;

@Component
public class ActionResultTypeMapper {

    private static final String MISSING_RESULT = "Result entity must not be null.";
    private static final String UNKNOWN_RESULT_TYPE = "Unknown result entity type: %s";

    public ActionResultType fromEntity(ResultEntity entity) {
        return switch (entity) {
            case SuccessEntity _ -> ActionResultType.SUCCESS;
            case SkippedResultEntity _ -> ActionResultType.SKIPPED;
            case ValidationFailureEntity _ -> ActionResultType.VALIDATION_FAILURE;
            case TechnicalFailureEntity _ -> ActionResultType.TECHNICAL_FAILURE;
            case null -> throw new IllegalArgumentException(MISSING_RESULT);
            default -> {
                String message = UNKNOWN_RESULT_TYPE.formatted(entity.getClass()
                                                                     .getName());
                throw new IllegalStateException(message);
            }
        };
    }

}
