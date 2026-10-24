package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.TextCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.ValueComputationInstructionEntityMapper;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.core.test.action.element.Locator;
import io.github.yaforster.trails.core.test.action.element.TextCheckAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
@RequiredArgsConstructor
public class TextCheckDetailsMapper {

    private final ElementDatabaseService elementDatabaseService;
    private final ValueComputationInstructionEntityMapper instructionMapper;
    private final PositionEntityMapper positionMapper;

    public TextCheckDetailsEntity toEntity(TextCheckAction action, Long applicationId, Long stageId) {
        String locatorText = action.getLocatorForElementToActOn()
                                   .locatorString();
        ElementDatabaseService.ElementLocator elementLocator = new ElementDatabaseService.ElementLocator(applicationId,
                stageId, locatorText);
        Long elementID = elementDatabaseService.getElementIDByLocator(elementLocator);
        ValueComputationInstructionEntity instruction = instructionMapper.toEntity(action.getComputationInstruction());
        return new TextCheckDetailsEntity(elementID, instruction);
    }

    public TextCheckAction fromEntity(ActionEntity entity, TextCheckDetailsEntity details) {
        return fromEntity(entity, details, UnaryOperator.identity());
    }

    public TextCheckAction fromEntity(ActionEntity entity, TextCheckDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        Locator locator = elementDatabaseService.getElementLocator(details.getElementID());
        ValueComputationInstruction instruction = instructionMapper.fromEntity(details.getValueComputation());
        ValueComputationInstruction resolvedInstruction = instructionResolver.apply(instruction);
        Position position = positionMapper.fromEntity(entity.getPosition());
        return TextCheckAction.builder()
                              .actionID(entity.getId())
                              .label(entity.getLabel())
                              .nextActions(entity.getNextActions())
                              .locatorForElementToActOn(locator)
                              .computationInstruction(resolvedInstruction)
                              .position(position)
                              .build();
    }

}
