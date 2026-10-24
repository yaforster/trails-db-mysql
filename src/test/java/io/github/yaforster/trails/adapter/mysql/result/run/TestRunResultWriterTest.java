package io.github.yaforster.trails.adapter.mysql.result.run;

import io.github.yaforster.trails.app.services.ActionResultPersistence;
import io.github.yaforster.trails.app.services.DownloadedFilePersistence;
import io.github.yaforster.trails.app.services.TestPathResultPersistence;
import io.github.yaforster.trails.app.services.TestSetResultPersistence;
import io.github.yaforster.trails.core.test.Browser;
import io.github.yaforster.trails.core.test.FileData;
import io.github.yaforster.trails.core.test.result.Success;
import io.github.yaforster.trails.core.test.TestCaseResult;
import io.github.yaforster.trails.core.test.TestPathResult;
import io.github.yaforster.trails.core.test.TestSetResult;
import java.sql.Timestamp;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestRunResultWriterTest {

    @Test
    void shouldRestartExecutionOrder_whenWritingAnotherPath() {
        TestSetResultPersistence sets = mock(TestSetResultPersistence.class);
        TestPathResultPersistence paths = mock(TestPathResultPersistence.class);
        ActionResultPersistence actions = mock(ActionResultPersistence.class);
        DownloadedFilePersistence downloads = mock(DownloadedFilePersistence.class);
        TestRunResultWriter writer = new TestRunResultWriter(sets, paths, actions, downloads);
        Success first = Success.builder()
                               .actionID(10L)
                               .label("first")
                               .resultMessage("ok")
                               .build();
        Success second = Success.builder()
                                .actionID(20L)
                                .label("second")
                                .resultMessage("ok")
                                .build();
        TestPathResult firstPath = new TestPathResult(List.of(first, second), List.of());
        TestPathResult secondPath = new TestPathResult(List.of(second), List.of());
        TestSetResult testSet = testSet(firstPath, secondPath);
        when(sets.store(any())).thenReturn(100L);
        when(paths.store(any())).thenReturn(200L, 300L);

        writer.writeResults(99L, List.of(testSet));

        ArgumentCaptor<ActionResultPersistence.ActionResult> storedActions = ArgumentCaptor.forClass(
                ActionResultPersistence.ActionResult.class);
        verify(actions, times(3)).store(storedActions.capture());
        assertThat(storedActions.getAllValues()).containsExactly(
                new ActionResultPersistence.ActionResult(200L, 0, first),
                new ActionResultPersistence.ActionResult(200L, 1, second),
                new ActionResultPersistence.ActionResult(300L, 0, second));
    }

    @Test
    void shouldRestartPathLabelsAndUseStoredSetIds_whenWritingAnotherTestSet() {
        TestSetResultPersistence sets = mock(TestSetResultPersistence.class);
        TestPathResultPersistence paths = mock(TestPathResultPersistence.class);
        ActionResultPersistence actions = mock(ActionResultPersistence.class);
        DownloadedFilePersistence downloads = mock(DownloadedFilePersistence.class);
        TestRunResultWriter writer = new TestRunResultWriter(sets, paths, actions, downloads);
        TestPathResult emptyPath = new TestPathResult(List.of(), List.of());
        TestSetResult firstSet = testSet(emptyPath, emptyPath);
        TestSetResult secondSet = testSet(emptyPath);
        when(sets.store(any())).thenReturn(100L, 200L);

        writer.writeResults(99L, List.of(firstSet, secondSet));

        ArgumentCaptor<TestPathResultPersistence.NamedTestPath> storedPaths = ArgumentCaptor.forClass(
                TestPathResultPersistence.NamedTestPath.class);
        verify(paths, times(3)).store(storedPaths.capture());
        assertThat(storedPaths.getAllValues()).containsExactly(
                new TestPathResultPersistence.NamedTestPath(100L, "Path 1"),
                new TestPathResultPersistence.NamedTestPath(100L, "Path 2"),
                new TestPathResultPersistence.NamedTestPath(200L, "Path 1"));
    }

    @Test
    void shouldAttachDownloadsToTheirStoredPath_whenWritingMultiplePaths() {
        TestSetResultPersistence sets = mock(TestSetResultPersistence.class);
        TestPathResultPersistence paths = mock(TestPathResultPersistence.class);
        ActionResultPersistence actions = mock(ActionResultPersistence.class);
        DownloadedFilePersistence downloads = mock(DownloadedFilePersistence.class);
        TestRunResultWriter writer = new TestRunResultWriter(sets, paths, actions, downloads);
        FileData firstFile = new FileData(new byte[]{1}, "first.txt");
        FileData secondFile = new FileData(new byte[]{2}, "second.txt");
        TestPathResult firstPath = new TestPathResult(List.of(), List.of(firstFile));
        TestPathResult secondPath = new TestPathResult(List.of(), List.of(secondFile));
        TestSetResult testSet = testSet(firstPath, secondPath);
        when(sets.store(any())).thenReturn(100L);
        when(paths.store(any())).thenReturn(200L, 300L);

        writer.writeResults(99L, List.of(testSet));

        ArgumentCaptor<DownloadedFilePersistence.DownloadedFile> storedFiles = ArgumentCaptor.forClass(
                DownloadedFilePersistence.DownloadedFile.class);
        verify(downloads, times(2)).store(storedFiles.capture());
        assertThat(storedFiles.getAllValues()).containsExactly(
                new DownloadedFilePersistence.DownloadedFile(200L, firstFile),
                new DownloadedFilePersistence.DownloadedFile(300L, secondFile));
    }

    @Test
    void shouldPropagatePersistenceFailure_whenStoringAPathFails() {
        TestSetResultPersistence sets = mock(TestSetResultPersistence.class);
        TestPathResultPersistence paths = mock(TestPathResultPersistence.class);
        ActionResultPersistence actions = mock(ActionResultPersistence.class);
        DownloadedFilePersistence downloads = mock(DownloadedFilePersistence.class);
        TestRunResultWriter writer = new TestRunResultWriter(sets, paths, actions, downloads);
        TestPathResult emptyPath = new TestPathResult(List.of(), List.of());
        TestSetResult testSet = testSet(emptyPath);
        IllegalStateException failure = new IllegalStateException("storage unavailable");
        when(paths.store(any())).thenThrow(failure);

        assertThatThrownBy(() -> writer.writeResults(99L, List.of(testSet))).isSameAs(failure);
    }

    private TestSetResult testSet(TestPathResult... paths) {
        Timestamp timestamp = Timestamp.valueOf("2026-01-01 00:00:00");
        List<TestPathResult> results = List.of(paths);
        TestCaseResult testCase = new TestCaseResult(timestamp, Browser.CHROME, results);
        return new TestSetResult(testCase, 1, Browser.CHROME, "plan");
    }
}
