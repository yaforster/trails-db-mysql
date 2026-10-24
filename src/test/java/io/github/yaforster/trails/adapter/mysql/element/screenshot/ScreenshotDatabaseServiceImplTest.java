package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.app.services.ScreenshotDatabaseService;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.data.Screenshot;
import io.github.yaforster.trails.core.data.TrailsScreenshotFile;
import io.github.yaforster.trails.core.deletion.DatabaseDeletionResult;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScreenshotDatabaseServiceImplTest extends TrailsTest {

    private final ScreenshotRepository repository = Mockito.mock(ScreenshotRepository.class);
    private final ScreenshotDatabaseServiceImpl service = new ScreenshotDatabaseServiceImpl(repository);

    @Test
    void findIdByElementId_ShouldReturnMappedId() {
        ElementScreenshotEntity entity = new ElementScreenshotEntity().setId(11L);
        when(repository.findByElementId(7L)).thenReturn(Optional.of(entity));

        Optional<Long> result = service.findIdByElementId(7L);

        assertTrue(result.isPresent());
        assertEquals(11L, result.get());
    }

    @Test
    void findIdByElementId_ShouldReturnEmpty_WhenMissing() {
        when(repository.findByElementId(7L)).thenReturn(Optional.empty());

        Optional<Long> result = service.findIdByElementId(7L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAllIdsByElementIds_ShouldReturnEmptyMap_WhenInputEmpty() {
        Map<Long, Long> result = service.findAllIdsByElementIds(
                new ScreenshotDatabaseService.ElementScreenshotIds(List.of()));

        assertTrue(result.isEmpty());
        verify(repository, never()).findIdsByElementIds(any());
    }

    @Test
    void findAllIdsByElementIds_ShouldMapRows() {
        when(repository.findIdsByElementIds(List.of(1L, 2L))).thenReturn(
                List.of(new Object[]{1L, 10L}, new Object[]{2L, 20L}));

        Map<Long, Long> result = service.findAllIdsByElementIds(
                new ScreenshotDatabaseService.ElementScreenshotIds(List.of(1L, 2L)));

        assertEquals(Map.of(1L, 10L, 2L, 20L), result);
    }

    @Test
    void setScreenshot_ShouldCreateNewAndSave() {
        byte[] content = pngBytes();
        TrailsScreenshotFile file = screenshotFile("s.png", content);
        when(repository.findByElementId(1L)).thenReturn(Optional.empty());

        service.setScreenshot(new ScreenshotDatabaseService.ElementScreenshot(1L, file));

        verify(repository).save(argThat(entity -> entity.getElementId()
                                                        .equals(1L)
                && "s.png".equals(entity.getFileName()) && "image/png".equals(entity.getContentType())
                && entity.getContent().length == content.length));
    }

    @Test
    void setScreenshot_ShouldUpdateExistingEntity() {
        ElementScreenshotEntity existing = new ElementScreenshotEntity().setId(50L)
                                                                        .setElementId(1L);
        byte[] content = pngBytes();
        TrailsScreenshotFile file = screenshotFile("updated.png", content);
        when(repository.findByElementId(1L)).thenReturn(Optional.of(existing));

        service.setScreenshot(new ScreenshotDatabaseService.ElementScreenshot(1L, file));

        verify(repository).save(argThat(entity -> entity.getId()
                                                        .equals(50L)
                && entity.getElementId()
                         .equals(1L)
                && "updated.png".equals(entity.getFileName()) && "image/png".equals(entity.getContentType())
                && entity.getContent().length == content.length));
    }

    @Test
    void loadScreenshot_ShouldMapEntity() {
        ElementScreenshotEntity entity = new ElementScreenshotEntity().setId(5L)
                                                                      .setFileName("a.png")
                                                                      .setContentType("image/png")
                                                                      .setContent(new byte[]{9});
        when(repository.findById(5L)).thenReturn(Optional.of(entity));

        Optional<Screenshot> result = service.loadScreenshot(5L);

        assertTrue(result.isPresent());
        assertEquals("a.png", result.get()
                                    .fileName());
        assertEquals(1L, result.get()
                               .size());
        assertEquals("image/png", result.get()
                                        .contentType());
    }

    @Test
    void loadScreenshot_ShouldReturnEmpty_WhenMissing() {
        when(repository.findById(5L)).thenReturn(Optional.empty());

        Optional<Screenshot> result = service.loadScreenshot(5L);

        assertTrue(result.isEmpty());
    }

    @Test
    void loadScreenshotByElementId_ShouldMapEntity() {
        ElementScreenshotEntity entity = new ElementScreenshotEntity().setId(6L)
                                                                      .setFileName("b.png")
                                                                      .setContentType("image/png")
                                                                      .setContent(new byte[]{1, 2, 3});
        when(repository.findByElementId(9L)).thenReturn(Optional.of(entity));

        Optional<Screenshot> result = service.loadScreenshotByElementId(9L);

        assertTrue(result.isPresent());
        assertEquals("b.png", result.get()
                                    .fileName());
        assertEquals(3L, result.get()
                               .size());
        assertEquals("image/png", result.get()
                                        .contentType());
    }

    @Test
    void loadScreenshotByElementId_ShouldReturnEmpty_WhenMissing() {
        when(repository.findByElementId(9L)).thenReturn(Optional.empty());

        Optional<Screenshot> result = service.loadScreenshotByElementId(9L);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteElementScreenshot_ShouldReturnNotFound_WhenScreenshotMissing() {
        when(repository.findByElementId(9L)).thenReturn(Optional.empty());

        DatabaseDeletionResult result = service.deleteElementScreenshot(9L);

        assertInstanceOf(DeletionNotFound.class, result);
    }

    @Test
    void deleteElementScreenshot_ShouldReturnSuccess_WhenDeleteWorks() {
        ElementScreenshotEntity entity = new ElementScreenshotEntity().setId(6L)
                                                                      .setElementId(9L);
        when(repository.findByElementId(9L)).thenReturn(Optional.of(entity));

        DatabaseDeletionResult result = service.deleteElementScreenshot(9L);

        assertInstanceOf(DeletionSuccess.class, result);
        verify(repository).delete(entity);
    }

    @Test
    void deleteElementScreenshot_ShouldReturnFailure_WhenDeleteThrows() {
        ElementScreenshotEntity entity = new ElementScreenshotEntity().setId(6L)
                                                                      .setElementId(9L);
        when(repository.findByElementId(9L)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException("boom")).when(repository)
                                             .delete(entity);

        DatabaseDeletionResult result = service.deleteElementScreenshot(9L);

        assertInstanceOf(DeletionFailure.class, result);
    }

}
