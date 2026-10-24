package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.TypingDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.ValueComputationInstructionEntityMapper;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.core.test.action.element.Locator;
import io.github.yaforster.trails.core.test.action.element.TypingAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class TypingDetailsMapper {

    private final ElementDatabaseService elementDatabaseService;
    private final ValueComputationInstructionEntityMapper instructionMapper;
    private final PositionEntityMapper positionMapper;

    public TypingDetailsEntity toEntity(TypingAction action, Long applicationId, Long stageId) {
        String locatorText = action.getLocatorForElementToActOn()
                                   .locatorString();
        ElementDatabaseService.ElementLocator elementLocator = new ElementDatabaseService.ElementLocator(applicationId,
                stageId, locatorText);
        Long elementID = elementDatabaseService.getElementIDByLocator(elementLocator);
        ValueComputationInstructionEntity instruction = instructionMapper.toEntity(action.getComputationInstruction());
        return new TypingDetailsEntity(elementID, action.isClearBeforeTyping(), action.getDelayAfterClearMillis(),
                instruction);
    }

    public TypingAction fromEntity(ActionEntity entity, TypingDetailsEntity details) {
        return fromEntity(entity, details, UnaryOperator.identity());
    }

    public TypingAction fromEntity(ActionEntity entity, TypingDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        Locator locator = elementDatabaseService.getElementLocator(details.getElementID());
        ValueComputationInstruction instruction = instructionMapper.fromEntity(details.getValueComputation());
        ValueComputationInstruction resolvedInstruction = instructionResolver.apply(instruction);
        Position position = positionMapper.fromEntity(entity.getPosition());
        return TypingAction.builder()
                           .actionID(entity.getId())
                           .label(entity.getLabel())
                           .nextActions(entity.getNextActions())
                           .locatorForElementToActOn(locator)
                           .clearBeforeTyping(details.isClearBeforeTyping())
                           .delayAfterClearMillis(details.getDelayAfterClearMillis())
                           .computationInstruction(resolvedInstruction)
                           .position(position)
                           .build();
    }

}
