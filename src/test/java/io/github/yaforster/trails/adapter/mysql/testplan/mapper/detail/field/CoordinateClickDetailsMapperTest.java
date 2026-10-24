package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.CoordinateClickDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.browser.CoordinateClickAction;
import io.github.yaforster.trails.core.test.action.browser.ViewportCoordinates;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CoordinateClickDetailsMapperTest {

    private final PositionEntityMapper positionMapper = Mockito.mock(PositionEntityMapper.class);
    private final CoordinateClickDetailsMapper mapper = new CoordinateClickDetailsMapper(positionMapper);

    @Test
    void toEntity_mapsCoordinates() {
        CoordinateClickAction action = CoordinateClickAction.builder()
                                                            .viewportCoordinates(new ViewportCoordinates(12, 34))
                                                            .build();

        CoordinateClickDetailsEntity entity = mapper.toEntity(action);

        assertEquals(12, entity.getXCoordinate());
        assertEquals(34, entity.getYCoordinate());
    }

    @Test
    void fromEntity_mapsCoordinatesAndCommonActionFields() {
        ActionEntity entity = new ActionEntity().setId(7L)
                                                .setLabel("coordinate")
                                                .setNextActions(List.of(8L))
                                                .setPosition(new PositionEntity());
        Position position = new Position(BigDecimal.ONE, BigDecimal.TEN);
        when(positionMapper.fromEntity(entity.getPosition())).thenReturn(position);

        CoordinateClickAction action = mapper.fromEntity(entity, new CoordinateClickDetailsEntity(12, 34));

        assertEquals(new ViewportCoordinates(12, 34), action.getViewportCoordinates());
        assertEquals(7L, action.getActionID());
        assertEquals("coordinate", action.getLabel());
        assertEquals(List.of(8L), action.getNextActions());
        assertEquals(position, action.getPosition());
    }

}
