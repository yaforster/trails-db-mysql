package io.github.yaforster.trails.adapter.mysql.result.run;

import io.github.yaforster.trails.core.persisted.PersistedTestRunStatistics;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TestRunStatisticsCalculator {

    private final TestRunResultRepository repository;

    public PersistedTestRunStatistics calculate(Long applicationId, Long stageId, Long testPlanId) {
        long totalRuns = repository.countByApplicationIdAndStageIdAndTestPlanId(applicationId, stageId, testPlanId);
        long successfulRuns = repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(applicationId, stageId,
                testPlanId, TestRunStatus.SUCCESS);
        long partialSuccessRuns = repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(applicationId,
                stageId, testPlanId, TestRunStatus.PARTIAL_SUCCESS);
        long failedRuns = repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(applicationId, stageId,
                testPlanId, TestRunStatus.FAILURE);

        return new PersistedTestRunStatistics(applicationId, stageId, testPlanId, totalRuns, successfulRuns,
                partialSuccessRuns, failedRuns);
    }

}
