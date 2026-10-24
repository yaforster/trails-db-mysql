package io.github.yaforster.trails.adapter.mysql.result.action.variant;

import io.github.yaforster.trails.adapter.mysql.result.action.ResultEntity;
import io.github.yaforster.trails.adapter.mysql.result.action.ValidationFailureEntity;
import io.github.yaforster.trails.core.test.Result;
import io.github.yaforster.trails.core.test.result.ValidationFailure;
import org.springframework.stereotype.Component;

@Component
public class ValidationFailureResultEntityVariantMapper implements ResultEntityVariantMapper {

    @Override
    public boolean supports(Result result) {
        return result instanceof ValidationFailure;
    }

    @Override
    public ResultEntity toEntity(Result result) {
        return new ValidationFailureEntity();
    }

}
