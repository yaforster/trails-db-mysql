package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail;

import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.CheckCookieDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.CheckExistenceDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.CheckLocalStorageDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.CheckSessionStorageDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.ClickDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.CoordinateClickDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.ElementValueCheckDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.ExplicitWaitDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.SelectDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.SwitchWebsiteDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.TextCheckDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.TypingDetailsMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant.ActionEntityVariantMapper;
import io.github.yaforster.trails.core.MappingException;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.*;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckCookieAction;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckLocalStorageAction;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckSessionStorageAction;
import io.github.yaforster.trails.core.test.action.browser.viewport.MoveViewportAction;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMove;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMoveDirection;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMoveUnit;
import io.github.yaforster.trails.core.test.action.document.CheckDownloadedDocumentTextAction;
import io.github.yaforster.trails.core.test.action.browser.*;
import io.github.yaforster.trails.core.test.action.download.*;
import io.github.yaforster.trails.core.test.action.element.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ActionEntityMapperTest extends TrailsTest {

    private final PositionEntityMapper positionEntityMapper = Mockito.mock(PositionEntityMapper.class);
    private final TypingDetailsMapper typingDetailsMapper = Mockito.mock(TypingDetailsMapper.class);
    private final ClickDetailsMapper clickDetailsMapper = Mockito.mock(ClickDetailsMapper.class);
    private final CoordinateClickDetailsMapper coordinateClickDetailsMapper = Mockito.mock(
            CoordinateClickDetailsMapper.class);
    private final CheckExistenceDetailsMapper checkExistenceDetailsMapper = Mockito.mock(
            CheckExistenceDetailsMapper.class);
    private final SelectDetailsMapper selectDetailsMapper = Mockito.mock(SelectDetailsMapper.class);
    private final TextCheckDetailsMapper textCheckDetailsMapper = Mockito.mock(TextCheckDetailsMapper.class);
    private final CheckCookieDetailsMapper checkCookieDetailsMapper = Mockito.mock(CheckCookieDetailsMapper.class);
    private final CheckSessionStorageDetailsMapper checkSessionStorageDetailsMapper = Mockito.mock(
            CheckSessionStorageDetailsMapper.class);
    private final CheckLocalStorageDetailsMapper checkLocalStorageDetailsMapper = Mockito.mock(
            CheckLocalStorageDetailsMapper.class);
    private final SwitchWebsiteDetailsMapper switchWebsiteDetailsMapper = Mockito.mock(
            SwitchWebsiteDetailsMapper.class);
    private final ExplicitWaitDetailsMapper explicitWaitDetailsMapper = Mockito.mock(ExplicitWaitDetailsMapper.class);
    private final ElementValueCheckDetailsMapper elementValueCheckDetailsMapper = Mockito.mock(
            ElementValueCheckDetailsMapper.class);
    private final ActionEntityVariantMapper typingVariantMapper = variantMapper(TypingAction.class,
            TypingDetailsEntity.class);
    private final ActionEntityVariantMapper clickVariantMapper = variantMapper(ClickAction.class,
            ClickActionDetailsEntity.class);
    private final ActionEntityVariantMapper coordinateClickVariantMapper = variantMapper(CoordinateClickAction.class,
            CoordinateClickDetailsEntity.class);
    private final ActionEntityVariantMapper resizeViewportVariantMapper = variantMapper(ResizeViewportAction.class,
            ResizeViewportDetailsEntity.class);
    private final ActionEntityVariantMapper checkExistenceVariantMapper = variantMapper(CheckExistenceAction.class,
            CheckExistenceActionDetailsEntity.class);
    private final ActionEntityVariantMapper selectionVariantMapper = variantMapper(SelectAction.class,
            SelectionDetailsEntity.class);
    private final ActionEntityVariantMapper textCheckVariantMapper = variantMapper(TextCheckAction.class,
            TextCheckDetailsEntity.class);
    private final ActionEntityVariantMapper cookieSearchVariantMapper = variantMapper(CheckCookieAction.class,
            CookieSearchDetailsEntity.class);
    private final ActionEntityVariantMapper sessionStorageVariantMapper = variantMapper(CheckSessionStorageAction.class,
            SessionStorageSearchDetailsEntity.class);
    private final ActionEntityVariantMapper localStorageVariantMapper = variantMapper(CheckLocalStorageAction.class,
            LocalStorageSearchDetailsEntity.class);
    private final ActionEntityVariantMapper switchWebsiteVariantMapper = variantMapper(SwitchWebsiteAction.class,
            SwitchWebsiteDetailsEntity.class);
    private final ActionEntityVariantMapper explicitWaitVariantMapper = variantMapper(ExplicitWaitAction.class,
            ExplicitWaitDetailsEntity.class);
    private final ActionEntityVariantMapper elementValueCheckVariantMapper = variantMapper(
            ElementValueCheckAction.class, ElementValueCheckDetailsEntity.class);
    private final ActionEntityVariantMapper downloadedFileCheckVariantMapper = variantMapper(
            CheckDownloadedFileAction.class, DownloadedFileCheckDetailsEntity.class);
    private final ActionEntityVariantMapper downloadedDocumentTextCheckVariantMapper = variantMapper(
            CheckDownloadedDocumentTextAction.class, DownloadedDocumentTextCheckDetailsEntity.class);
    private final ActionEntityVariantMapper moveViewportVariantMapper = variantMapper(MoveViewportAction.class,
            ViewportMoveDetailsEntity.class);
    private final ActionEntityMapper mapper = new ActionEntityMapper(positionEntityMapper,
            List.of(typingVariantMapper, clickVariantMapper, coordinateClickVariantMapper, checkExistenceVariantMapper,
                    resizeViewportVariantMapper, selectionVariantMapper, textCheckVariantMapper,
                    cookieSearchVariantMapper, sessionStorageVariantMapper, localStorageVariantMapper,
                    switchWebsiteVariantMapper, explicitWaitVariantMapper, elementValueCheckVariantMapper,
                    downloadedFileCheckVariantMapper, downloadedDocumentTextCheckVariantMapper,
                    moveViewportVariantMapper));

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static ActionEntityVariantMapper variantMapper(Class<? extends Action> actionType,
            Class<? extends ActionDetailsEntity> detailsType) {
        ActionEntityVariantMapper mapper = Mockito.mock(ActionEntityVariantMapper.class);
        when(mapper.actionType()).thenReturn((Class) actionType);
        when(mapper.detailsType()).thenReturn((Class) detailsType);
        return mapper;
    }

    @Test
    void toEntity_MapsCommonFieldsAndTypingDetails() {
        TypingAction action = Mockito.mock(TypingAction.class);
        Position position = getInstancioOf(Position.class).create();
        PositionEntity positionEntity = getInstancioOf(PositionEntity.class).create();
        TypingDetailsEntity details = getInstancioOf(TypingDetailsEntity.class).create();
        when(action.getActionID()).thenReturn(1L);
        when(action.getLabel()).thenReturn("typing");
        when(action.getNextActions()).thenReturn(List.of(2L, 3L));
        when(action.getPosition()).thenReturn(position);
        when(positionEntityMapper.toEntity(position)).thenReturn(positionEntity);
        when(typingVariantMapper.supports(action)).thenReturn(true);
        when(typingVariantMapper.toEntity(action, 1L, 2L)).thenReturn(details);

        ActionEntity entity = mapper.toEntity(action, 1L, 2L);

        assertEquals(action.getActionID(), entity.getActionId());
        assertEquals(action.getLabel(), entity.getLabel());
        assertEquals(action.getNextActions(), entity.getNextActions());
        assertEquals(positionEntity, entity.getPosition());
        assertEquals(details, entity.getDetails());
    }

    @Test
    void toEntity_UsesScopedVariantMapper_WhenApplicationAndStageAreProvided() {
        TypingAction action = Mockito.mock(TypingAction.class);
        Position position = getInstancioOf(Position.class).create();
        PositionEntity positionEntity = getInstancioOf(PositionEntity.class).create();
        TypingDetailsEntity details = getInstancioOf(TypingDetailsEntity.class).create();
        when(action.getPosition()).thenReturn(position);
        when(positionEntityMapper.toEntity(position)).thenReturn(positionEntity);
        when(typingVariantMapper.toEntity(action, 1L, 2L)).thenReturn(details);

        ActionEntity entity = mapper.toEntity(action, 1L, 2L);

        assertEquals(details, entity.getDetails());
    }

    @Test
    void toEntity_UsesCorrectDetailsMapperForAllActionTypes() {
        ClickAction clickAction = Mockito.mock(ClickAction.class);
        CoordinateClickAction coordinateClickAction = Mockito.mock(CoordinateClickAction.class);
        ResizeViewportAction resizeViewportAction = Mockito.mock(ResizeViewportAction.class);
        CheckExistenceAction checkExistenceAction = Mockito.mock(CheckExistenceAction.class);
        SelectAction selectAction = Mockito.mock(SelectAction.class);
        TextCheckAction textCheckAction = Mockito.mock(TextCheckAction.class);
        CheckCookieAction checkCookieAction = Mockito.mock(CheckCookieAction.class);
        CheckSessionStorageAction checkSessionStorageAction = Mockito.mock(CheckSessionStorageAction.class);
        CheckLocalStorageAction checkLocalStorageAction = Mockito.mock(CheckLocalStorageAction.class);
        SwitchWebsiteAction switchWebsiteAction = Mockito.mock(SwitchWebsiteAction.class);
        ExplicitWaitAction explicitWaitAction = Mockito.mock(ExplicitWaitAction.class);
        ElementValueCheckAction elementValueCheckAction = Mockito.mock(ElementValueCheckAction.class);
        CheckDownloadedFileAction downloadedFileCheckAction = Mockito.mock(CheckDownloadedFileAction.class);
        CheckDownloadedDocumentTextAction downloadedDocumentTextCheckAction = Mockito.mock(
                CheckDownloadedDocumentTextAction.class);
        MoveViewportAction moveViewportAction = Mockito.mock(MoveViewportAction.class);

        ClickActionDetailsEntity clickDetails = new ClickActionDetailsEntity(1L);
        CoordinateClickDetailsEntity coordinateClickDetails = new CoordinateClickDetailsEntity(12, 34);
        ResizeViewportDetailsEntity resizeViewportDetails = new ResizeViewportDetailsEntity(800, 600);
        CheckExistenceActionDetailsEntity existenceDetails = new CheckExistenceActionDetailsEntity(2L);
        SelectionDetailsEntity selectionDetails = new SelectionDetailsEntity(3L, null);
        TextCheckDetailsEntity textDetails = new TextCheckDetailsEntity(4L, null);
        CookieSearchDetailsEntity cookieDetails = new CookieSearchDetailsEntity("cookie", null);
        SessionStorageSearchDetailsEntity sessionDetails = new SessionStorageSearchDetailsEntity("ss", null);
        LocalStorageSearchDetailsEntity localDetails = new LocalStorageSearchDetailsEntity("ls", null);
        SwitchWebsiteDetailsEntity switchDetails = new SwitchWebsiteDetailsEntity(null);
        ExplicitWaitDetailsEntity explicitWaitDetails = new ExplicitWaitDetailsEntity(100L);
        ElementValueCheckDetailsEntity elementValueCheckDetails = new ElementValueCheckDetailsEntity(5L,
                ElementValueSource.ATTRIBUTE, "data-state", null);
        DownloadedFileCheckDetailsEntity downloadedFileCheckDetails = new DownloadedFileCheckDetailsEntity(
                "report.pdf");
        DownloadedDocumentTextCheckDetailsEntity downloadedDocumentTextCheckDetails = new DownloadedDocumentTextCheckDetailsEntity(
                "report.pdf", "approved", true);
        ViewportMoveDetailsEntity moveViewportDetails = new ViewportMoveDetailsEntity(ViewportMove.PAGE_FLIP,
                ViewportMoveDirection.DOWN, 2D, ViewportMoveUnit.VIEWPORTS, 250L);

        when(clickVariantMapper.supports(clickAction)).thenReturn(true);
        when(clickVariantMapper.toEntity(clickAction, 1L, 2L)).thenReturn(clickDetails);
        when(coordinateClickVariantMapper.supports(coordinateClickAction)).thenReturn(true);
        when(coordinateClickVariantMapper.toEntity(coordinateClickAction, 1L, 2L)).thenReturn(coordinateClickDetails);
        when(resizeViewportVariantMapper.supports(resizeViewportAction)).thenReturn(true);
        when(resizeViewportVariantMapper.toEntity(resizeViewportAction, 1L, 2L)).thenReturn(resizeViewportDetails);
        when(checkExistenceVariantMapper.supports(checkExistenceAction)).thenReturn(true);
        when(checkExistenceVariantMapper.toEntity(checkExistenceAction, 1L, 2L)).thenReturn(existenceDetails);
        when(selectionVariantMapper.supports(selectAction)).thenReturn(true);
        when(selectionVariantMapper.toEntity(selectAction, 1L, 2L)).thenReturn(selectionDetails);
        when(textCheckVariantMapper.supports(textCheckAction)).thenReturn(true);
        when(textCheckVariantMapper.toEntity(textCheckAction, 1L, 2L)).thenReturn(textDetails);
        when(cookieSearchVariantMapper.supports(checkCookieAction)).thenReturn(true);
        when(cookieSearchVariantMapper.toEntity(checkCookieAction, 1L, 2L)).thenReturn(cookieDetails);
        when(sessionStorageVariantMapper.supports(checkSessionStorageAction)).thenReturn(true);
        when(sessionStorageVariantMapper.toEntity(checkSessionStorageAction, 1L, 2L)).thenReturn(sessionDetails);
        when(localStorageVariantMapper.supports(checkLocalStorageAction)).thenReturn(true);
        when(localStorageVariantMapper.toEntity(checkLocalStorageAction, 1L, 2L)).thenReturn(localDetails);
        when(switchWebsiteVariantMapper.supports(switchWebsiteAction)).thenReturn(true);
        when(switchWebsiteVariantMapper.toEntity(switchWebsiteAction, 1L, 2L)).thenReturn(switchDetails);
        when(explicitWaitVariantMapper.supports(explicitWaitAction)).thenReturn(true);
        when(explicitWaitVariantMapper.toEntity(explicitWaitAction, 1L, 2L)).thenReturn(explicitWaitDetails);
        when(elementValueCheckVariantMapper.supports(elementValueCheckAction)).thenReturn(true);
        when(elementValueCheckVariantMapper.toEntity(elementValueCheckAction, 1L, 2L)).thenReturn(
                elementValueCheckDetails);
        when(downloadedFileCheckVariantMapper.supports(downloadedFileCheckAction)).thenReturn(true);
        when(downloadedFileCheckVariantMapper.toEntity(downloadedFileCheckAction, 1L, 2L)).thenReturn(
                downloadedFileCheckDetails);
        when(downloadedDocumentTextCheckVariantMapper.supports(downloadedDocumentTextCheckAction)).thenReturn(true);
        when(downloadedDocumentTextCheckVariantMapper.toEntity(downloadedDocumentTextCheckAction, 1L, 2L)).thenReturn(
                downloadedDocumentTextCheckDetails);
        when(moveViewportVariantMapper.supports(moveViewportAction)).thenReturn(true);
        when(moveViewportVariantMapper.toEntity(moveViewportAction, 1L, 2L)).thenReturn(moveViewportDetails);

        assertEquals(clickDetails, mapper.toEntity(clickAction, 1L, 2L)
                                         .getDetails());
        assertEquals(coordinateClickDetails, mapper.toEntity(coordinateClickAction, 1L, 2L)
                                                   .getDetails());
        assertEquals(resizeViewportDetails, mapper.toEntity(resizeViewportAction, 1L, 2L)
                                                  .getDetails());
        assertEquals(existenceDetails, mapper.toEntity(checkExistenceAction, 1L, 2L)
                                             .getDetails());
        assertEquals(selectionDetails, mapper.toEntity(selectAction, 1L, 2L)
                                             .getDetails());
        assertEquals(textDetails, mapper.toEntity(textCheckAction, 1L, 2L)
                                        .getDetails());
        assertEquals(cookieDetails, mapper.toEntity(checkCookieAction, 1L, 2L)
                                          .getDetails());
        assertEquals(sessionDetails, mapper.toEntity(checkSessionStorageAction, 1L, 2L)
                                           .getDetails());
        assertEquals(localDetails, mapper.toEntity(checkLocalStorageAction, 1L, 2L)
                                         .getDetails());
        assertEquals(switchDetails, mapper.toEntity(switchWebsiteAction, 1L, 2L)
                                          .getDetails());
        assertEquals(explicitWaitDetails, mapper.toEntity(explicitWaitAction, 1L, 2L)
                                                .getDetails());
        assertEquals(elementValueCheckDetails, mapper.toEntity(elementValueCheckAction, 1L, 2L)
                                                     .getDetails());
        assertEquals(downloadedFileCheckDetails, mapper.toEntity(downloadedFileCheckAction, 1L, 2L)
                                                       .getDetails());
        assertEquals(downloadedDocumentTextCheckDetails, mapper.toEntity(downloadedDocumentTextCheckAction, 1L, 2L)
                                                               .getDetails());
        assertEquals(moveViewportDetails, mapper.toEntity(moveViewportAction, 1L, 2L)
                                                .getDetails());
    }

    @Test
    void toEntity_ThrowsForUnhandledActionType() {
        Action unknownAction = Mockito.mock(Action.class);
        when(unknownAction.getPosition()).thenReturn(getInstancioOf(Position.class).create());
        when(positionEntityMapper.toEntity(unknownAction.getPosition())).thenReturn(
                getInstancioOf(PositionEntity.class).create());

        assertThrows(MappingException.class, () -> mapper.toEntity(unknownAction, 1L, 2L));
    }

    @Test
    void fromEntity_UsesCorrectDetailsMapperForAllDetailsTypes() {
        ActionEntity entity = getInstancioOf(ActionEntity.class).create();

        TypingDetailsEntity typingDetails = new TypingDetailsEntity(1L, false, 0L, null);
        ClickActionDetailsEntity clickDetails = new ClickActionDetailsEntity(2L);
        CoordinateClickDetailsEntity coordinateClickDetails = new CoordinateClickDetailsEntity(12, 34);
        ResizeViewportDetailsEntity resizeViewportDetails = new ResizeViewportDetailsEntity(800, 600);
        CheckExistenceActionDetailsEntity existenceDetails = new CheckExistenceActionDetailsEntity(3L);
        SelectionDetailsEntity selectionDetails = new SelectionDetailsEntity(4L, null);
        TextCheckDetailsEntity textDetails = new TextCheckDetailsEntity(5L, null);
        CookieSearchDetailsEntity cookieDetails = new CookieSearchDetailsEntity("cookie", null);
        SessionStorageSearchDetailsEntity sessionDetails = new SessionStorageSearchDetailsEntity("ss", null);
        LocalStorageSearchDetailsEntity localDetails = new LocalStorageSearchDetailsEntity("ls", null);
        SwitchWebsiteDetailsEntity switchDetails = new SwitchWebsiteDetailsEntity(null);
        ExplicitWaitDetailsEntity explicitWaitDetails = new ExplicitWaitDetailsEntity(100L);
        ElementValueCheckDetailsEntity elementValueCheckDetails = new ElementValueCheckDetailsEntity(6L,
                ElementValueSource.CSS_VALUE, "display", null);
        DownloadedFileCheckDetailsEntity downloadedFileCheckDetails = new DownloadedFileCheckDetailsEntity(
                "report.pdf");
        ViewportMoveDetailsEntity moveViewportDetails = new ViewportMoveDetailsEntity(ViewportMove.SCROLL_BY,
                ViewportMoveDirection.UP, 0.5D, ViewportMoveUnit.VIEWPORTS, 100L);

        TypingAction typingAction = Mockito.mock(TypingAction.class);
        ClickAction clickAction = Mockito.mock(ClickAction.class);
        CoordinateClickAction coordinateClickAction = Mockito.mock(CoordinateClickAction.class);
        ResizeViewportAction resizeViewportAction = Mockito.mock(ResizeViewportAction.class);
        CheckExistenceAction existenceAction = Mockito.mock(CheckExistenceAction.class);
        SelectAction selectAction = Mockito.mock(SelectAction.class);
        TextCheckAction textAction = Mockito.mock(TextCheckAction.class);
        CheckCookieAction cookieAction = Mockito.mock(CheckCookieAction.class);
        CheckSessionStorageAction sessionAction = Mockito.mock(CheckSessionStorageAction.class);
        CheckLocalStorageAction localAction = Mockito.mock(CheckLocalStorageAction.class);
        SwitchWebsiteAction switchAction = Mockito.mock(SwitchWebsiteAction.class);
        ExplicitWaitAction explicitWaitAction = Mockito.mock(ExplicitWaitAction.class);
        ElementValueCheckAction elementValueCheckAction = Mockito.mock(ElementValueCheckAction.class);
        CheckDownloadedFileAction downloadedFileCheckAction = Mockito.mock(CheckDownloadedFileAction.class);
        MoveViewportAction moveViewportAction = Mockito.mock(MoveViewportAction.class);

        when(typingVariantMapper.supports(typingDetails)).thenReturn(true);
        when(typingVariantMapper.fromEntity(any(ActionEntity.class), eq(typingDetails), any())).thenReturn(
                typingAction);
        when(clickVariantMapper.supports(clickDetails)).thenReturn(true);
        when(clickVariantMapper.fromEntity(any(ActionEntity.class), eq(clickDetails), any())).thenReturn(clickAction);
        when(coordinateClickVariantMapper.supports(coordinateClickDetails)).thenReturn(true);
        when(coordinateClickVariantMapper.fromEntity(any(ActionEntity.class), eq(coordinateClickDetails),
                any())).thenReturn(coordinateClickAction);
        when(resizeViewportVariantMapper.supports(resizeViewportDetails)).thenReturn(true);
        when(resizeViewportVariantMapper.fromEntity(any(ActionEntity.class), eq(resizeViewportDetails),
                any())).thenReturn(resizeViewportAction);
        when(checkExistenceVariantMapper.supports(existenceDetails)).thenReturn(true);
        when(checkExistenceVariantMapper.fromEntity(any(ActionEntity.class), eq(existenceDetails), any())).thenReturn(
                existenceAction);
        when(selectionVariantMapper.supports(selectionDetails)).thenReturn(true);
        when(selectionVariantMapper.fromEntity(any(ActionEntity.class), eq(selectionDetails), any())).thenReturn(
                selectAction);
        when(textCheckVariantMapper.supports(textDetails)).thenReturn(true);
        when(textCheckVariantMapper.fromEntity(any(ActionEntity.class), eq(textDetails), any())).thenReturn(textAction);
        when(cookieSearchVariantMapper.supports(cookieDetails)).thenReturn(true);
        when(cookieSearchVariantMapper.fromEntity(any(ActionEntity.class), eq(cookieDetails), any())).thenReturn(
                cookieAction);
        when(sessionStorageVariantMapper.supports(sessionDetails)).thenReturn(true);
        when(sessionStorageVariantMapper.fromEntity(any(ActionEntity.class), eq(sessionDetails), any())).thenReturn(
                sessionAction);
        when(localStorageVariantMapper.supports(localDetails)).thenReturn(true);
        when(localStorageVariantMapper.fromEntity(any(ActionEntity.class), eq(localDetails), any())).thenReturn(
                localAction);
        when(switchWebsiteVariantMapper.supports(switchDetails)).thenReturn(true);
        when(switchWebsiteVariantMapper.fromEntity(any(ActionEntity.class), eq(switchDetails), any())).thenReturn(
                switchAction);
        when(explicitWaitVariantMapper.supports(explicitWaitDetails)).thenReturn(true);
        when(explicitWaitVariantMapper.fromEntity(any(ActionEntity.class), eq(explicitWaitDetails), any())).thenReturn(
                explicitWaitAction);
        when(elementValueCheckVariantMapper.supports(elementValueCheckDetails)).thenReturn(true);
        when(elementValueCheckVariantMapper.fromEntity(any(ActionEntity.class), eq(elementValueCheckDetails),
                any())).thenReturn(elementValueCheckAction);
        when(downloadedFileCheckVariantMapper.supports(downloadedFileCheckDetails)).thenReturn(true);
        when(downloadedFileCheckVariantMapper.fromEntity(any(ActionEntity.class), eq(downloadedFileCheckDetails),
                any())).thenReturn(downloadedFileCheckAction);
        when(moveViewportVariantMapper.supports(moveViewportDetails)).thenReturn(true);
        when(moveViewportVariantMapper.fromEntity(any(ActionEntity.class), eq(moveViewportDetails), any())).thenReturn(
                moveViewportAction);

        entity.setDetails(typingDetails);
        assertEquals(typingAction, mapper.fromEntity(entity));
        entity.setDetails(clickDetails);
        assertEquals(clickAction, mapper.fromEntity(entity));
        entity.setDetails(coordinateClickDetails);
        assertEquals(coordinateClickAction, mapper.fromEntity(entity));
        entity.setDetails(resizeViewportDetails);
        assertEquals(resizeViewportAction, mapper.fromEntity(entity));
        entity.setDetails(existenceDetails);
        assertEquals(existenceAction, mapper.fromEntity(entity));
        entity.setDetails(selectionDetails);
        assertEquals(selectAction, mapper.fromEntity(entity));
        entity.setDetails(textDetails);
        assertEquals(textAction, mapper.fromEntity(entity));
        entity.setDetails(cookieDetails);
        assertEquals(cookieAction, mapper.fromEntity(entity));
        entity.setDetails(sessionDetails);
        assertEquals(sessionAction, mapper.fromEntity(entity));
        entity.setDetails(localDetails);
        assertEquals(localAction, mapper.fromEntity(entity));
        entity.setDetails(switchDetails);
        assertEquals(switchAction, mapper.fromEntity(entity));
        entity.setDetails(explicitWaitDetails);
        assertEquals(explicitWaitAction, mapper.fromEntity(entity));
        entity.setDetails(elementValueCheckDetails);
        assertEquals(elementValueCheckAction, mapper.fromEntity(entity));
        entity.setDetails(downloadedFileCheckDetails);
        assertEquals(downloadedFileCheckAction, mapper.fromEntity(entity));
        entity.setDetails(moveViewportDetails);
        assertEquals(moveViewportAction, mapper.fromEntity(entity));
    }

    @Test
    void fromEntity_ThrowsForUnhandledDetailsType() {
        ActionEntity entity = getInstancioOf(ActionEntity.class).create();
        entity.setDetails(new UnsupportedDetailsEntity());

        assertThrows(MappingException.class, () -> mapper.fromEntity(entity));
    }

    private static class UnsupportedDetailsEntity extends ActionDetailsEntity {

    }

}
