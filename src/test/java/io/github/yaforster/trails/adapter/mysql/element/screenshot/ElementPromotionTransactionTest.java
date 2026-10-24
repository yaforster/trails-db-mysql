package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.core.data.ElementType;
import io.github.yaforster.trails.core.test.action.element.LocatorType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:element-promotion;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
        "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=none"})
class ElementPromotionTransactionTest {

    private final ElementRepository repository;
    private final ElementDatabaseService service;
    @MockitoBean
    private ScreenshotRepository screenshots;

    @Autowired
    ElementPromotionTransactionTest(ElementRepository repository, ElementDatabaseService service) {
        this.repository = repository;
        this.service = service;
    }

    @Test
    void shouldRollBackCopiedElement_whenScreenshotStorageFails() {
        ElementEntity source = new ElementEntity().setApplicationId(1L)
                                                  .setStageId(2L)
                                                  .setLabel("button")
                                                  .setLocator("#button")
                                                  .setLocatorType(LocatorType.CSS)
                                                  .setType(ElementType.BUTTON);
        repository.save(source);
        ElementScreenshotEntity screenshot = new ElementScreenshotEntity().setElementId(source.getId())
                                                                          .setFileName("button.png")
                                                                          .setContentType("image/png")
                                                                          .setContent(new byte[]{1, 2});
        IllegalStateException failure = new IllegalStateException("Screenshot storage unavailable");
        when(screenshots.findByElementId(source.getId())).thenReturn(Optional.of(screenshot));
        when(screenshots.save(any(ElementScreenshotEntity.class))).thenThrow(failure);
        ElementDatabaseService.ElementPromotion promotion = new ElementDatabaseService.ElementPromotion(1L, 2L,
                source.getId(), 3L);

        Throwable thrown = catchThrowable(() -> service.promoteElement(promotion));

        assertThat(
                java.util.List.of(thrown, repository.count(), repository.existsById(source.getId()))).containsExactly(
                        failure, 1L, true);
    }
}
