package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ResizeViewportDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.browser.ResizeViewportAction;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportDimensions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ResizeViewportDetailsMapperTest {

    private final PositionEntityMapper positionMapper = Mockito.mock(PositionEntityMapper.class);
    private final ResizeViewportDetailsMapper mapper = new ResizeViewportDetailsMapper(positionMapper);

    @Test
    void toEntity_mapsDimensions() {
        ResizeViewportAction action = ResizeViewportAction.builder()
                                                          .viewportDimensions(new ViewportDimensions(800, 600))
                                                          .build();

        ResizeViewportDetailsEntity entity = mapper.toEntity(action);

        assertEquals(800, entity.getViewportWidth());
        assertEquals(600, entity.getViewportHeight());
    }

    @Test
    void fromEntity_mapsDimensionsAndCommonActionFields() {
        ActionEntity entity = new ActionEntity().setId(7L)
                                                .setLabel("resize")
                                                .setNextActions(List.of(8L))
                                                .setPosition(new PositionEntity());
        Position position = new Position(BigDecimal.ONE, BigDecimal.TEN);
        when(positionMapper.fromEntity(entity.getPosition())).thenReturn(position);

        ResizeViewportAction action = mapper.fromEntity(entity, new ResizeViewportDetailsEntity(800, 600));

        assertEquals(new ViewportDimensions(800, 600), action.getViewportDimensions());
        assertEquals(7L, action.getActionID());
        assertEquals("resize", action.getLabel());
        assertEquals(List.of(8L), action.getNextActions());
        assertEquals(position, action.getPosition());
    }

}
