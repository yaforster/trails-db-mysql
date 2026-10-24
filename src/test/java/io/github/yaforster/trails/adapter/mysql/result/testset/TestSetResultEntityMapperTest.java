package io.github.yaforster.trails.adapter.mysql.result.testset;

import io.github.yaforster.trails.core.persisted.PersistedTestSetResult;
import io.github.yaforster.trails.core.test.Browser;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TestSetResultEntityMapperTest {

    private final TestSetResultEntityMapper mapper = new TestSetResultEntityMapper();

    @Test
    void toPersisted_ShouldMapAllFields() {
        Timestamp timestamp = Timestamp.valueOf("2026-01-02 03:04:05");
        TestCaseResultEntity testCaseResult = new TestCaseResultEntity().setTimestamp(timestamp)
                                                                        .setTestedInBrowser("FIREFOX");
        TestSetResultEntity entity = new TestSetResultEntity().setId(1L)
                                                              .setTestCaseResult(testCaseResult)
                                                              .setTotalRunTime(120)
                                                              .setTestRunId(200L)
                                                              .setBrowser("CHROME")
                                                              .setTestPlanLabel("plan-label");

        PersistedTestSetResult result = mapper.toPersisted(entity, 10L, 20L, 30L);

        assertEquals(1L, result.id());
        assertEquals(30L, result.testRunId());
        assertEquals(10L, result.applicationId());
        assertEquals(20L, result.stageId());
        assertEquals(120, result.totalRunTime());
        assertEquals(Browser.CHROME, result.browserToRunIn());
        assertEquals("plan-label", result.testPlanLabel());
        assertEquals(timestamp.toInstant()
                              .atOffset(ZoneOffset.UTC),
                result.testCaseTimestamp());
        assertEquals(Browser.FIREFOX, result.testedInBrowser());
    }

    @Test
    void toPersisted_ShouldFallbackTestedInBrowserToBrowserToRunIn_WhenTestCaseResultIsMissing() {
        TestSetResultEntity entity = new TestSetResultEntity().setId(2L)
                                                              .setTestCaseResult(null)
                                                              .setTotalRunTime(90)
                                                              .setBrowser("EDGE")
                                                              .setTestPlanLabel("plan");

        PersistedTestSetResult result = mapper.toPersisted(entity, 11L, 21L, 31L);

        assertEquals(Browser.EDGE, result.browserToRunIn());
        assertEquals(Browser.EDGE, result.testedInBrowser());
        assertEquals(PersistedTestSetResult.NO_TEST_CASE_TIMESTAMP, result.testCaseTimestamp());
    }

    @Test
    void toPersisted_ShouldFallbackTestedInBrowserToBrowserToRunIn_WhenTestCaseBrowserIsBlank() {
        Timestamp timestamp = Timestamp.valueOf("2026-05-06 07:08:09");
        TestCaseResultEntity testCaseResult = new TestCaseResultEntity().setTimestamp(timestamp)
                                                                        .setTestedInBrowser(" ");
        TestSetResultEntity entity = new TestSetResultEntity().setId(3L)
                                                              .setTestCaseResult(testCaseResult)
                                                              .setTotalRunTime(60)
                                                              .setBrowser("FIREFOX")
                                                              .setTestPlanLabel("plan");

        PersistedTestSetResult result = mapper.toPersisted(entity, 12L, 22L, 32L);

        assertEquals(Browser.FIREFOX, result.browserToRunIn());
        assertEquals(Browser.FIREFOX, result.testedInBrowser());
        assertEquals(OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC), result.testCaseTimestamp());
    }

    @Test
    void toPersisted_ShouldThrowForNullBrowserToRunIn() {
        TestSetResultEntity entity = new TestSetResultEntity().setBrowser(null);

        assertThrows(IllegalArgumentException.class, () -> mapper.toPersisted(entity, 1L, 2L, 3L));
    }

    @Test
    void toPersisted_ShouldThrowForUnsupportedBrowserToRunIn() {
        TestSetResultEntity entity = new TestSetResultEntity().setBrowser("SAFARI");

        assertThrows(IllegalArgumentException.class, () -> mapper.toPersisted(entity, 1L, 2L, 3L));
    }

}
