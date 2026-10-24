package io.github.yaforster.trails.adapter.mysql.result.testset;

import io.github.yaforster.trails.core.persisted.PersistedTestSetResult;
import io.github.yaforster.trails.core.test.Browser;
import io.github.yaforster.trails.core.test.TestCaseResult;
import io.github.yaforster.trails.core.test.TestSetResult;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Component
public class TestSetResultEntityMapper {

    public TestSetResultEntity toEntity(Long testRunId, TestSetResult testSetResult) {
        TestCaseResultEntity testCase = toEntity(testSetResult.testCaseResult());
        String browserName = Optional.ofNullable(testSetResult.browserToRunIn())
                                     .map(Browser::name)
                                     .orElse(null);
        return new TestSetResultEntity().setTestCaseResult(testCase)
                                        .setTotalRunTime(testSetResult.totalRunTime())
                                        .setTestRunId(testRunId)
                                        .setBrowser(browserName)
                                        .setTestPlanLabel(testSetResult.testPlanLabel());
    }

    public PersistedTestSetResult toPersisted(TestSetResultEntity entity, Long applicationId, Long stageId,
            Long testRunId) {
        Browser browserToRunIn = Browser.parse(entity.getBrowser());
        OffsetDateTime timestamp = Optional.ofNullable(entity.getTestCaseResult())
                                           .map(TestCaseResultEntity::getTimestamp)
                                           .map(value -> value.toInstant()
                                                              .atOffset(ZoneOffset.UTC))
                                           .orElse(PersistedTestSetResult.NO_TEST_CASE_TIMESTAMP);
        Browser testedInBrowser = Optional.ofNullable(entity.getTestCaseResult())
                                          .map(TestCaseResultEntity::getTestedInBrowser)
                                          .filter(browser -> !browser.isBlank())
                                          .map(Browser::parse)
                                          .orElse(browserToRunIn);

        return new PersistedTestSetResult(entity.getId(), testRunId, applicationId, stageId, entity.getTotalRunTime(),
                browserToRunIn, entity.getTestPlanLabel(), timestamp, testedInBrowser);
    }

    private TestCaseResultEntity toEntity(TestCaseResult testCaseResult) {
        if (testCaseResult == null) {
            return null;
        }
        String browserName = Optional.ofNullable(testCaseResult.testedInBrowser())
                                     .map(Browser::name)
                                     .orElse(null);
        return new TestCaseResultEntity().setTimestamp(testCaseResult.timestamp())
                                         .setTestedInBrowser(browserName);
    }

}
