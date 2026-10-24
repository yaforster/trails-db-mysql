package io.github.yaforster.trails.adapter.mysql.result.testset;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.app.services.TestRunResultDatabaseService;
import io.github.yaforster.trails.app.services.TestSetResultQueryService;
import io.github.yaforster.trails.core.persisted.PersistedTestSetResult;
import io.github.yaforster.trails.core.persisted.PersistedTestRunResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestSetResultDatabaseServiceImplTest {

    private final TestRunResultDatabaseService testRunResultDatabaseService = Mockito.mock(
            TestRunResultDatabaseService.class);
    private final TestSetResultRepository testSetResultRepository = Mockito.mock(TestSetResultRepository.class);
    private final TestSetResultEntityMapper mapper = Mockito.mock(TestSetResultEntityMapper.class);
    private final TestSetResultDatabaseServiceImpl service = new TestSetResultDatabaseServiceImpl(
            testRunResultDatabaseService, testSetResultRepository, mapper);

    @Test
    void listBrowserResults_ShouldReturnEmpty_WhenTestRunMissing() {
        when(testRunResultDatabaseService.getTestRunResult(
                new TestRunResultDatabaseService.TestRunResultDetails(1L, 2L, 3L))).thenReturn(Optional.empty());

        Optional<PagedResult<PersistedTestSetResult>> result = service.listBrowserResults(
                new TestSetResultQueryService.TestRunBrowserResults(1L, 2L, 3L, 0, 20));

        assertTrue(result.isEmpty());
        verify(testSetResultRepository, never()).findByTestRunIdOrderByIdAsc(any(), any());
    }

    @Test
    void listBrowserResults_ShouldMapPage_WhenTestRunExists() {
        TestSetResultEntity entity = new TestSetResultEntity().setId(1L);
        PersistedTestSetResult persisted = Mockito.mock(PersistedTestSetResult.class);
        when(testRunResultDatabaseService.getTestRunResult(
                new TestRunResultDatabaseService.TestRunResultDetails(1L, 2L, 3L))).thenReturn(
                        Optional.of(Mockito.mock(PersistedTestRunResult.class)));
        when(testSetResultRepository.findByTestRunIdOrderByIdAsc(3L, PageRequest.of(0, 5))).thenReturn(
                new PageImpl<>(List.of(entity), PageRequest.of(0, 5), 1));
        when(mapper.toPersisted(entity, 1L, 2L, 3L)).thenReturn(persisted);

        Optional<PagedResult<PersistedTestSetResult>> result = service.listBrowserResults(
                new TestSetResultQueryService.TestRunBrowserResults(1L, 2L, 3L, 0, 5));

        assertTrue(result.isPresent());
        assertEquals(persisted, result.get()
                                      .getContent()
                                      .getFirst());
    }

    @Test
    void getTestSetResult_ShouldMapUsingLoadedTestRunContext() {
        TestSetResultEntity testSetResult = new TestSetResultEntity().setId(1L)
                                                                     .setTestRunId(99L);
        PersistedTestRunResult testRun = new PersistedTestRunResult(99L, 10L, 20L, null, null, null, null);
        PersistedTestSetResult persisted = Mockito.mock(PersistedTestSetResult.class);
        when(testSetResultRepository.findById(1L)).thenReturn(Optional.of(testSetResult));
        when(testRunResultDatabaseService.getTestRunResult(
                new TestRunResultDatabaseService.TestRunReference(99L))).thenReturn(Optional.of(testRun));
        when(mapper.toPersisted(testSetResult, 10L, 20L, 99L)).thenReturn(persisted);

        Optional<PersistedTestSetResult> result = service.getTestSetResult(1L);

        assertTrue(result.isPresent());
        assertEquals(persisted, result.get());
    }

}
