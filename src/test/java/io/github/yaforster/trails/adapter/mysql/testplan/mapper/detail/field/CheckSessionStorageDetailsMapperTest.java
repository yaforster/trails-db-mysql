package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SessionStorageSearchDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.ValueComputationInstructionEntityMapper;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckSessionStorageAction;
import io.github.yaforster.trails.core.test.action.value.FixedValueInstruction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CheckSessionStorageDetailsMapperTest extends TrailsTest {

    @Test
    void mapsBothDirections() {
        ValueComputationInstructionEntityMapper instructionMapper = Mockito.mock(
                ValueComputationInstructionEntityMapper.class);
        PositionEntityMapper positionMapper = Mockito.mock(PositionEntityMapper.class);
        CheckSessionStorageDetailsMapper mapper = new CheckSessionStorageDetailsMapper(instructionMapper,
                positionMapper);

        CheckSessionStorageAction action = Mockito.mock(CheckSessionStorageAction.class);
        ValueComputationInstruction instruction = new FixedValueInstruction("value");
        ValueComputationInstructionEntity instructionEntity = Mockito.mock(ValueComputationInstructionEntity.class);
        when(action.getSessionStorageItemKey()).thenReturn("ss-key");
        when(action.getComputationInstruction()).thenReturn(instruction);
        when(instructionMapper.toEntity(instruction)).thenReturn(instructionEntity);

        SessionStorageSearchDetailsEntity detailsEntity = mapper.toEntity(action);
        assertEquals("ss-key", detailsEntity.getStorageKey());
        assertEquals(instructionEntity, detailsEntity.getValueComputation());

        ActionEntity actionEntity = getInstancioOf(ActionEntity.class).create()
                                                                      .setId(3L)
                                                                      .setLabel("ss");
        PositionEntity positionEntity = getInstancioOf(PositionEntity.class).create();
        actionEntity.setPosition(positionEntity);
        Position mappedPosition = getInstancioOf(Position.class).create();
        ValueComputationInstruction mappedInstruction = new FixedValueInstruction("mapped");
        when(instructionMapper.fromEntity(instructionEntity)).thenReturn(mappedInstruction);
        when(positionMapper.fromEntity(positionEntity)).thenReturn(mappedPosition);

        CheckSessionStorageAction mappedAction = mapper.fromEntity(actionEntity, detailsEntity);
        assertEquals(actionEntity.getId(), mappedAction.getActionID());
        assertEquals(actionEntity.getLabel(), mappedAction.getLabel());
        assertEquals(actionEntity.getNextActions(), mappedAction.getNextActions());
        assertEquals(detailsEntity.getStorageKey(), mappedAction.getSessionStorageItemKey());
        assertEquals(mappedInstruction, mappedAction.getComputationInstruction());
        assertEquals(mappedPosition, mappedAction.getPosition());
    }

}
