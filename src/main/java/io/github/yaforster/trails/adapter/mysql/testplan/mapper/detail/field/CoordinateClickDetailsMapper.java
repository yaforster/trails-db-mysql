package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.CoordinateClickDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.action.browser.CoordinateClickAction;
import io.github.yaforster.trails.core.test.action.browser.ViewportCoordinates;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CoordinateClickDetailsMapper {

    private final PositionEntityMapper positionMapper;

    public CoordinateClickDetailsEntity toEntity(CoordinateClickAction action) {
        ViewportCoordinates coordinates = action.getViewportCoordinates();
        return new CoordinateClickDetailsEntity(coordinates.xCoordinate(), coordinates.yCoordinate());
    }

    public CoordinateClickAction fromEntity(ActionEntity entity, CoordinateClickDetailsEntity details) {
        ViewportCoordinates coordinates = new ViewportCoordinates(details.getXCoordinate(), details.getYCoordinate());
        Position position = positionMapper.fromEntity(entity.getPosition());
        return CoordinateClickAction.builder()
                                    .actionID(entity.getId())
                                    .label(entity.getLabel())
                                    .nextActions(entity.getNextActions())
                                    .viewportCoordinates(coordinates)
                                    .position(position)
                                    .build();
    }

}
