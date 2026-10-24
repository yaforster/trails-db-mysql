package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.app.services.ScreenshotDatabaseService;
import io.github.yaforster.trails.core.BinaryContent;
import io.github.yaforster.trails.core.data.Screenshot;
import io.github.yaforster.trails.core.data.TrailsScreenshotFile;
import io.github.yaforster.trails.core.deletion.DatabaseDeletionResult;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ScreenshotDatabaseServiceImpl implements ScreenshotDatabaseService {

    private final ScreenshotRepository repository;

    @Override
    public Optional<Long> findIdByElementId(Long elementId) {
        return repository.findByElementId(elementId)
                         .map(ElementScreenshotEntity::getId);
    }

    @Override
    public Optional<Screenshot> loadScreenshot(Long screenshotID) {
        return repository.findById(screenshotID)
                         .map(this::toScreenshot);
    }

    @Override
    public Optional<Screenshot> loadScreenshotByElementId(Long elementId) {
        return repository.findByElementId(elementId)
                         .map(this::toScreenshot);
    }

    @Override
    public DatabaseDeletionResult deleteElementScreenshot(Long elementID) {
        Optional<ElementScreenshotEntity> entity = repository.findByElementId(elementID);
        if (entity.isEmpty()) {
            return new DeletionNotFound(elementID);
        }
        try {
            repository.delete(entity.get());
            return new DeletionSuccess(elementID);
        } catch (Exception exception) {
            return new DeletionFailure(elementID, exception);
        }
    }

    @Override
    public Map<Long, Long> findAllIdsByElementIds(ElementScreenshotIds elementScreenshotIds) {
        if (elementScreenshotIds.elementIds()
                                .isEmpty()) {
            return Map.of();
        }
        return repository.findIdsByElementIds(elementScreenshotIds.elementIds())
                         .stream()
                         .collect(Collectors.toMap(row -> (Long) row[0], // elementId
                                 row -> (Long) row[1] // screenshotId
                         ));
    }

    @Override
    @Transactional
    public void setScreenshot(ElementScreenshot elementScreenshot) {
        ElementScreenshotEntity entity = repository.findByElementId(elementScreenshot.elementId())
                                                   .orElseGet(ElementScreenshotEntity::new);
        TrailsScreenshotFile file = elementScreenshot.file();
        String contentType = file.imageType()
                                 .contentType();
        entity.setElementId(elementScreenshot.elementId());
        entity.setFileName(file.filename());
        entity.setContentType(contentType);
        entity.setContent(file.content());
        repository.save(entity);
    }

    private Screenshot toScreenshot(ElementScreenshotEntity entity) {
        BinaryContent content = new BinaryContent(entity.getContent());
        return new Screenshot(content, entity.getFileName(), entity.getContentType());
    }

}
