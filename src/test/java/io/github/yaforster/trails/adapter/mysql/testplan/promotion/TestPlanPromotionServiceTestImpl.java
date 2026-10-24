package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.TestPlanPromotionService;
import io.github.yaforster.trails.adapter.mysql.testplan.TestPlanCopy;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.EnvInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.FixedValueEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.RandomValueInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.RelativeDateInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.TestDataValueInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.TimeStampInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.*;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.*;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.core.test.action.element.ElementValueSource;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMove;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMoveDirection;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMoveUnit;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestPlanPromotionServiceTestImpl {

    private final ElementDatabaseService elementDatabaseService = Mockito.mock(ElementDatabaseService.class);
    private final ValueComputationInstructionCopyService instructionCopyService = new ValueComputationInstructionCopyService(
            List.of(new EnvInstructionCopyVariant(), new FixedValueInstructionCopyVariant(),
                    new RandomValueInstructionCopyVariant(), new RelativeDateInstructionCopyVariant(),
                    new TimeStampInstructionCopyVariant(), new TestDataValueInstructionCopyVariant()));
    private final ActionDetailsCopyService actionDetailsCopyService = new ActionDetailsCopyService(
            elementDatabaseService, instructionCopyService,
            List.of(new TypingDetailsCopyVariant(), new ClickDetailsCopyVariant(),
                    new CoordinateClickDetailsCopyVariant(), new CheckExistenceDetailsCopyVariant(),
                    new ResizeViewportDetailsCopyVariant(), new SelectionDetailsCopyVariant(),
                    new TextCheckDetailsCopyVariant(), new ElementValueCheckDetailsCopyVariant(),
                    new CookieSearchDetailsCopyVariant(), new SessionStorageSearchDetailsCopyVariant(),
                    new LocalStorageSearchDetailsCopyVariant(), new SwitchWebsiteDetailsCopyVariant(),
                    new ExplicitWaitDetailsCopyVariant(), new DownloadedFileCheckDetailsCopyVariant(),
                    new DownloadedDocumentTextCheckDetailsCopyVariant(), new ViewportMoveDetailsCopyVariant()));
    private final TestPlanPromotionService service = new TestPlanPromotionService(actionDetailsCopyService);

    @Test
    void copyToStage_ShouldCopyTestPlanMetadataAndActionsToTargetStage() {
        stubTargetElement(30L, 40L);
        ActionEntity action = action(100L, new TypingDetailsEntity(30L, true, 50L, new FixedValueEntity("abc")));
        TestPlanEntity source = sourceTestPlan(List.of(action));

        Optional<TestPlanCopy> result = service.copyToStage(source, 3L);

        assertTrue(result.isPresent());
        TestPlanEntity copy = result.get()
                                    .entity();
        assertNull(copy.getId());
        assertEquals(1L, copy.getApplicationId());
        assertEquals(3L, copy.getStageId());
        assertEquals("plan", copy.getLabel());
        assertFalse(copy.isRetired());
        assertEquals(1, copy.getTestSteps()
                            .size());
        assertNotSame(action, copy.getTestSteps()
                                  .getFirst());
        assertEquals(List.of(), copy.getTestSteps()
                                    .getFirst()
                                    .getNextActions());
    }

    @Test
    void copyToStage_ShouldRewriteElementBoundDetailsToMatchingTargetElement() {
        stubTargetElement(30L, 40L);
        ActionEntity action = action(100L, new ElementValueCheckDetailsEntity(30L, ElementValueSource.ATTRIBUTE, "href",
                new FixedValueEntity("expected")));
        TestPlanEntity source = sourceTestPlan(List.of(action));

        TestPlanEntity copy = service.copyToStage(source, 3L)
                                     .orElseThrow()
                                     .entity();

        ElementValueCheckDetailsEntity details = (ElementValueCheckDetailsEntity) copy.getTestSteps()
                                                                                      .getFirst()
                                                                                      .getDetails();
        assertEquals(40L, details.getElementID());
        assertEquals(ElementValueSource.ATTRIBUTE, details.getValueSource());
        assertEquals("href", details.getValueName());
        assertNotSame(((ElementValueCheckDetailsEntity) action.getDetails()).getValueComputation(),
                details.getValueComputation());
        assertInstanceOf(FixedValueEntity.class, details.getValueComputation());
        assertEquals("expected", ((FixedValueEntity) details.getValueComputation()).getValue());
    }

    @Test
    void copyToStage_ShouldCopyRemainingElementBoundDetails() {
        stubTargetElement(30L, 40L);
        assertCopiedElementDetails(new CheckExistenceActionDetailsEntity(30L), CheckExistenceActionDetailsEntity.class);
        assertCopiedElementDetails(new SelectionDetailsEntity(30L, new FixedValueEntity("option")),
                SelectionDetailsEntity.class);
        assertCopiedElementDetails(new TextCheckDetailsEntity(30L, new FixedValueEntity("text")),
                TextCheckDetailsEntity.class);
    }

    @Test
    void copyToStage_ShouldCopyNonElementDetailsWithoutElementLookup() {
        ActionEntity action = action(100L,
                new CookieSearchDetailsEntity("session", new EnvInstructionEntity("TOKEN", "dev")));
        TestPlanEntity source = sourceTestPlan(List.of(action));

        TestPlanEntity copy = service.copyToStage(source, 3L)
                                     .orElseThrow()
                                     .entity();

        CookieSearchDetailsEntity details = (CookieSearchDetailsEntity) copy.getTestSteps()
                                                                            .getFirst()
                                                                            .getDetails();
        assertEquals("session", details.getCookieName());
        assertInstanceOf(EnvInstructionEntity.class, details.getValueComputation());
        EnvInstructionEntity instruction = (EnvInstructionEntity) details.getValueComputation();
        assertEquals("TOKEN", instruction.getVariableName());
        assertEquals("dev", instruction.getDefaultValue());
        verifyNoInteractions(elementDatabaseService);
    }

    @Test
    void copyToStage_ShouldReturnEmpty_WhenMatchingTargetElementIsMissing() {
        when(elementDatabaseService.findPromotionTargetElementId(
                new ElementDatabaseService.ElementPromotionTarget(1L, 30L, 3L))).thenReturn(Optional.empty());
        TestPlanEntity source = sourceTestPlan(List.of(action(100L, new ClickActionDetailsEntity(30L))));

        Optional<TestPlanCopy> result = service.copyToStage(source, 3L);

        assertTrue(result.isEmpty());
    }

    @Test
    void copyToStage_ShouldDefensivelyCopyPositionAndNextActionList() {
        stubTargetElement(30L, 40L);
        PositionEntity position = new PositionEntity().setXCoordinatePixels(BigDecimal.ONE)
                                                      .setYCoordinatePixels(BigDecimal.TEN);
        ActionEntity action = action(100L, new ClickActionDetailsEntity(30L)).setPosition(position);
        TestPlanEntity source = sourceTestPlan(List.of(action));

        ActionEntity copiedAction = service.copyToStage(source, 3L)
                                           .orElseThrow()
                                           .entity()
                                           .getTestSteps()
                                           .getFirst();

        assertNotSame(position, copiedAction.getPosition());
        assertEquals(BigDecimal.ONE, copiedAction.getPosition()
                                                 .getXCoordinatePixels());
        assertEquals(BigDecimal.TEN, copiedAction.getPosition()
                                                 .getYCoordinatePixels());
        assertNotSame(action.getNextActions(), copiedAction.getNextActions());
    }

    @Test
    void copyToStage_ShouldReplaceNullNextActionsWithEmptyList() {
        stubTargetElement(30L, 40L);
        TestPlanEntity source = sourceTestPlan(
                List.of(action(100L, new ClickActionDetailsEntity(30L)).setNextActions(null)));

        ActionEntity copiedAction = service.copyToStage(source, 3L)
                                           .orElseThrow()
                                           .entity()
                                           .getTestSteps()
                                           .getFirst();

        assertEquals(List.of(), copiedAction.getNextActions());
    }

    @Test
    void copyToStage_ShouldCopySupportedValueComputationInstructions() {
        assertCopiedInstruction(
                new CookieSearchDetailsEntity("cookie", new RandomValueInstructionEntity("pre", 8, "abc", "post")),
                RandomValueInstructionEntity.class);
        assertCopiedInstruction(
                new LocalStorageSearchDetailsEntity("key", new RelativeDateInstructionEntity(3, "yyyy-MM-dd")),
                RelativeDateInstructionEntity.class);
        assertCopiedInstruction(
                new SessionStorageSearchDetailsEntity("key", new TimeStampInstructionEntity("HH:mm:ss")),
                TimeStampInstructionEntity.class);
        assertCopiedInstruction(new SwitchWebsiteDetailsEntity(new TestDataValueInstructionEntity(7L, "url")),
                TestDataValueInstructionEntity.class);
    }

    @Test
    void copyToStage_ShouldCopyExplicitWaitDetails() {
        TestPlanEntity source = sourceTestPlan(List.of(action(100L, new ExplicitWaitDetailsEntity(500L))));

        ActionDetailsEntity details = service.copyToStage(source, 3L)
                                             .orElseThrow()
                                             .entity()
                                             .getTestSteps()
                                             .getFirst()
                                             .getDetails();

        assertInstanceOf(ExplicitWaitDetailsEntity.class, details);
        assertEquals(500L, ((ExplicitWaitDetailsEntity) details).getDelayMillis());
        verifyNoInteractions(elementDatabaseService);
    }

    @Test
    void copyToStage_ShouldCopyDownloadedFileCheckDetails() {
        TestPlanEntity source = sourceTestPlan(
                List.of(action(100L, new DownloadedFileCheckDetailsEntity("report.pdf"))));

        ActionDetailsEntity details = service.copyToStage(source, 3L)
                                             .orElseThrow()
                                             .entity()
                                             .getTestSteps()
                                             .getFirst()
                                             .getDetails();

        assertInstanceOf(DownloadedFileCheckDetailsEntity.class, details);
        assertEquals("report.pdf", ((DownloadedFileCheckDetailsEntity) details).getFileName());
        verifyNoInteractions(elementDatabaseService);
    }

    @Test
    void copyToStage_ShouldCopyDownloadedDocumentTextCheckDetails() {
        TestPlanEntity source = sourceTestPlan(
                List.of(action(100L, new DownloadedDocumentTextCheckDetailsEntity("report.pdf", "approved", true))));

        ActionDetailsEntity details = service.copyToStage(source, 3L)
                                             .orElseThrow()
                                             .entity()
                                             .getTestSteps()
                                             .getFirst()
                                             .getDetails();

        assertInstanceOf(DownloadedDocumentTextCheckDetailsEntity.class, details);
        DownloadedDocumentTextCheckDetailsEntity documentDetails = (DownloadedDocumentTextCheckDetailsEntity) details;
        assertEquals("report.pdf", documentDetails.getFileName());
        assertEquals("approved", documentDetails.getExpectedText());
        assertEquals(true, documentDetails.getCaseSensitive());
        verifyNoInteractions(elementDatabaseService);
    }

    @Test
    void copyToStage_ShouldCopyViewportMoveDetails() {
        TestPlanEntity source = sourceTestPlan(
                List.of(action(100L, new ViewportMoveDetailsEntity(ViewportMove.SCROLL_BY, ViewportMoveDirection.DOWN,
                        0.5D, ViewportMoveUnit.VIEWPORTS, 250L))));

        ActionDetailsEntity details = service.copyToStage(source, 3L)
                                             .orElseThrow()
                                             .entity()
                                             .getTestSteps()
                                             .getFirst()
                                             .getDetails();

        assertInstanceOf(ViewportMoveDetailsEntity.class, details);
        ViewportMoveDetailsEntity moveDetails = (ViewportMoveDetailsEntity) details;
        assertEquals(ViewportMove.SCROLL_BY, moveDetails.getMovement());
        assertEquals(ViewportMoveDirection.DOWN, moveDetails.getDirection());
        assertEquals(0.5D, moveDetails.getAmount());
        assertEquals(ViewportMoveUnit.VIEWPORTS, moveDetails.getUnit());
        assertEquals(250L, moveDetails.getDelayAfterMoveMillis());
        verifyNoInteractions(elementDatabaseService);
    }

    @Test
    void copyToStage_ShouldCopyCoordinateClickDetailsWithoutElementLookup() {
        TestPlanEntity source = sourceTestPlan(List.of(action(100L, new CoordinateClickDetailsEntity(12, 34))));

        ActionDetailsEntity details = service.copyToStage(source, 3L)
                                             .orElseThrow()
                                             .entity()
                                             .getTestSteps()
                                             .getFirst()
                                             .getDetails();

        CoordinateClickDetailsEntity coordinateDetails = assertInstanceOf(CoordinateClickDetailsEntity.class, details);
        assertEquals(12, coordinateDetails.getXCoordinate());
        assertEquals(34, coordinateDetails.getYCoordinate());
        verifyNoInteractions(elementDatabaseService);
    }

    @Test
    void copyToStage_ShouldCopyResizeViewportDetailsWithoutElementLookup() {
        TestPlanEntity source = sourceTestPlan(List.of(action(100L, new ResizeViewportDetailsEntity(800, 600))));

        ActionDetailsEntity details = service.copyToStage(source, 3L)
                                             .orElseThrow()
                                             .entity()
                                             .getTestSteps()
                                             .getFirst()
                                             .getDetails();

        ResizeViewportDetailsEntity resizeDetails = assertInstanceOf(ResizeViewportDetailsEntity.class, details);
        assertEquals(800, resizeDetails.getViewportWidth());
        assertEquals(600, resizeDetails.getViewportHeight());
        verifyNoInteractions(elementDatabaseService);
    }

    @Test
    void copyToStage_ShouldRetainNullValueComputation() {
        TestPlanEntity source = sourceTestPlan(List.of(action(100L, new CookieSearchDetailsEntity("cookie", null))));

        CookieSearchDetailsEntity details = (CookieSearchDetailsEntity) service.copyToStage(source, 3L)
                                                                               .orElseThrow()
                                                                               .entity()
                                                                               .getTestSteps()
                                                                               .getFirst()
                                                                               .getDetails();

        assertNull(details.getValueComputation());
    }

    @Test
    void copyToStage_ShouldThrow_WhenValueComputationInstructionIsUnknown() {
        TestPlanEntity source = sourceTestPlan(
                List.of(action(100L, new CookieSearchDetailsEntity("cookie", new UnknownInstructionEntity()))));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> service.copyToStage(source, 3L));

        assertTrue(exception.getMessage()
                            .contains(UnknownInstructionEntity.class.getName()));
    }

    @Test
    void copyToStage_ShouldReturnEmpty_WhenActionDetailsAreUnknown() {
        TestPlanEntity source = sourceTestPlan(List.of(action(100L, new UnknownDetailsEntity())));

        Optional<TestPlanCopy> result = service.copyToStage(source, 3L);

        assertTrue(result.isEmpty());
    }

    @Test
    void copyToStage_ShouldReturnEmpty_WhenNextActionReferencesUnknownSourceAction() {
        TestPlanEntity source = sourceTestPlan(
                List.of(action(100L, new ExplicitWaitDetailsEntity(100L)).setNextActions(List.of(200L))));

        Optional<TestPlanCopy> result = service.copyToStage(source, 3L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldReconnectCopiedActions_whenTheirOrderChangesAfterCopying() {
        ActionEntity firstSource = action(100L, new ExplicitWaitDetailsEntity(10L)).setNextActions(List.of(200L));
        ActionEntity secondSource = action(200L, new ExplicitWaitDetailsEntity(20L));
        TestPlanEntity source = sourceTestPlan(List.of(firstSource, secondSource));
        TestPlanCopy copy = service.copyToStage(source, 3L)
                                   .orElseThrow();
        TestPlanEntity copiedPlan = copy.entity();
        ActionEntity firstCopy = copiedPlan.getTestSteps()
                                           .get(0)
                                           .setId(101L);
        ActionEntity secondCopy = copiedPlan.getTestSteps()
                                            .get(1)
                                            .setId(201L);
        copiedPlan.setId(900L)
                  .setTestSteps(List.of(secondCopy, firstCopy));

        copy.resolveNextActions();

        org.assertj.core.api.Assertions.assertThat(firstCopy.getNextActions())
                                       .containsExactly(201L);
    }

    private <T extends ValueComputationInstructionEntity> void assertCopiedInstruction(ActionDetailsEntity details,
            Class<T> expectedType) {
        ActionEntity copiedAction = service.copyToStage(sourceTestPlan(List.of(action(100L, details))), 3L)
                                           .orElseThrow()
                                           .entity()
                                           .getTestSteps()
                                           .getFirst();

        ValueComputationInstructionEntity copiedInstruction = switch (copiedAction.getDetails()) {
            case CookieSearchDetailsEntity cookie -> cookie.getValueComputation();
            case LocalStorageSearchDetailsEntity localStorage -> localStorage.getValueComputation();
            case SessionStorageSearchDetailsEntity sessionStorage -> sessionStorage.getValueComputation();
            case SwitchWebsiteDetailsEntity switchWebsite -> switchWebsite.getValueComputation();
            default -> throw new AssertionError("Unexpected details type: " + copiedAction.getDetails()
                                                                                          .getClass()
                                                                                          .getName());
        };
        assertInstanceOf(expectedType, copiedInstruction);
    }

    private <T extends ActionDetailsEntity> void assertCopiedElementDetails(ActionDetailsEntity details,
            Class<T> expectedType) {
        ActionDetailsEntity copiedDetails = service.copyToStage(sourceTestPlan(List.of(action(100L, details))), 3L)
                                                   .orElseThrow()
                                                   .entity()
                                                   .getTestSteps()
                                                   .getFirst()
                                                   .getDetails();

        assertInstanceOf(expectedType, copiedDetails);
        Long copiedElementId = switch (copiedDetails) {
            case CheckExistenceActionDetailsEntity checkExistence -> checkExistence.getElementID();
            case SelectionDetailsEntity selection -> selection.getElementID();
            case TextCheckDetailsEntity textCheck -> textCheck.getElementID();
            default -> throw new AssertionError("Unexpected details type: " + copiedDetails.getClass()
                                                                                           .getName());
        };
        assertEquals(40L, copiedElementId);
    }

    private ActionEntity action(Long id, ActionDetailsEntity details) {
        return new ActionEntity().setId(id)
                                 .setActionId(id + 1)
                                 .setLabel("action-" + id)
                                 .setDetails(details)
                                 .setPosition(new PositionEntity().setXCoordinatePixels(BigDecimal.ONE)
                                                                  .setYCoordinatePixels(BigDecimal.TEN))
                                 .setNextActions(List.of());
    }

    private TestPlanEntity sourceTestPlan(List<ActionEntity> actions) {
        return new TestPlanEntity().setId(9L)
                                   .setApplicationId(1L)
                                   .setStageId(2L)
                                   .setLabel("plan")
                                   .setRetired(true)
                                   .setTestSteps(actions);
    }

    private void stubTargetElement(Long sourceElementId, Long targetElementId) {
        when(elementDatabaseService.findPromotionTargetElementId(
                new ElementDatabaseService.ElementPromotionTarget(1L, sourceElementId, 3L))).thenReturn(
                        Optional.of(targetElementId));
    }

    private static class UnknownDetailsEntity extends ActionDetailsEntity {

    }

    private static class UnknownInstructionEntity extends ValueComputationInstructionEntity {

    }

}
