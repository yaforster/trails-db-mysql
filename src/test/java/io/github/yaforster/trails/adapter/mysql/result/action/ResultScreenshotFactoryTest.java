package io.github.yaforster.trails.adapter.mysql.result.action;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;

class ResultScreenshotFactoryTest {

    private final ResultScreenshotFactory factory = new ResultScreenshotFactory();

    @Test
    void fromBase64_shouldDecodeDataUri() {
        Optional<ResultScreenshotEntity> result = factory.fromBase64(7L, "data:image/png;base64,AQID");

        assertTrue(result.isPresent());
        assertEquals(7L, result.get()
                               .getResultId());
        assertEquals("image/png", result.get()
                                        .getContentType());
        assertEquals("action-result-7.png", result.get()
                                                  .getFileName());
        assertArrayEquals(new byte[]{1, 2, 3}, result.get()
                                                     .getContent());
    }

    @Test
    void fromBase64_shouldReturnEmpty_whenInputInvalid() {
        Optional<ResultScreenshotEntity> result = factory.fromBase64(7L, "%%%");

        assertTrue(result.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"  ", "%%%", "data:image/png;base64,", "data:image/png;base64,%%%"})
    void shouldOmitScreenshot_whenContentIsAbsentOrUndecodable(String encoded) {
        ResultScreenshotFactory factory = new ResultScreenshotFactory();

        Optional<ResultScreenshotEntity> result = factory.fromBase64(7L, encoded);

        assertThat(result).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({"image/png,.png", "image/jpeg,.jpg", "image/jpg,.jpg", "image/webp,.webp", "image/gif,.gif",
            "image/JPEG,.jpg", "application/octet-stream,.png"})
    void shouldRetainMimeTypeAndChooseExtension_whenReadingDataUri(String contentType, String extension) {
        ResultScreenshotFactory factory = new ResultScreenshotFactory();
        String encoded = "data:" + contentType + ";base64,AQID";

        ResultScreenshotEntity result = factory.fromBase64(7L, encoded)
                                               .orElseThrow();

        assertThat(result)
                          .extracting(ResultScreenshotEntity::getResultId, ResultScreenshotEntity::getContentType,
                                  ResultScreenshotEntity::getFileName)
                          .containsExactly(7L, contentType, "action-result-7" + extension);
    }

    @ParameterizedTest
    @ValueSource(strings = {"AQID", "  AQID  ", "data:;base64,AQID", "data:,AQID"})
    void shouldDefaultToPng_whenMimeTypeIsAbsent(String encoded) {
        ResultScreenshotFactory factory = new ResultScreenshotFactory();

        ResultScreenshotEntity result = factory.fromBase64(7L, encoded)
                                               .orElseThrow();

        assertThat(result).extracting(ResultScreenshotEntity::getContentType, ResultScreenshotEntity::getFileName)
                          .containsExactly("image/png", "action-result-7.png");
    }

}
