package io.github.yaforster.trails.adapter.mysql.result.testset;

import io.github.yaforster.trails.adapter.mysql.SpringPageMapper;
import io.github.yaforster.trails.app.services.TestRunResultDatabaseService;
import io.github.yaforster.trails.app.services.TestSetResultQueryService;
import io.github.yaforster.trails.core.PagedResult;
import io.github.yaforster.trails.core.persisted.PersistedTestSetResult;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class TestSetResultDatabaseServiceImpl implements TestSetResultQueryService {

    private final TestRunResultDatabaseService testRunResultDatabaseService;
    private final TestSetResultRepository testSetResultRepository;
    private final TestSetResultEntityMapper testSetResultEntityMapper;

    @Override
    public Optional<PagedResult<PersistedTestSetResult>> listBrowserResults(
            TestRunBrowserResults testRunBrowserResults) {
        TestRunResultDatabaseService.TestRunResultDetails runDetails = new TestRunResultDatabaseService.TestRunResultDetails(
                testRunBrowserResults.applicationId(), testRunBrowserResults.stageId(),
                testRunBrowserResults.testRunId());
        if (testRunResultDatabaseService.getTestRunResult(runDetails)
                                        .isEmpty()) {
            return Optional.empty();
        }
        Pageable pageable = PageRequest.of(testRunBrowserResults.page(), testRunBrowserResults.size());
        Page<PersistedTestSetResult> browserResults = testSetResultRepository.findByTestRunIdOrderByIdAsc(
                testRunBrowserResults.testRunId(), pageable)
                                                                             .map(entity -> testSetResultEntityMapper.toPersisted(
                                                                                     entity,
                                                                                     testRunBrowserResults.applicationId(),
                                                                                     testRunBrowserResults.stageId(),
                                                                                     testRunBrowserResults.testRunId()));
        PagedResult<PersistedTestSetResult> result = SpringPageMapper.toPagedResult(browserResults);
        return Optional.of(result);
    }

    @Override
    public Optional<PersistedTestSetResult> getTestSetResult(Long testSetResultId) {
        return testSetResultRepository.findById(testSetResultId)
                                      .flatMap(this::includeRunContext);
    }

    private Optional<PersistedTestSetResult> includeRunContext(TestSetResultEntity testSetResult) {
        TestRunResultDatabaseService.TestRunReference runReference = new TestRunResultDatabaseService.TestRunReference(
                testSetResult.getTestRunId());
        return testRunResultDatabaseService.getTestRunResult(runReference)
                                           .map(testRunResult -> testSetResultEntityMapper.toPersisted(testSetResult,
                                                   testRunResult.applicationId(), testRunResult.stageId(),
                                                   testRunResult.id()));
    }

}
