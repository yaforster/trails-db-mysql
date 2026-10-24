package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SessionStorageSearchDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.ValueComputationInstructionEntityMapper;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckSessionStorageAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class CheckSessionStorageDetailsMapper {

    private final ValueComputationInstructionEntityMapper instructionMapper;
    private final PositionEntityMapper positionMapper;

    public SessionStorageSearchDetailsEntity toEntity(CheckSessionStorageAction action) {
        ValueComputationInstructionEntity instruction = instructionMapper.toEntity(action.getComputationInstruction());
        return new SessionStorageSearchDetailsEntity(action.getSessionStorageItemKey(), instruction);
    }

    public CheckSessionStorageAction fromEntity(ActionEntity entity, SessionStorageSearchDetailsEntity details) {
        return fromEntity(entity, details, UnaryOperator.identity());
    }

    public CheckSessionStorageAction fromEntity(ActionEntity entity, SessionStorageSearchDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        ValueComputationInstruction instruction = instructionMapper.fromEntity(details.getValueComputation());
        ValueComputationInstruction resolvedInstruction = instructionResolver.apply(instruction);
        Position position = positionMapper.fromEntity(entity.getPosition());
        return CheckSessionStorageAction.builder()
                                        .actionID(entity.getId())
                                        .label(entity.getLabel())
                                        .nextActions(entity.getNextActions())
                                        .sessionStorageItemKey(details.getStorageKey())
                                        .computationInstruction(resolvedInstruction)
                                        .position(position)
                                        .build();
    }

}
