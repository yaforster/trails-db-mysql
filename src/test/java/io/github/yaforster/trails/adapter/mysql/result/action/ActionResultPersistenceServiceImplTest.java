package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.app.services.ActionResultPersistence;
import io.github.yaforster.trails.core.test.result.Success;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ActionResultPersistenceServiceImplTest {

    private final ResultRepository resultRepository = Mockito.mock(ResultRepository.class);
    private final ResultScreenshotRepository resultScreenshotRepository = Mockito.mock(
            ResultScreenshotRepository.class);
    private final ResultScreenshotFactory resultScreenshotFactory = new ResultScreenshotFactory();
    private final ResultEntityMapper resultEntityMapper = Mockito.mock(ResultEntityMapper.class);
    private final ActionResultPersistenceServiceImpl persistence = new ActionResultPersistenceServiceImpl(
            resultRepository, resultScreenshotRepository, resultScreenshotFactory, resultEntityMapper);

    @Test
    void store_shouldPersistDecodedScreenshot() {
        Success success = Success.builder()
                                 .actionID(10L)
                                 .base64Screenshot("data:image/png;base64,AQID")
                                 .build();
        ResultEntity storedResult = new SuccessEntity().setId(22L);
        when(resultEntityMapper.toEntity(5L, 0, success)).thenReturn(storedResult);
        when(resultRepository.save(storedResult)).thenReturn(storedResult);

        persistence.store(new ActionResultPersistence.ActionResult(5L, 0, success));

        verify(resultScreenshotRepository).save(Mockito.argThat(screenshot -> screenshot.getResultId()
                                                                                        .equals(22L)
                && screenshot.getContentType()
                             .equals("image/png")
                && screenshot.getContent().length == 3));
    }

}
