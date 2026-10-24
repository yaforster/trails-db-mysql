package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ViewportMoveDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.browser.viewport.MoveViewportAction;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMove;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMoveDirection;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMoveUnit;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ViewportMoveDetailsMapperTest {

    private final PositionEntityMapper positionMapper = mock(PositionEntityMapper.class);
    private final ViewportMoveDetailsMapper mapper = new ViewportMoveDetailsMapper(positionMapper);

    @Test
    void toEntity_ShouldMapFields() {
        MoveViewportAction action = MoveViewportAction.builder()
                                                      .movement(ViewportMove.SCROLL_BY)
                                                      .direction(ViewportMoveDirection.DOWN)
                                                      .amount(0.5D)
                                                      .unit(ViewportMoveUnit.VIEWPORTS)
                                                      .delayAfterMoveMillis(250L)
                                                      .build();

        ViewportMoveDetailsEntity entity = mapper.toEntity(action);

        assertEquals(ViewportMove.SCROLL_BY, entity.getMovement());
        assertEquals(ViewportMoveDirection.DOWN, entity.getDirection());
        assertEquals(0.5D, entity.getAmount());
        assertEquals(ViewportMoveUnit.VIEWPORTS, entity.getUnit());
        assertEquals(250L, entity.getDelayAfterMoveMillis());
    }

    @Test
    void fromEntity_ShouldMapAction() {
        PositionEntity positionEntity = new PositionEntity();
        Position position = new Position(BigDecimal.ONE, BigDecimal.TEN);
        ActionEntity entity = new ActionEntity().setId(1L)
                                                .setLabel("move viewport")
                                                .setNextActions(List.of(2L))
                                                .setPosition(positionEntity);
        ViewportMoveDetailsEntity details = new ViewportMoveDetailsEntity(ViewportMove.PAGE_FLIP,
                ViewportMoveDirection.UP, 2D, ViewportMoveUnit.VIEWPORTS, 300L);
        when(positionMapper.fromEntity(positionEntity)).thenReturn(position);

        MoveViewportAction action = mapper.fromEntity(entity, details);

        assertEquals(1L, action.getActionID());
        assertEquals("move viewport", action.getLabel());
        assertEquals(List.of(2L), action.getNextActions());
        assertEquals(ViewportMove.PAGE_FLIP, action.getMovement());
        assertEquals(ViewportMoveDirection.UP, action.getDirection());
        assertEquals(2D, action.getAmount());
        assertEquals(ViewportMoveUnit.VIEWPORTS, action.getUnit());
        assertEquals(300L, action.getDelayAfterMoveMillis());
        assertEquals(position, action.getPosition());
    }

}
