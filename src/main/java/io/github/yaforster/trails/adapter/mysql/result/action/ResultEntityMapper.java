package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.adapter.mysql.result.action.variant.ResultEntityVariantMapper;
import io.github.yaforster.trails.core.test.Result;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ResultEntityMapper {

    private final List<ResultEntityVariantMapper> variants;

    public ResultEntity toEntity(Long testPathResultId, int executionOrder, Result result) {
        ResultEntity entity = variants.stream()
                                      .filter(variant -> variant.supports(result))
                                      .findFirst()
                                      .map(variant -> variant.toEntity(result))
                                      .orElseGet(TechnicalFailureEntity::new);

        entity.setTestPathResultId(testPathResultId);
        entity.setExecutionOrder(executionOrder);
        entity.setActionId(result.getActionID());
        entity.setLabel(result.getLabel());
        entity.setMessage(result.getResultMessage());
        return entity;
    }

}
