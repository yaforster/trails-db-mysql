package io.github.yaforster.trails.adapter.mysql.result.run;

import io.github.yaforster.trails.adapter.mysql.SpringPageMapper;
import io.github.yaforster.trails.app.services.StageDatabaseService;
import io.github.yaforster.trails.app.services.TestPlanDatabaseService;
import io.github.yaforster.trails.app.services.TestPlanDatabaseService.PersistedTestPlanDetails;
import io.github.yaforster.trails.app.services.TestRunResultDatabaseService;
import io.github.yaforster.trails.core.PagedResult;
import io.github.yaforster.trails.core.persisted.PersistedTestRunHistory;
import io.github.yaforster.trails.core.persisted.PersistedTestRunResult;
import io.github.yaforster.trails.core.persisted.PersistedTestRunStatistics;
import io.github.yaforster.trails.core.test.TestRunResult;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class TestRunResultDatabaseServiceImpl implements TestRunResultDatabaseService {

    private final TestRunResultRepository repository;
    private final TestRunResultEntityMapper testRunResultEntityMapper;
    private final StageDatabaseService stageDatabaseService;
    private final TestPlanDatabaseService testPlanDatabaseService;
    private final TestRunStatisticsCalculator statisticsCalculator;
    private final TestRunResultWriter testRunResultWriter;

    @Override
    public Optional<PersistedTestRunResult> getTestRunResult(TestRunResultDetails testRunResultDetails) {
        Optional<TestRunResultEntity> testRunResult = repository.findByApplicationIdAndStageIdAndId(
                testRunResultDetails.applicationId(), testRunResultDetails.stageId(), testRunResultDetails.testRunId());
        return testRunResult.map(testRunResultEntityMapper::toPersisted);
    }

    @Override
    public Optional<PersistedTestRunResult> getTestRunResult(TestRunReference testRunReference) {
        return repository.findById(testRunReference.testRunId())
                         .map(testRunResultEntityMapper::toPersisted);
    }

    @Override
    public Optional<PagedResult<PersistedTestRunResult>> listTestRuns(TestRunPage testRunPage) {
        StageDatabaseService.StageDetails stage = new StageDatabaseService.StageDetails(testRunPage.applicationId(),
                testRunPage.stageId(), true);
        if (stageDatabaseService.getStage(stage)
                                .isEmpty()) {
            return Optional.empty();
        }
        Pageable pageable = PageRequest.of(testRunPage.page(), testRunPage.size());
        Page<TestRunResultEntity> pagedEntities = repository.findByApplicationIdAndStageIdOrderByIdDesc(
                testRunPage.applicationId(), testRunPage.stageId(), pageable);
        Page<PersistedTestRunResult> runs = pagedEntities.map(testRunResultEntityMapper::toPersisted);
        PagedResult<PersistedTestRunResult> result = SpringPageMapper.toPagedResult(runs);
        return Optional.of(result);
    }

    @Override
    public Optional<PersistedTestRunStatistics> getTestRunStatistics(TestPlanStatistics testPlanStatistics) {
        PersistedTestPlanDetails testPlan = new PersistedTestPlanDetails(testPlanStatistics.applicationId(),
                testPlanStatistics.stageId(), testPlanStatistics.testPlanId(), true);
        if (testPlanDatabaseService.getPersistedTestPlan(testPlan)
                                   .isEmpty()) {
            return Optional.empty();
        }

        PersistedTestRunStatistics statistics = statisticsCalculator.calculate(testPlanStatistics.applicationId(),
                testPlanStatistics.stageId(), testPlanStatistics.testPlanId());

        return Optional.of(statistics);
    }

    @Override
    public Optional<PersistedTestRunHistory> getTestRunHistory(TestPlanHistory testPlanHistory) {
        PersistedTestPlanDetails testPlan = new PersistedTestPlanDetails(testPlanHistory.applicationId(),
                testPlanHistory.stageId(), testPlanHistory.testPlanId(), true);
        if (testPlanDatabaseService.getPersistedTestPlan(testPlan)
                                   .isEmpty()) {
            return Optional.empty();
        }

        PersistedTestRunStatistics statistics = statisticsCalculator.calculate(testPlanHistory.applicationId(),
                testPlanHistory.stageId(), testPlanHistory.testPlanId());
        Pageable pageable = PageRequest.of(testPlanHistory.page(), testPlanHistory.size());
        Page<PersistedTestRunResult> runs = repository.findByApplicationIdAndStageIdAndTestPlanIdOrderByTimestampAscIdAsc(
                testPlanHistory.applicationId(), testPlanHistory.stageId(), testPlanHistory.testPlanId(), pageable)
                                                      .map(testRunResultEntityMapper::toPersisted);

        PagedResult<PersistedTestRunResult> pagedRuns = SpringPageMapper.toPagedResult(runs);
        PersistedTestRunHistory history = new PersistedTestRunHistory(statistics.applicationId(), statistics.stageId(),
                statistics.testPlanId(), statistics.totalRuns(), statistics.successfulRuns(),
                statistics.partialSuccessRuns(), statistics.failedRuns(), pagedRuns);
        return Optional.of(history);
    }

    @Override
    @Transactional
    public PersistedTestRunResult storeTestRunResult(TestRunResult result) {
        TestRunResultEntity entity = testRunResultEntityMapper.toEntity(result);
        TestRunResultEntity storedTestRunResult = repository.save(entity);
        testRunResultWriter.writeResults(storedTestRunResult.getId(), result.testResults());
        return testRunResultEntityMapper.toPersisted(storedTestRunResult);
    }

}
