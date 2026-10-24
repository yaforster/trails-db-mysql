package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.app.services.ElementDatabaseService.ElementPromotion;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/** Copies an element and its screenshot within the caller's promotion transaction. */
@Component
@AllArgsConstructor
public class ElementPromotionService {

    private final ElementRepository elementRepository;
    private final ScreenshotRepository screenshotRepository;

    public Optional<ElementEntity> promote(ElementPromotion promotion) {
        Optional<ElementEntity> source = elementRepository.findByIdAndApplicationIdAndStageIdAndRetiredFalse(
                promotion.elementId(), promotion.applicationId(), promotion.sourceStageId());
        return source.map(element -> copyToStage(element, promotion.targetStageId()));
    }

    private ElementEntity copyToStage(ElementEntity source, Long targetStageId) {
        ElementEntity copy = copyElement(source, targetStageId);
        ElementEntity storedElement = elementRepository.save(copy);
        copyScreenshot(source.getId(), storedElement.getId());
        return storedElement;
    }

    private ElementEntity copyElement(ElementEntity source, Long targetStageId) {
        return new ElementEntity().setApplicationId(source.getApplicationId())
                                  .setStageId(targetStageId)
                                  .setLabel(source.getLabel())
                                  .setLocator(source.getLocator())
                                  .setLocatorType(source.getLocatorType())
                                  .setType(source.getType())
                                  .setRetired(false);
    }

    private void copyScreenshot(Long sourceElementId, Long targetElementId) {
        screenshotRepository.findByElementId(sourceElementId)
                            .map(source -> copyScreenshotContent(source, targetElementId))
                            .ifPresent(screenshotRepository::save);
    }

    private ElementScreenshotEntity copyScreenshotContent(ElementScreenshotEntity source, Long targetElementId) {
        byte[] copiedContent = source.getContent()
                                     .clone();
        return new ElementScreenshotEntity().setElementId(targetElementId)
                                            .setFileName(source.getFileName())
                                            .setContentType(source.getContentType())
                                            .setContent(copiedContent);
    }
}
