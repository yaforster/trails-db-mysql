package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SelectionDetailsEntity;
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

class SelectDetailsMapperTest extends TrailsTest {

    @Test
    void mapsBothDirections() {
        ElementDatabaseService elementDatabaseService = Mockito.mock(ElementDatabaseService.class);
        ValueComputationInstructionEntityMapper instructionMapper = Mockito.mock(
                ValueComputationInstructionEntityMapper.class);
        PositionEntityMapper positionMapper = Mockito.mock(PositionEntityMapper.class);
        SelectDetailsMapper mapper = new SelectDetailsMapper(elementDatabaseService, instructionMapper, positionMapper);

        SelectAction action = Mockito.mock(SelectAction.class);
        Locator locatorForEntity = new Locator(LocatorType.CSS, "#select");
        ValueComputationInstruction instruction = new FixedValueInstruction("option");
        ValueComputationInstructionEntity instructionEntity = Mockito.mock(ValueComputationInstructionEntity.class);
        when(action.getLocatorForElementToActOn()).thenReturn(locatorForEntity);
        when(action.getComputationInstruction()).thenReturn(instruction);
        when(elementDatabaseService.getElementIDByLocator(
                new ElementDatabaseService.ElementLocator(1L, 2L, "#select"))).thenReturn(99L);
        when(instructionMapper.toEntity(instruction)).thenReturn(instructionEntity);

        SelectionDetailsEntity detailsEntity = mapper.toEntity(action, 1L, 2L);
        assertEquals(99L, detailsEntity.getElementID());
        assertEquals(instructionEntity, detailsEntity.getValueComputation());

        ActionEntity actionEntity = getInstancioOf(ActionEntity.class).create()
                                                                      .setId(12L)
                                                                      .setLabel("select");
        PositionEntity positionEntity = getInstancioOf(PositionEntity.class).create();
        actionEntity.setPosition(positionEntity);
        Locator locatorFromEntity = new Locator(LocatorType.XPATH, "//select");
        Position mappedPosition = getInstancioOf(Position.class).create();
        ValueComputationInstruction mappedInstruction = new FixedValueInstruction("mapped");
        when(elementDatabaseService.getElementLocator(detailsEntity.getElementID())).thenReturn(locatorFromEntity);
        when(instructionMapper.fromEntity(instructionEntity)).thenReturn(mappedInstruction);
        when(positionMapper.fromEntity(positionEntity)).thenReturn(mappedPosition);

        SelectAction mappedAction = mapper.fromEntity(actionEntity, detailsEntity);
        assertEquals(actionEntity.getId(), mappedAction.getActionID());
        assertEquals(actionEntity.getLabel(), mappedAction.getLabel());
        assertEquals(actionEntity.getNextActions(), mappedAction.getNextActions());
        assertEquals(locatorFromEntity, mappedAction.getLocatorForElementToActOn());
        assertEquals(mappedInstruction, mappedAction.getComputationInstruction());
        assertEquals(mappedPosition, mappedAction.getPosition());
    }

}
