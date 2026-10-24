package io.github.yaforster.trails.adapter.mysql.testplan.mapper;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.core.test.Position;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PositionEntityMapper {

    public Position fromEntity(PositionEntity positionEntity) {
        return new Position(positionEntity.getXCoordinatePixels(), positionEntity.getYCoordinatePixels());
    }

    public PositionEntity toEntity(Position position) {
        return new PositionEntity(null, position.xCoordinatePixels(), position.yCoordinatePixels());
    }

}
