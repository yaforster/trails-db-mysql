package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.CookieSearchDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.ValueComputationInstructionEntityMapper;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckCookieAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class CheckCookieDetailsMapper {

    private final ValueComputationInstructionEntityMapper instructionMapper;
    private final PositionEntityMapper positionMapper;

    public CookieSearchDetailsEntity toEntity(CheckCookieAction action) {
        ValueComputationInstructionEntity instruction = instructionMapper.toEntity(action.getComputationInstruction());
        return new CookieSearchDetailsEntity(action.getCookieName(), instruction);
    }

    public CheckCookieAction fromEntity(ActionEntity entity, CookieSearchDetailsEntity details) {
        return fromEntity(entity, details, UnaryOperator.identity());
    }

    public CheckCookieAction fromEntity(ActionEntity entity, CookieSearchDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        ValueComputationInstruction instruction = instructionMapper.fromEntity(details.getValueComputation());
        ValueComputationInstruction resolvedInstruction = instructionResolver.apply(instruction);
        Position position = positionMapper.fromEntity(entity.getPosition());
        return CheckCookieAction.builder()
                                .actionID(entity.getId())
                                .label(entity.getLabel())
                                .nextActions(entity.getNextActions())
                                .cookieName(details.getCookieName())
                                .computationInstruction(resolvedInstruction)
                                .position(position)
                                .build();
    }

}
