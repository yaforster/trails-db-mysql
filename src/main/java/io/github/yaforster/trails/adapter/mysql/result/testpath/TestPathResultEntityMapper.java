package io.github.yaforster.trails.adapter.mysql.result.testpath;

import io.github.yaforster.trails.core.persisted.PersistedTestPathResult;
import org.springframework.stereotype.Component;

@Component
public class TestPathResultEntityMapper {

    public PersistedTestPathResult toPersisted(TestPathResultEntity entity) {
        return new PersistedTestPathResult(entity.getId(), entity.getTestSetResultId(), entity.getPathLabel());
    }

}
