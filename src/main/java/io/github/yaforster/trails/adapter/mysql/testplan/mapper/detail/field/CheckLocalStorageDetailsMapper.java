package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.LocalStorageSearchDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.ValueComputationInstructionEntityMapper;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckLocalStorageAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class CheckLocalStorageDetailsMapper {

    private final ValueComputationInstructionEntityMapper instructionMapper;
    private final PositionEntityMapper positionMapper;

    public LocalStorageSearchDetailsEntity toEntity(CheckLocalStorageAction action) {
        ValueComputationInstructionEntity instruction = instructionMapper.toEntity(action.getComputationInstruction());
        return new LocalStorageSearchDetailsEntity(action.getLocalStorageItemKey(), instruction);
    }

    public CheckLocalStorageAction fromEntity(ActionEntity entity, LocalStorageSearchDetailsEntity details) {
        return fromEntity(entity, details, UnaryOperator.identity());
    }

    public CheckLocalStorageAction fromEntity(ActionEntity entity, LocalStorageSearchDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        ValueComputationInstruction instruction = instructionMapper.fromEntity(details.getValueComputation());
        ValueComputationInstruction resolvedInstruction = instructionResolver.apply(instruction);
        Position position = positionMapper.fromEntity(entity.getPosition());
        return CheckLocalStorageAction.builder()
                                      .actionID(entity.getId())
                                      .label(entity.getLabel())
                                      .nextActions(entity.getNextActions())
                                      .localStorageItemKey(details.getStorageKey())
                                      .computationInstruction(resolvedInstruction)
                                      .position(position)
                                      .build();
    }

}
