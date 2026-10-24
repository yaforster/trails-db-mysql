package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.core.EntityMissingException;
import io.github.yaforster.trails.core.MappingException;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.core.data.ElementType;
import io.github.yaforster.trails.core.definition.ElementDefinition;
import io.github.yaforster.trails.core.deletion.DatabaseDeletionResult;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import io.github.yaforster.trails.core.persisted.PersistedElement;
import io.github.yaforster.trails.core.test.action.element.Locator;
import io.github.yaforster.trails.core.test.action.element.LocatorType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ElementDatabaseServiceImplTest {

    private final ElementRepository repository = Mockito.mock(ElementRepository.class);
    private final ScreenshotRepository screenshotRepository = Mockito.mock(ScreenshotRepository.class);
    private final ElementEntityMapper elementEntityMapper = Mockito.mock(ElementEntityMapper.class);
    private final ElementPromotionService promotionService = new ElementPromotionService(repository,
            screenshotRepository);
    private final ElementDatabaseServiceImpl service = new ElementDatabaseServiceImpl(repository, promotionService,
            elementEntityMapper);

    @Test
    void storeElement_ShouldDelegateToMapperAndRepository() {
        ElementDefinition definition = new ElementDefinition(ElementType.TEXT, "label", "#id", LocatorType.CSS);
        ElementEntity entity = new ElementEntity();
        ElementEntity saved = new ElementEntity().setId(12L);
        PersistedElement persisted = Mockito.mock(PersistedElement.class);
        when(elementEntityMapper.toEntity(1L, 2L, definition)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(elementEntityMapper.toPersisted(saved)).thenReturn(persisted);

        PersistedElement result = service.storeElement(new ElementDatabaseService.ElementCreation(1L, 2L, definition));

        assertEquals(persisted, result);
    }

    @Test
    void getElements_ShouldMapPage() {
        ElementEntity entity = new ElementEntity().setId(1L);
        PersistedElement persisted = Mockito.mock(PersistedElement.class);
        when(repository.findAllByApplicationIdAndStageIdAndRetiredFalse(1L, 2L, PageRequest.of(0, 10))).thenReturn(
                new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1));
        when(elementEntityMapper.toPersisted(entity)).thenReturn(persisted);

        PagedResult<PersistedElement> result = service.getElements(
                new ElementDatabaseService.ElementPage(1L, 2L, 0, 10, false));

        assertEquals(1, result.getContent()
                              .size());
        assertEquals(persisted, result.getContent()
                                      .getFirst());
    }

    @Test
    void getElement_ShouldMapOptional_WhenFound() {
        ElementEntity entity = new ElementEntity().setId(10L);
        PersistedElement persisted = Mockito.mock(PersistedElement.class);
        when(repository.findByIdAndApplicationIdAndStageId(10L, 1L, 2L)).thenReturn(Optional.of(entity));
        when(elementEntityMapper.toPersisted(entity)).thenReturn(persisted);

        Optional<PersistedElement> result = service.getElement(
                new ElementDatabaseService.ElementDetails(1L, 2L, 10L, true));

        assertTrue(result.isPresent());
        assertEquals(persisted, result.get());
    }

    @Test
    void getElement_ShouldReturnEmpty_WhenNotFound() {
        when(repository.findByIdAndApplicationIdAndStageId(10L, 1L, 2L)).thenReturn(Optional.empty());

        Optional<PersistedElement> result = service.getElement(
                new ElementDatabaseService.ElementDetails(1L, 2L, 10L, true));

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteElement_ShouldReturnNotFound_WhenEntityMissing() {
        when(repository.findByIdAndApplicationIdAndStageId(10L, 1L, 2L)).thenReturn(Optional.empty());

        DatabaseDeletionResult result = service.deleteElement(new ElementDatabaseService.ElementReference(1L, 2L, 10L));

        assertInstanceOf(DeletionNotFound.class, result);
    }

    @Test
    void deleteElement_ShouldReturnSuccess_WhenRetireWorks() {
        ElementEntity entity = new ElementEntity().setId(10L);
        when(repository.findByIdAndApplicationIdAndStageId(10L, 1L, 2L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        DatabaseDeletionResult result = service.deleteElement(new ElementDatabaseService.ElementReference(1L, 2L, 10L));

        assertInstanceOf(DeletionSuccess.class, result);
        verify(repository).save(entity);
        assertTrue(entity.isRetired());
    }

    @Test
    void deleteElement_ShouldReturnFailure_WhenRetireThrows() {
        ElementEntity entity = new ElementEntity().setId(10L);
        when(repository.findByIdAndApplicationIdAndStageId(10L, 1L, 2L)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException("boom")).when(repository)
                                             .save(entity);

        DatabaseDeletionResult result = service.deleteElement(new ElementDatabaseService.ElementReference(1L, 2L, 10L));

        assertInstanceOf(DeletionFailure.class, result);
    }

    @Test
    void promoteElement_ShouldCopyActiveElementAndScreenshotToTargetStage() {
        ElementEntity source = new ElementEntity().setId(10L)
                                                  .setApplicationId(1L)
                                                  .setStageId(2L)
                                                  .setLabel("Login")
                                                  .setLocator("#login")
                                                  .setLocatorType(LocatorType.CSS)
                                                  .setType(ElementType.BUTTON);
        ElementEntity stored = new ElementEntity().setId(20L)
                                                  .setApplicationId(1L)
                                                  .setStageId(3L)
                                                  .setLabel("Login")
                                                  .setLocator("#login")
                                                  .setLocatorType(LocatorType.CSS)
                                                  .setType(ElementType.BUTTON);
        ElementScreenshotEntity screenshot = new ElementScreenshotEntity().setElementId(10L)
                                                                          .setFileName("login.jpg")
                                                                          .setContent(new byte[]{1, 2, 3});
        PersistedElement persisted = new PersistedElement(20L, "Login", LocatorType.CSS, 1L, 3L, "#login",
                ElementType.BUTTON);
        when(repository.findByIdAndApplicationIdAndStageIdAndRetiredFalse(10L, 1L, 2L)).thenReturn(Optional.of(source));
        when(repository.save(any(ElementEntity.class))).thenReturn(stored);
        when(screenshotRepository.findByElementId(10L)).thenReturn(Optional.of(screenshot));
        when(elementEntityMapper.toPersisted(stored)).thenReturn(persisted);

        Optional<PersistedElement> result = service.promoteElement(
                new ElementDatabaseService.ElementPromotion(1L, 2L, 10L, 3L));

        assertTrue(result.isPresent());
        assertEquals(persisted, result.get());
        verify(repository).save(Mockito.argThat(entity -> entity.getId() == null && entity.getApplicationId()
                                                                                          .equals(1L)
                && entity.getStageId()
                         .equals(3L)
                && entity.getLabel()
                         .equals("Login")
                && entity.getLocator()
                         .equals("#login")
                && entity.getLocatorType() == LocatorType.CSS && entity.getType() == ElementType.BUTTON
                && !entity.isRetired()));
        verify(screenshotRepository).save(Mockito.argThat(copy -> copy.getId() == null && copy.getElementId()
                                                                                              .equals(20L)
                && copy.getFileName()
                       .equals("login.jpg")
                && copy.getContent() != screenshot.getContent() && copy.getContent().length == 3));
    }

    @Test
    void promoteElement_ShouldReturnEmpty_WhenSourceElementIsMissingOrRetired() {
        when(repository.findByIdAndApplicationIdAndStageIdAndRetiredFalse(10L, 1L, 2L)).thenReturn(Optional.empty());

        Optional<PersistedElement> result = service.promoteElement(
                new ElementDatabaseService.ElementPromotion(1L, 2L, 10L, 3L));

        assertTrue(result.isEmpty());
    }

    @Test
    void hasElements_ShouldDelegateToRepository() {
        when(repository.existsByApplicationIdAndStageIdAndRetiredFalse(1L, 2L)).thenReturn(true);

        assertTrue(service.hasElements(new ElementDatabaseService.StageReference(1L, 2L)));
    }

    @Test
    void findStageIdsWithElements_ShouldDelegateToRepository() {
        when(repository.findStageIdsWithActiveElements(1L, List.of(2L, 3L))).thenReturn(Set.of(2L));

        Set<Long> result = service.findStageIdsWithElements(
                new ElementDatabaseService.CandidateStages(1L, List.of(2L, 3L)));

        assertEquals(Set.of(2L), result);
        verify(repository).findStageIdsWithActiveElements(1L, List.of(2L, 3L));
    }

    @Test
    void findStageIdsWithElements_ShouldNotQueryRepository_WhenInputIsEmpty() {
        Set<Long> result = service.findStageIdsWithElements(new ElementDatabaseService.CandidateStages(1L, List.of()));

        assertTrue(result.isEmpty());
        verifyNoInteractions(repository);
    }

    @Test
    void getElementIDByLocatorWithStage_ShouldReturnId_WhenFound() {
        when(repository.findByApplicationIdAndStageIdAndLocatorAndRetiredFalse(1L, 2L, "#existing")).thenReturn(
                Optional.of(new ElementEntity().setId(124L)));

        Long result = service.getElementIDByLocator(new ElementDatabaseService.ElementLocator(1L, 2L, "#existing"));

        assertEquals(124L, result);
    }

    @Test
    void getElementIDByLocatorWithStage_ShouldThrow_WhenElementMissing() {
        when(repository.findByApplicationIdAndStageIdAndLocatorAndRetiredFalse(1L, 2L, "#missing")).thenReturn(
                Optional.empty());

        assertThrows(EntityMissingException.class,
                () -> service.getElementIDByLocator(new ElementDatabaseService.ElementLocator(1L, 2L, "#missing")));
    }

    @Test
    void getElementLocator_ShouldThrow_WhenElementMissing() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MappingException.class, () -> service.getElementLocator(99L));
    }

    @Test
    void getElementLocator_ShouldReturnLocator_WhenElementExists() {
        ElementEntity entity = new ElementEntity().setLocatorType(LocatorType.XPATH)
                                                  .setLocator("//div");
        when(repository.findById(5L)).thenReturn(Optional.of(entity));

        Locator locator = service.getElementLocator(5L);

        assertEquals(LocatorType.XPATH, locator.type());
        assertEquals("//div", locator.locatorString());
    }

    @Test
    void findPromotionTargetElementId_ShouldReturnMatchingActiveTargetElement() {
        ElementEntity sourceElement = new ElementEntity().setId(30L)
                                                         .setLocator("#login")
                                                         .setLocatorType(LocatorType.CSS)
                                                         .setType(ElementType.BUTTON);
        when(repository.findById(30L)).thenReturn(Optional.of(sourceElement));
        when(repository.findByApplicationIdAndStageIdAndLocatorAndLocatorTypeAndTypeAndRetiredFalse(1L, 3L, "#login",
                LocatorType.CSS, ElementType.BUTTON)).thenReturn(Optional.of(new ElementEntity().setId(40L)));

        Optional<Long> result = service.findPromotionTargetElementId(
                new ElementDatabaseService.ElementPromotionTarget(1L, 30L, 3L));

        assertEquals(Optional.of(40L), result);
    }

}
