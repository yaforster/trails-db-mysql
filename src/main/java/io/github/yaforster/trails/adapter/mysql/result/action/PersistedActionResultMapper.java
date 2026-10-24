package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.core.persisted.PersistedActionResult;
import io.github.yaforster.trails.core.persisted.ActionResultType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PersistedActionResultMapper {

    private static final String UNKNOWN_TECHNICAL_FAILURE_MESSAGE = "The cause of the error could not be identified.";
    private final ActionResultTypeMapper actionResultTypeMapper;

    public PersistedActionResult fromEntity(ResultEntity entity) {
        ActionResultType resultType = actionResultTypeMapper.fromEntity(entity);
        String exceptionMessage = mapExceptionMessage(entity);
        return new PersistedActionResult(entity.getId(), entity.getTestPathResultId(), entity.getActionId(),
                entity.getLabel(), entity.getMessage(), resultType, exceptionMessage);
    }

    private String mapExceptionMessage(ResultEntity entity) {
        if (entity instanceof TechnicalFailureEntity technicalFailure) {
            return getTechnicalFailureMessage(technicalFailure);
        }
        return null;
    }

    private String getTechnicalFailureMessage(TechnicalFailureEntity technicalFailure) {
        String exceptionMessage = technicalFailure.getExceptionMessageFromAction();
        if (exceptionMessage == null || exceptionMessage.isBlank()) {
            return UNKNOWN_TECHNICAL_FAILURE_MESSAGE;
        }
        return exceptionMessage;
    }

}
