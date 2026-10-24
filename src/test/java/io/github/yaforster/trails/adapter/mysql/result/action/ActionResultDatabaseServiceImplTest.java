package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.app.services.ActionResultScreenshotQueryService;
import io.github.yaforster.trails.app.services.TestPathResultQueryService;
import io.github.yaforster.trails.core.persisted.PersistedActionResult;
import io.github.yaforster.trails.core.persisted.PersistedArtifactFile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActionResultDatabaseServiceImplTest {

    private final TestPathResultQueryService testPathResultQueryService = Mockito.mock(
            TestPathResultQueryService.class);
    private final ResultRepository resultRepository = Mockito.mock(ResultRepository.class);
    private final PersistedActionResultMapper persistedActionResultMapper = Mockito.mock(
            PersistedActionResultMapper.class);
    private final ResultScreenshotRepository resultScreenshotRepository = Mockito.mock(
            ResultScreenshotRepository.class);
    private final ActionResultDatabaseServiceImpl service = new ActionResultDatabaseServiceImpl(
            testPathResultQueryService, resultRepository, persistedActionResultMapper, resultScreenshotRepository);

    @Test
    void listActionResultChain_shouldReturnMappedItemsInOrder() {
        ResultEntity first = Mockito.mock(ResultEntity.class);
        ResultEntity second = Mockito.mock(ResultEntity.class);
        PersistedActionResult firstMapped = Mockito.mock(PersistedActionResult.class);
        PersistedActionResult secondMapped = Mockito.mock(PersistedActionResult.class);
        when(testPathResultQueryService.getPathResult(5L)).thenReturn(
                Optional.of(Mockito.mock(io.github.yaforster.trails.core.persisted.PersistedTestPathResult.class)));
        when(resultRepository.findByTestPathResultIdOrderByExecutionOrderAscIdAsc(5L)).thenReturn(
                List.of(first, second));
        when(persistedActionResultMapper.fromEntity(first)).thenReturn(firstMapped);
        when(persistedActionResultMapper.fromEntity(second)).thenReturn(secondMapped);

        Optional<List<PersistedActionResult>> result = service.listActionResultChain(5L);

        assertTrue(result.isPresent());
        assertEquals(List.of(firstMapped, secondMapped), result.get());
    }

    @Test
    void findInPath_shouldReturnScopedScreenshotWithFallbackContentType() {
        ResultEntity resultEntity = Mockito.mock(ResultEntity.class);
        when(resultEntity.getId()).thenReturn(22L);
        ResultScreenshotEntity screenshot = new ResultScreenshotEntity().setResultId(22L)
                                                                        .setFileName("action-result-22.png")
                                                                        .setContentType(" ")
                                                                        .setContent(new byte[]{1});
        when(resultRepository.findByIdAndTestPathResultId(22L, 10L)).thenReturn(Optional.of(resultEntity));
        when(resultScreenshotRepository.findByResultId(22L)).thenReturn(Optional.of(screenshot));

        Optional<PersistedArtifactFile> result = service.findInPath(
                new ActionResultScreenshotQueryService.PathActionScreenshot(10L, 22L));

        assertTrue(result.isPresent());
        assertEquals("image/png", result.get()
                                        .contentType());
    }

}
