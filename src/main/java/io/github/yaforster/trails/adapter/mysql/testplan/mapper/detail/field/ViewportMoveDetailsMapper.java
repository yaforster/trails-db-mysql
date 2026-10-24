package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ViewportMoveDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.action.browser.viewport.MoveViewportAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewportMoveDetailsMapper {

    private final PositionEntityMapper positionMapper;

    public ViewportMoveDetailsEntity toEntity(MoveViewportAction action) {
        double amount = Math.max(0D, action.getAmount());
        long delayMillis = Math.max(0L, action.getDelayAfterMoveMillis());
        return new ViewportMoveDetailsEntity(action.getMovement(), action.getDirection(), amount, action.getUnit(),
                delayMillis);
    }

    public MoveViewportAction fromEntity(ActionEntity entity, ViewportMoveDetailsEntity details) {
        double amount = Math.max(0D, details.getAmount());
        long delayMillis = Math.max(0L, details.getDelayAfterMoveMillis());
        Position position = positionMapper.fromEntity(entity.getPosition());
        return MoveViewportAction.builder()
                                 .actionID(entity.getId())
                                 .label(entity.getLabel())
                                 .nextActions(entity.getNextActions())
                                 .movement(details.getMovement())
                                 .direction(details.getDirection())
                                 .amount(amount)
                                 .unit(details.getUnit())
                                 .delayAfterMoveMillis(delayMillis)
                                 .position(position)
                                 .build();
    }

}
