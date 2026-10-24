package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ElementValueCheckDetailsEntity;
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

class ElementValueCheckDetailsMapperTest extends TrailsTest {

    @Test
    void mapsBothDirections() {
        ElementDatabaseService elementDatabaseService = Mockito.mock(ElementDatabaseService.class);
        ValueComputationInstructionEntityMapper instructionMapper = Mockito.mock(
                ValueComputationInstructionEntityMapper.class);
        PositionEntityMapper positionMapper = Mockito.mock(PositionEntityMapper.class);
        ElementValueCheckDetailsMapper mapper = new ElementValueCheckDetailsMapper(elementDatabaseService,
                instructionMapper, positionMapper);

        ElementValueCheckAction action = Mockito.mock(ElementValueCheckAction.class);
        Locator locatorForEntity = new Locator(LocatorType.CSS, ".button");
        ValueComputationInstruction instruction = new FixedValueInstruction("primary");
        ValueComputationInstructionEntity instructionEntity = Mockito.mock(ValueComputationInstructionEntity.class);
        when(action.getLocatorForElementToActOn()).thenReturn(locatorForEntity);
        when(action.getValueSource()).thenReturn(ElementValueSource.ATTRIBUTE);
        when(action.getValueName()).thenReturn("data-variant");
        when(action.getComputationInstruction()).thenReturn(instruction);
        when(elementDatabaseService.getElementIDByLocator(
                new ElementDatabaseService.ElementLocator(1L, 2L, ".button"))).thenReturn(33L);
        when(instructionMapper.toEntity(instruction)).thenReturn(instructionEntity);

        ElementValueCheckDetailsEntity detailsEntity = mapper.toEntity(action, 1L, 2L);
        assertEquals(33L, detailsEntity.getElementID());
        assertEquals(ElementValueSource.ATTRIBUTE, detailsEntity.getValueSource());
        assertEquals("data-variant", detailsEntity.getValueName());
        assertEquals(instructionEntity, detailsEntity.getValueComputation());

        ActionEntity actionEntity = getInstancioOf(ActionEntity.class).create()
                                                                      .setId(13L)
                                                                      .setLabel("attribute check");
        PositionEntity positionEntity = getInstancioOf(PositionEntity.class).create();
        actionEntity.setPosition(positionEntity);
        Locator locatorFromEntity = new Locator(LocatorType.XPATH, "//button");
        Position mappedPosition = getInstancioOf(Position.class).create();
        ValueComputationInstruction mappedInstruction = new FixedValueInstruction("mapped");
        when(elementDatabaseService.getElementLocator(detailsEntity.getElementID())).thenReturn(locatorFromEntity);
        when(instructionMapper.fromEntity(instructionEntity)).thenReturn(mappedInstruction);
        when(positionMapper.fromEntity(positionEntity)).thenReturn(mappedPosition);

        ElementValueCheckAction mappedAction = mapper.fromEntity(actionEntity, detailsEntity);
        assertEquals(actionEntity.getId(), mappedAction.getActionID());
        assertEquals(actionEntity.getLabel(), mappedAction.getLabel());
        assertEquals(actionEntity.getNextActions(), mappedAction.getNextActions());
        assertEquals(locatorFromEntity, mappedAction.getLocatorForElementToActOn());
        assertEquals(ElementValueSource.ATTRIBUTE, mappedAction.getValueSource());
        assertEquals("data-variant", mappedAction.getValueName());
        assertEquals(mappedInstruction, mappedAction.getComputationInstruction());
        assertEquals(mappedPosition, mappedAction.getPosition());
    }

}
