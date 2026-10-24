package io.github.yaforster.trails.adapter.mysql.result.run;

import io.github.yaforster.trails.core.data.ResultIndicator;
import io.github.yaforster.trails.core.persisted.PersistedTestRunResult;
import io.github.yaforster.trails.core.test.TestRunResult;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class TestRunResultEntityMapper {

    private final ResultIndicatorMapper resultIndicatorMapper;

    public PersistedTestRunResult toPersisted(TestRunResultEntity entity) {
        ResultIndicator indicator = resultIndicatorMapper.toResultIndicator(entity.getStatus());
        return new PersistedTestRunResult(entity.getId(), entity.getApplicationId(), entity.getStageId(),
                entity.getTestPlanId(), entity.getTimestamp(), indicator, entity.getLabel());
    }

    public TestRunResultEntity toEntity(TestRunResult result) {
        TestRunStatus status = resultIndicatorMapper.toTestRunStatus(result.indicator());
        return new TestRunResultEntity(null, result.applicationID(), result.stageID(), result.testPlanID(),
                result.timestamp(), status, result.label());
    }

}
