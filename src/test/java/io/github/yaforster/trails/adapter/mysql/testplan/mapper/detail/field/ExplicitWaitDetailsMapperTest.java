package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ExplicitWaitDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.browser.ExplicitWaitAction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ExplicitWaitDetailsMapperTest {

    private final PositionEntityMapper positionMapper = mock(PositionEntityMapper.class);
    private final ExplicitWaitDetailsMapper mapper = new ExplicitWaitDetailsMapper(positionMapper);

    @Test
    void toEntity_ShouldMapDelay() {
        ExplicitWaitAction action = ExplicitWaitAction.builder()
                                                      .delayMillis(125L)
                                                      .build();

        ExplicitWaitDetailsEntity entity = mapper.toEntity(action);

        assertEquals(125L, entity.getDelayMillis());
    }

    @Test
    void fromEntity_ShouldMapAction() {
        PositionEntity positionEntity = new PositionEntity();
        Position position = new Position(BigDecimal.ONE, BigDecimal.TEN);
        ActionEntity entity = new ActionEntity().setId(1L)
                                                .setLabel("wait")
                                                .setNextActions(List.of(2L))
                                                .setPosition(positionEntity);
        ExplicitWaitDetailsEntity details = new ExplicitWaitDetailsEntity(125L);
        when(positionMapper.fromEntity(positionEntity)).thenReturn(position);

        ExplicitWaitAction action = mapper.fromEntity(entity, details);

        assertEquals(1L, action.getActionID());
        assertEquals("wait", action.getLabel());
        assertEquals(List.of(2L), action.getNextActions());
        assertEquals(125L, action.getDelayMillis());
        assertEquals(position, action.getPosition());
    }

}
