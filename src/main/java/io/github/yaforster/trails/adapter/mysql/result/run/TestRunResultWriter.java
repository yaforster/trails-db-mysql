package io.github.yaforster.trails.adapter.mysql.result.run;

import io.github.yaforster.trails.app.services.ActionResultPersistence;
import io.github.yaforster.trails.app.services.DownloadedFilePersistence;
import io.github.yaforster.trails.app.services.TestPathResultPersistence;
import io.github.yaforster.trails.app.services.TestSetResultPersistence;
import io.github.yaforster.trails.core.test.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class TestRunResultWriter {

    private final TestSetResultPersistence testSetResultPersistence;
    private final TestPathResultPersistence testPathResultPersistence;
    private final ActionResultPersistence actionResultPersistence;
    private final DownloadedFilePersistence downloadedFilePersistence;

    public void writeResults(Long testRunId, List<TestSetResult> testSetResults) {
        if (testSetResults == null || testSetResults.isEmpty()) {
            return;
        }
        for (TestSetResult testSetResult : testSetResults) {
            writeTestSetResult(testRunId, testSetResult);
        }
    }

    private void writeTestSetResult(Long testRunId, TestSetResult testSetResult) {
        TestSetResultPersistence.TestSetResultToStore resultToStore = new TestSetResultPersistence.TestSetResultToStore(
                testRunId, testSetResult);
        Long testSetResultId = testSetResultPersistence.store(resultToStore);
        TestCaseResult testCaseResult = testSetResult.testCaseResult();
        writePathResults(testSetResultId, testCaseResult);
    }

    private void writePathResults(Long testSetResultId, TestCaseResult testCaseResult) {
        if (testCaseResult == null || testCaseResult.results() == null || testCaseResult.results()
                                                                                        .isEmpty()) {
            return;
        }

        int pathIndex = 1;
        for (TestPathResult testPathResult : testCaseResult.results()) {
            String pathLabel = "Path " + pathIndex;
            writePathResult(testSetResultId, pathLabel, testPathResult);
            pathIndex++;
        }
    }

    private void writePathResult(Long testSetResultId, String pathLabel, TestPathResult testPathResult) {
        TestPathResultPersistence.NamedTestPath pathToStore = new TestPathResultPersistence.NamedTestPath(
                testSetResultId, pathLabel);
        Long testPathResultId = testPathResultPersistence.store(pathToStore);
        List<Result> actionResults = testPathResult.results();
        writeActionResults(testPathResultId, actionResults);
        List<FileData> downloadedFiles = testPathResult.downloadedFiles();
        writeDownloadedFiles(testPathResultId, downloadedFiles);
    }

    private void writeActionResults(Long testPathResultId, List<Result> results) {
        if (results == null || results.isEmpty()) {
            return;
        }
        for (int executionOrder = 0; executionOrder < results.size(); executionOrder++) {
            Result result = results.get(executionOrder);
            ActionResultPersistence.ActionResult actionResult = new ActionResultPersistence.ActionResult(
                    testPathResultId, executionOrder, result);
            actionResultPersistence.store(actionResult);
        }
    }

    private void writeDownloadedFiles(Long testPathResultId, List<FileData> downloadedFiles) {
        if (downloadedFiles == null || downloadedFiles.isEmpty()) {
            return;
        }
        for (FileData fileData : downloadedFiles) {
            DownloadedFilePersistence.DownloadedFile downloadedFile = new DownloadedFilePersistence.DownloadedFile(
                    testPathResultId, fileData);
            downloadedFilePersistence.store(downloadedFile);
        }
    }

}
