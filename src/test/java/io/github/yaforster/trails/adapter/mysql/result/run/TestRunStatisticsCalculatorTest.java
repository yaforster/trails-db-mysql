package io.github.yaforster.trails.adapter.mysql.result.run;

import io.github.yaforster.trails.core.persisted.PersistedTestRunStatistics;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TestRunStatisticsCalculatorTest {

    private final TestRunResultRepository repository = Mockito.mock(TestRunResultRepository.class);
    private final TestRunStatisticsCalculator calculator = new TestRunStatisticsCalculator(repository);

    @Test
    void calculate_ShouldAggregateRunCountsByStatus() {
        when(repository.countByApplicationIdAndStageIdAndTestPlanId(1L, 2L, 3L)).thenReturn(9L);
        when(repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(1L, 2L, 3L,
                TestRunStatus.SUCCESS)).thenReturn(4L);
        when(repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(1L, 2L, 3L,
                TestRunStatus.PARTIAL_SUCCESS)).thenReturn(3L);
        when(repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(1L, 2L, 3L,
                TestRunStatus.FAILURE)).thenReturn(2L);

        PersistedTestRunStatistics result = calculator.calculate(1L, 2L, 3L);

        assertEquals(1L, result.applicationId());
        assertEquals(2L, result.stageId());
        assertEquals(3L, result.testPlanId());
        assertEquals(9L, result.totalRuns());
        assertEquals(4L, result.successfulRuns());
        assertEquals(3L, result.partialSuccessRuns());
        assertEquals(2L, result.failedRuns());
    }

}
