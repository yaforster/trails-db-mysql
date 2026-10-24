package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SwitchWebsiteDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.ValueComputationInstructionEntityMapper;
import io.github.yaforster.trails.core.test.action.browser.SwitchWebsiteAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class SwitchWebsiteDetailsMapper {

    private final ValueComputationInstructionEntityMapper instructionMapper;
    private final PositionEntityMapper positionMapper;

    public SwitchWebsiteDetailsEntity toEntity(SwitchWebsiteAction action) {
        ValueComputationInstructionEntity instruction = instructionMapper.toEntity(action.getComputationInstruction());
        return new SwitchWebsiteDetailsEntity(instruction);
    }

    public SwitchWebsiteAction fromEntity(ActionEntity entity, SwitchWebsiteDetailsEntity details) {
        return fromEntity(entity, details, UnaryOperator.identity());
    }

    public SwitchWebsiteAction fromEntity(ActionEntity entity, SwitchWebsiteDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        ValueComputationInstruction instruction = instructionMapper.fromEntity(details.getValueComputation());
        ValueComputationInstruction resolvedInstruction = instructionResolver.apply(instruction);
        Position position = positionMapper.fromEntity(entity.getPosition());
        return SwitchWebsiteAction.builder()
                                  .actionID(entity.getId())
                                  .label(entity.getLabel())
                                  .nextActions(entity.getNextActions())
                                  .computationInstruction(resolvedInstruction)
                                  .position(position)
                                  .build();
    }

}
