package io.github.yaforster.trails.adapter.mysql.result.action.variant;

import io.github.yaforster.trails.adapter.mysql.result.action.ResultEntity;
import io.github.yaforster.trails.adapter.mysql.result.action.TechnicalFailureEntity;
import io.github.yaforster.trails.core.test.Result;
import io.github.yaforster.trails.core.test.result.TechnicalFailure;
import org.springframework.stereotype.Component;

@Component
public class TechnicalFailureResultEntityVariantMapper implements ResultEntityVariantMapper {

    @Override
    public boolean supports(Result result) {
        return result instanceof TechnicalFailure;
    }

    @Override
    public ResultEntity toEntity(Result result) {
        TechnicalFailure technicalFailure = (TechnicalFailure) result;
        TechnicalFailureEntity entity = new TechnicalFailureEntity();
        entity.setExceptionMessageFromAction(technicalFailure.getExceptionMessageFromAction());
        return entity;
    }

}
