package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.CookieSearchDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.ValueComputationInstructionEntityMapper;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckCookieAction;
import io.github.yaforster.trails.core.test.action.value.FixedValueInstruction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CheckCookieDetailsMapperTest extends TrailsTest {

    @Test
    void mapsBothDirections() {
        ValueComputationInstructionEntityMapper instructionMapper = Mockito.mock(
                ValueComputationInstructionEntityMapper.class);
        PositionEntityMapper positionMapper = Mockito.mock(PositionEntityMapper.class);
        CheckCookieDetailsMapper mapper = new CheckCookieDetailsMapper(instructionMapper, positionMapper);

        CheckCookieAction action = Mockito.mock(CheckCookieAction.class);
        ValueComputationInstruction instruction = new FixedValueInstruction("value");
        ValueComputationInstructionEntity instructionEntity = Mockito.mock(ValueComputationInstructionEntity.class);
        when(action.getCookieName()).thenReturn("cookie");
        when(action.getComputationInstruction()).thenReturn(instruction);
        when(instructionMapper.toEntity(instruction)).thenReturn(instructionEntity);

        CookieSearchDetailsEntity detailsEntity = mapper.toEntity(action);
        assertEquals("cookie", detailsEntity.getCookieName());
        assertEquals(instructionEntity, detailsEntity.getValueComputation());

        ActionEntity actionEntity = getInstancioOf(ActionEntity.class).create()
                                                                      .setId(1L)
                                                                      .setLabel("cookie");
        PositionEntity positionEntity = getInstancioOf(PositionEntity.class).create();
        actionEntity.setPosition(positionEntity);
        Position mappedPosition = getInstancioOf(Position.class).create();
        ValueComputationInstruction mappedInstruction = new FixedValueInstruction("mapped");
        when(instructionMapper.fromEntity(instructionEntity)).thenReturn(mappedInstruction);
        when(positionMapper.fromEntity(positionEntity)).thenReturn(mappedPosition);

        CheckCookieAction mappedAction = mapper.fromEntity(actionEntity, detailsEntity);
        assertEquals(actionEntity.getId(), mappedAction.getActionID());
        assertEquals(actionEntity.getLabel(), mappedAction.getLabel());
        assertEquals(actionEntity.getNextActions(), mappedAction.getNextActions());
        assertEquals(detailsEntity.getCookieName(), mappedAction.getCookieName());
        assertEquals(mappedInstruction, mappedAction.getComputationInstruction());
        assertEquals(mappedPosition, mappedAction.getPosition());
    }

}
