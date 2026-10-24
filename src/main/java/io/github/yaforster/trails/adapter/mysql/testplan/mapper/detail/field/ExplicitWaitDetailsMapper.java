package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ExplicitWaitDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.action.browser.ExplicitWaitAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExplicitWaitDetailsMapper {

    private final PositionEntityMapper positionMapper;

    public ExplicitWaitDetailsEntity toEntity(ExplicitWaitAction action) {
        long delayMillis = Math.max(0L, action.getDelayMillis());
        return new ExplicitWaitDetailsEntity(delayMillis);
    }

    public ExplicitWaitAction fromEntity(ActionEntity entity, ExplicitWaitDetailsEntity details) {
        long delayMillis = Math.max(0L, details.getDelayMillis());
        Position position = positionMapper.fromEntity(entity.getPosition());
        return ExplicitWaitAction.builder()
                                 .actionID(entity.getId())
                                 .label(entity.getLabel())
                                 .nextActions(entity.getNextActions())
                                 .delayMillis(delayMillis)
                                 .position(position)
                                 .build();
    }

}
