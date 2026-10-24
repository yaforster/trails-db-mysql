package io.github.yaforster.trails.adapter.mysql.result.action.variant;

import io.github.yaforster.trails.adapter.mysql.result.action.ResultEntity;
import io.github.yaforster.trails.adapter.mysql.result.action.SuccessEntity;
import io.github.yaforster.trails.core.test.Result;
import io.github.yaforster.trails.core.test.result.Success;
import org.springframework.stereotype.Component;

@Component
public class SuccessResultEntityVariantMapper implements ResultEntityVariantMapper {

    @Override
    public boolean supports(Result result) {
        return result instanceof Success;
    }

    @Override
    public ResultEntity toEntity(Result result) {
        return new SuccessEntity().setSuccess(true);
    }

}
