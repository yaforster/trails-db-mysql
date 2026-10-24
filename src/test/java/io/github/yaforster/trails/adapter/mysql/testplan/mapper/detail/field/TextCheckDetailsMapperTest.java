package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.TextCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.ValueComputationInstructionEntityMapper;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.browser.*;
import io.github.yaforster.trails.core.test.action.download.*;
import io.github.yaforster.trails.core.test.action.element.*;
import io.github.yaforster.trails.core.test.action.browser.storage.*;
import io.github.yaforster.trails.core.test.action.value.*;
import io.github.yaforster.trails.core.test.action.browser.viewport.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TextCheckDetailsMapperTest extends TrailsTest {

    @Test
    void mapsBothDirections() {
        ElementDatabaseService elementDatabaseService = Mockito.mock(ElementDatabaseService.class);
        ValueComputationInstructionEntityMapper instructionMapper = Mockito.mock(
                ValueComputationInstructionEntityMapper.class);
        PositionEntityMapper positionMapper = Mockito.mock(PositionEntityMapper.class);
        TextCheckDetailsMapper mapper = new TextCheckDetailsMapper(elementDatabaseService, instructionMapper,
                positionMapper);

        TextCheckAction action = Mockito.mock(TextCheckAction.class);
        Locator locatorForEntity = new Locator(LocatorType.CSS, ".txt");
        ValueComputationInstruction instruction = new FixedValueInstruction("needle");
        ValueComputationInstructionEntity instructionEntity = Mockito.mock(ValueComputationInstructionEntity.class);
        when(action.getLocatorForElementToActOn()).thenReturn(locatorForEntity);
        when(action.getComputationInstruction()).thenReturn(instruction);
        when(elementDatabaseService.getElementIDByLocator(
                new ElementDatabaseService.ElementLocator(1L, 2L, ".txt"))).thenReturn(33L);
        when(instructionMapper.toEntity(instruction)).thenReturn(instructionEntity);

        TextCheckDetailsEntity detailsEntity = mapper.toEntity(action, 1L, 2L);
        assertEquals(33L, detailsEntity.getElementID());
        assertEquals(instructionEntity, detailsEntity.getValueComputation());

        ActionEntity actionEntity = getInstancioOf(ActionEntity.class).create()
                                                                      .setId(13L)
                                                                      .setLabel("text");
        PositionEntity positionEntity = getInstancioOf(PositionEntity.class).create();
        actionEntity.setPosition(positionEntity);
        Locator locatorFromEntity = new Locator(LocatorType.XPATH, "//p");
        Position mappedPosition = getInstancioOf(Position.class).create();
        ValueComputationInstruction mappedInstruction = new FixedValueInstruction("mapped");
        when(elementDatabaseService.getElementLocator(detailsEntity.getElementID())).thenReturn(locatorFromEntity);
        when(instructionMapper.fromEntity(instructionEntity)).thenReturn(mappedInstruction);
        when(positionMapper.fromEntity(positionEntity)).thenReturn(mappedPosition);

        TextCheckAction mappedAction = mapper.fromEntity(actionEntity, detailsEntity);
        assertEquals(actionEntity.getId(), mappedAction.getActionID());
        assertEquals(actionEntity.getLabel(), mappedAction.getLabel());
        assertEquals(actionEntity.getNextActions(), mappedAction.getNextActions());
        assertEquals(locatorFromEntity, mappedAction.getLocatorForElementToActOn());
        assertEquals(mappedInstruction, mappedAction.getComputationInstruction());
        assertEquals(mappedPosition, mappedAction.getPosition());
    }

}
