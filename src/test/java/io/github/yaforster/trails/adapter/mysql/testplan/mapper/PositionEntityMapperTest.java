package io.github.yaforster.trails.adapter.mysql.testplan.mapper;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.test.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionEntityMapperTest extends TrailsTest {

    private final PositionEntityMapper mapper = new PositionEntityMapper();

    @Test
    void fromEntity_ShouldMapPositionEntityToPosition() {
        PositionEntity entity = getInstancioOf(PositionEntity.class).create();

        Position result = mapper.fromEntity(entity);

        assertNotNull(result);
        assertEquals(entity.getXCoordinatePixels(), result.xCoordinatePixels());
        assertEquals(entity.getYCoordinatePixels(), result.yCoordinatePixels());
    }

    @Test
    void toEntity_ShouldMapPositionToPositionEntityAndResetId() {
        Position position = getInstancioOf(Position.class).create();

        PositionEntity result = mapper.toEntity(position);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(position.xCoordinatePixels(), result.getXCoordinatePixels());
        assertEquals(position.yCoordinatePixels(), result.getYCoordinatePixels());
    }

}
