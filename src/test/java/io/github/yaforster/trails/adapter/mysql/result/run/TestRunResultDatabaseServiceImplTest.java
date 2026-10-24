package io.github.yaforster.trails.adapter.mysql.result.run;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.app.services.ActionResultPersistence;
import io.github.yaforster.trails.app.services.DownloadedFilePersistence;
import io.github.yaforster.trails.app.services.StageDatabaseService;
import io.github.yaforster.trails.app.services.TestPlanDatabaseService;
import io.github.yaforster.trails.app.services.TestPlanDatabaseService.PersistedTestPlanDetails;
import io.github.yaforster.trails.app.services.TestPathResultPersistence;
import io.github.yaforster.trails.app.services.TestSetResultPersistence;
import io.github.yaforster.trails.app.services.TestRunResultDatabaseService.TestPlanHistory;
import io.github.yaforster.trails.app.services.TestRunResultDatabaseService.TestPlanStatistics;
import io.github.yaforster.trails.app.services.TestRunResultDatabaseService.TestRunPage;
import io.github.yaforster.trails.app.services.TestRunResultDatabaseService.TestRunResultDetails;
import io.github.yaforster.trails.core.data.ResultIndicator;
import io.github.yaforster.trails.core.persisted.PersistedTestRunHistory;
import io.github.yaforster.trails.core.persisted.PersistedTestPlan;
import io.github.yaforster.trails.core.persisted.PersistedTestRunResult;
import io.github.yaforster.trails.core.persisted.PersistedTestRunStatistics;
import io.github.yaforster.trails.core.persisted.PersistedStage;
import io.github.yaforster.trails.core.test.*;
import io.github.yaforster.trails.core.test.result.Success;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestRunResultDatabaseServiceImplTest {

    private final TestRunResultRepository repository = Mockito.mock(TestRunResultRepository.class);
    private final TestRunResultEntityMapper mapper = Mockito.mock(TestRunResultEntityMapper.class);
    private final StageDatabaseService stageDatabaseService = Mockito.mock(StageDatabaseService.class);
    private final TestPlanDatabaseService testPlanDatabaseService = Mockito.mock(TestPlanDatabaseService.class);
    private final TestSetResultPersistence testSetResultPersistence = Mockito.mock(TestSetResultPersistence.class);
    private final TestPathResultPersistence testPathResultPersistence = Mockito.mock(TestPathResultPersistence.class);
    private final ActionResultPersistence actionResultPersistence = Mockito.mock(ActionResultPersistence.class);
    private final DownloadedFilePersistence downloadedFilePersistence = Mockito.mock(DownloadedFilePersistence.class);
    private final TestRunStatisticsCalculator statisticsCalculator = new TestRunStatisticsCalculator(repository);
    private final TestRunResultWriter testRunResultWriter = new TestRunResultWriter(testSetResultPersistence,
            testPathResultPersistence, actionResultPersistence, downloadedFilePersistence);
    private final TestRunResultDatabaseServiceImpl service = new TestRunResultDatabaseServiceImpl(repository, mapper,
            stageDatabaseService, testPlanDatabaseService, statisticsCalculator, testRunResultWriter);

    @Test
    void listTestRuns_ShouldReturnEmpty_WhenStageMissing() {
        when(stageDatabaseService.getStage(new StageDatabaseService.StageDetails(1L, 2L, true))).thenReturn(
                Optional.empty());

        Optional<PagedResult<PersistedTestRunResult>> result = service.listTestRuns(new TestRunPage(1L, 2L, 0, 20));

        assertTrue(result.isEmpty());
        verify(repository, never()).findByApplicationIdAndStageIdOrderByIdDesc(any(), any(), any());
    }

    @Test
    void listTestRuns_ShouldMapPage_WhenStageExists() {
        TestRunResultEntity entity = new TestRunResultEntity().setId(1L);
        PersistedTestRunResult persisted = Mockito.mock(PersistedTestRunResult.class);
        when(stageDatabaseService.getStage(new StageDatabaseService.StageDetails(1L, 2L, true))).thenReturn(
                Optional.of(Mockito.mock(PersistedStage.class)));
        when(repository.findByApplicationIdAndStageIdOrderByIdDesc(1L, 2L, PageRequest.of(1, 5))).thenReturn(
                new PageImpl<>(List.of(entity)));
        when(mapper.toPersisted(entity)).thenReturn(persisted);

        Optional<PagedResult<PersistedTestRunResult>> result = service.listTestRuns(new TestRunPage(1L, 2L, 1, 5));

        assertTrue(result.isPresent());
        assertEquals(persisted, result.get()
                                      .getContent()
                                      .getFirst());
    }

    @Test
    void storeTestRunResult_ShouldDelegateToMapperAndRepository() {
        TestRunResult input = Mockito.mock(TestRunResult.class);
        TestRunResultEntity entity = new TestRunResultEntity();
        TestRunResultEntity saved = new TestRunResultEntity().setId(99L);
        PersistedTestRunResult persisted = Mockito.mock(PersistedTestRunResult.class);
        when(mapper.toEntity(input)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toPersisted(saved)).thenReturn(persisted);

        PersistedTestRunResult result = service.storeTestRunResult(input);

        assertEquals(persisted, result);
    }

    @Test
    void getTestRunResult_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.empty());

        Optional<PersistedTestRunResult> result = service.getTestRunResult(new TestRunResultDetails(1L, 2L, 3L));

        assertTrue(result.isEmpty());
        verify(mapper, never()).toPersisted(any());
    }

    @Test
    void getTestRunResult_ShouldMapResult_WhenFound() {
        TestRunResultEntity entity = new TestRunResultEntity().setId(3L);
        PersistedTestRunResult persisted = Mockito.mock(PersistedTestRunResult.class);
        when(repository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.of(entity));
        when(mapper.toPersisted(entity)).thenReturn(persisted);

        Optional<PersistedTestRunResult> result = service.getTestRunResult(new TestRunResultDetails(1L, 2L, 3L));

        assertTrue(result.isPresent());
        assertEquals(persisted, result.get());
    }

    @Test
    void storeTestRunResult_ShouldStoreActionResultsThroughApplicationPort() {
        Success success = Success.builder()
                                 .actionID(10L)
                                 .label("action")
                                 .resultMessage("ok")
                                 .build();

        TestPathResult testPathResult = new TestPathResult(List.of(success), List.of());
        TestCaseResult testCaseResult = new TestCaseResult(new Timestamp(System.currentTimeMillis()), Browser.CHROME,
                List.of(testPathResult));
        TestSetResult testSetResult = new TestSetResult(testCaseResult, 1, Browser.CHROME, "plan");
        TestRunResult input = Mockito.mock(TestRunResult.class);

        TestRunResultEntity entity = new TestRunResultEntity();
        TestRunResultEntity saved = new TestRunResultEntity().setId(99L);
        PersistedTestRunResult persisted = Mockito.mock(PersistedTestRunResult.class);

        when(input.testResults()).thenReturn(List.of(testSetResult));
        when(mapper.toEntity(input)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(mapper.toPersisted(saved)).thenReturn(persisted);
        when(testSetResultPersistence.store(any())).thenReturn(200L);
        when(testPathResultPersistence.store(any())).thenReturn(300L);
        service.storeTestRunResult(input);

        ArgumentCaptor<ActionResultPersistence.ActionResult> captor = ArgumentCaptor.forClass(
                ActionResultPersistence.ActionResult.class);
        verify(actionResultPersistence).store(captor.capture());
        assertEquals(10L, captor.getValue()
                                .result()
                                .getActionID());
    }

    @Test
    void getTestRunStatistics_shouldReturnEmpty_whenTestPlanIsMissing() {
        when(testPlanDatabaseService.getPersistedTestPlan(new PersistedTestPlanDetails(1L, 2L, 3L, true))).thenReturn(
                Optional.empty());

        Optional<PersistedTestRunStatistics> result = service.getTestRunStatistics(new TestPlanStatistics(1L, 2L, 3L));

        assertTrue(result.isEmpty());
        verify(repository, never()).countByApplicationIdAndStageIdAndTestPlanId(any(), any(), any());
    }

    @Test
    void getTestRunStatistics_shouldAggregateCounts() {
        when(testPlanDatabaseService.getPersistedTestPlan(new PersistedTestPlanDetails(1L, 2L, 3L, true))).thenReturn(
                Optional.of(Mockito.mock(PersistedTestPlan.class)));
        when(repository.countByApplicationIdAndStageIdAndTestPlanId(1L, 2L, 3L)).thenReturn(10L);
        when(repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(1L, 2L, 3L,
                TestRunStatus.SUCCESS)).thenReturn(4L);
        when(repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(1L, 2L, 3L,
                TestRunStatus.PARTIAL_SUCCESS)).thenReturn(3L);
        when(repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(1L, 2L, 3L,
                TestRunStatus.FAILURE)).thenReturn(3L);

        Optional<PersistedTestRunStatistics> result = service.getTestRunStatistics(new TestPlanStatistics(1L, 2L, 3L));

        assertTrue(result.isPresent());
        assertEquals(1L, result.get()
                               .applicationId());
        assertEquals(2L, result.get()
                               .stageId());
        assertEquals(3L, result.get()
                               .testPlanId());
        assertEquals(10L, result.get()
                                .totalRuns());
        assertEquals(4L, result.get()
                               .successfulRuns());
        assertEquals(3L, result.get()
                               .partialSuccessRuns());
        assertEquals(3L, result.get()
                               .failedRuns());
    }

    @Test
    void getTestRunHistory_shouldReturnEmpty_whenTestPlanIsMissing() {
        when(testPlanDatabaseService.getPersistedTestPlan(new PersistedTestPlanDetails(1L, 2L, 3L, true))).thenReturn(
                Optional.empty());

        Optional<PersistedTestRunHistory> result = service.getTestRunHistory(new TestPlanHistory(1L, 2L, 3L, 0, 50));

        assertTrue(result.isEmpty());
        verify(repository, never()).findByApplicationIdAndStageIdAndTestPlanIdOrderByTimestampAscIdAsc(any(), any(),
                any(), any());
    }

    @Test
    void getTestRunHistory_shouldReturnChronologicalRunsAndCounts() {
        TestRunResultEntity newestEntity = new TestRunResultEntity().setId(2L);
        TestRunResultEntity oldestEntity = new TestRunResultEntity().setId(1L);
        PersistedTestRunResult newest = new PersistedTestRunResult(2L, 1L, 2L, 3L,
                Timestamp.valueOf("2026-01-02 00:00:00"), ResultIndicator.FAILURE, "newest");
        PersistedTestRunResult oldest = new PersistedTestRunResult(1L, 1L, 2L, 3L,
                Timestamp.valueOf("2026-01-01 00:00:00"), ResultIndicator.SUCCESS, "oldest");

        when(testPlanDatabaseService.getPersistedTestPlan(new PersistedTestPlanDetails(1L, 2L, 3L, true))).thenReturn(
                Optional.of(Mockito.mock(PersistedTestPlan.class)));
        when(repository.countByApplicationIdAndStageIdAndTestPlanId(1L, 2L, 3L)).thenReturn(2L);
        when(repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(1L, 2L, 3L,
                TestRunStatus.SUCCESS)).thenReturn(1L);
        when(repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(1L, 2L, 3L,
                TestRunStatus.PARTIAL_SUCCESS)).thenReturn(0L);
        when(repository.countByApplicationIdAndStageIdAndTestPlanIdAndStatus(1L, 2L, 3L,
                TestRunStatus.FAILURE)).thenReturn(1L);
        when(repository.findByApplicationIdAndStageIdAndTestPlanIdOrderByTimestampAscIdAsc(1L, 2L, 3L,
                PageRequest.of(0, 2))).thenReturn(
                        new PageImpl<>(List.of(oldestEntity, newestEntity), PageRequest.of(0, 2), 2));
        when(mapper.toPersisted(newestEntity)).thenReturn(newest);
        when(mapper.toPersisted(oldestEntity)).thenReturn(oldest);

        Optional<PersistedTestRunHistory> result = service.getTestRunHistory(new TestPlanHistory(1L, 2L, 3L, 0, 2));

        assertTrue(result.isPresent());
        assertEquals(2L, result.get()
                               .totalRuns());
        assertEquals(1L, result.get()
                               .successfulRuns());
        assertEquals(0L, result.get()
                               .partialSuccessRuns());
        assertEquals(1L, result.get()
                               .failedRuns());
        assertEquals(0, result.get()
                              .items()
                              .getNumber());
        assertEquals(2, result.get()
                              .items()
                              .getSize());
        assertEquals(2, result.get()
                              .items()
                              .getTotalElements());
        assertEquals(List.of(oldest, newest), result.get()
                                                    .items()
                                                    .getContent());
    }

}
