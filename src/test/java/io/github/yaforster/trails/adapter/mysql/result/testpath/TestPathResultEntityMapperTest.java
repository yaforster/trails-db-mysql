package io.github.yaforster.trails.adapter.mysql.result.testpath;

import io.github.yaforster.trails.core.persisted.PersistedTestPathResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestPathResultEntityMapperTest {

    private final TestPathResultEntityMapper mapper = new TestPathResultEntityMapper();

    @Test
    void toPersisted_ShouldMapEntityToPersistedResult() {
        TestPathResultEntity entity = new TestPathResultEntity().setId(5L)
                                                                .setTestSetResultId(9L)
                                                                .setPathLabel("path-A");

        PersistedTestPathResult result = mapper.toPersisted(entity);

        assertEquals(5L, result.id());
        assertEquals(9L, result.testSetResultId());
        assertEquals("path-A", result.pathLabel());
    }

}
