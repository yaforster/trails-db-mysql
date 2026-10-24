package io.github.yaforster.trails.adapter.mysql.result.run;

import io.github.yaforster.trails.core.data.ResultIndicator;
import io.github.yaforster.trails.core.persisted.PersistedTestRunResult;
import io.github.yaforster.trails.core.test.TestRunResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.Timestamp;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

class TestRunResultEntityMapperTest {

    private final ResultIndicatorMapper resultIndicatorMapper = Mockito.mock(ResultIndicatorMapper.class);
    private final TestRunResultEntityMapper mapper = new TestRunResultEntityMapper(resultIndicatorMapper);

    @Test
    void toPersisted_ShouldMapEntityToPersistedResult() {
        Timestamp timestamp = Timestamp.valueOf("2026-01-02 03:04:05");
        TestRunResultEntity entity = new TestRunResultEntity(44L, 11L, 22L, 33L, timestamp, TestRunStatus.SUCCESS,
                "label");
        when(resultIndicatorMapper.toResultIndicator(TestRunStatus.SUCCESS)).thenReturn(ResultIndicator.SUCCESS);

        PersistedTestRunResult result = mapper.toPersisted(entity);

        assertEquals(44L, result.id());
        assertEquals(11L, result.applicationId());
        assertEquals(22L, result.stageId());
        assertEquals(33L, result.testPlanId());
        assertEquals(timestamp, result.timestamp());
        assertEquals(ResultIndicator.SUCCESS, result.status());
        assertEquals("label", result.label());
    }

    @Test
    void toEntity_ShouldMapTestRunResultToEntityAndResetId() {
        Timestamp timestamp = Timestamp.valueOf("2026-01-02 03:04:05");
        TestRunResult result = new TestRunResult(List.of(), timestamp, ResultIndicator.FAILURE, 101L, 202L, 303L,
                "label");
        when(resultIndicatorMapper.toTestRunStatus(ResultIndicator.FAILURE)).thenReturn(TestRunStatus.FAILURE);

        TestRunResultEntity entity = mapper.toEntity(result);

        assertNull(entity.getId());
        assertEquals(101L, entity.getApplicationId());
        assertEquals(202L, entity.getStageId());
        assertEquals(303L, entity.getTestPlanId());
        assertEquals(timestamp, entity.getTimestamp());
        assertEquals(TestRunStatus.FAILURE, entity.getStatus());
        assertEquals("label", entity.getLabel());
    }

}
