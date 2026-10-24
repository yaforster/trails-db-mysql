package io.github.yaforster.trails.adapter.mysql.testplan.mapper;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.EnvInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.FixedValueEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.RandomValueInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.RelativeDateInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.TestDataValueInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.TimeStampInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.*;
import io.github.yaforster.trails.core.test.action.browser.*;
import io.github.yaforster.trails.core.test.action.download.*;
import io.github.yaforster.trails.core.test.action.element.*;
import io.github.yaforster.trails.core.test.action.browser.storage.*;
import io.github.yaforster.trails.core.test.action.value.*;
import io.github.yaforster.trails.core.test.action.browser.viewport.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValueComputationInstructionEntityMapperTest {

    private final ValueComputationInstructionEntityMapper mapper = new ValueComputationInstructionEntityMapper();

    @Test
    void toEntity_FixedValueInstruction_MapsToFixedValueEntity() {
        FixedValueInstruction instruction = new FixedValueInstruction("fixed");

        ValueComputationInstructionEntity entity = mapper.toEntity(instruction);

        assertInstanceOf(FixedValueEntity.class, entity);
        assertEquals("fixed", ((FixedValueEntity) entity).getValue());
    }

    @Test
    void toEntity_RelativeDateInstruction_MapsToRelativeDateInstructionEntity() {
        RelativeDateInstruction instruction = new RelativeDateInstruction(3, "yyyy-MM-dd");

        ValueComputationInstructionEntity entity = mapper.toEntity(instruction);

        assertInstanceOf(RelativeDateInstructionEntity.class, entity);
        RelativeDateInstructionEntity mapped = (RelativeDateInstructionEntity) entity;
        assertEquals(3, mapped.getOffsetDays());
        assertEquals("yyyy-MM-dd", mapped.getFormatPattern());
    }

    @Test
    void toEntity_RandomValueInstruction_MapsToRandomValueInstructionEntity() {
        RandomValueInstruction instruction = new RandomValueInstruction("pre-", "-suf", "abc", 5);

        ValueComputationInstructionEntity entity = mapper.toEntity(instruction);

        assertInstanceOf(RandomValueInstructionEntity.class, entity);
        RandomValueInstructionEntity mapped = (RandomValueInstructionEntity) entity;
        assertEquals("pre-", mapped.getPrefix());
        assertEquals("-suf", mapped.getSuffix());
        assertEquals("abc", mapped.getCharPool());
        assertEquals(5, mapped.getRandomStringLength());
    }

    @Test
    void toEntity_EnvInstruction_MapsToEnvInstructionEntity() {
        EnvInstruction instruction = new EnvInstruction("TOKEN", "fallback");

        ValueComputationInstructionEntity entity = mapper.toEntity(instruction);

        assertInstanceOf(EnvInstructionEntity.class, entity);
        EnvInstructionEntity mapped = (EnvInstructionEntity) entity;
        assertEquals("TOKEN", mapped.getVariableName());
        assertEquals("fallback", mapped.getDefaultValue());
    }

    @Test
    void toEntity_TimeStampInstruction_MapsToTimeStampInstructionEntity() {
        TimeStampInstruction instruction = new TimeStampInstruction("HH:mm:ss");

        ValueComputationInstructionEntity entity = mapper.toEntity(instruction);

        assertInstanceOf(TimeStampInstructionEntity.class, entity);
        assertEquals("HH:mm:ss", ((TimeStampInstructionEntity) entity).getFormatPattern());
    }

    @Test
    void toEntity_TestDataValueInstruction_MapsToTestDataValueInstructionEntity() {
        TestDataValueInstruction instruction = new TestDataValueInstruction(12L, "email");

        ValueComputationInstructionEntity entity = mapper.toEntity(instruction);

        assertInstanceOf(TestDataValueInstructionEntity.class, entity);
        TestDataValueInstructionEntity mapped = (TestDataValueInstructionEntity) entity;
        assertEquals(12L, mapped.getTestDataId());
        assertEquals("email", mapped.getTestDataKey());
    }

    @Test
    void fromEntity_FixedValueEntity_MapsToFixedValueInstruction() {
        FixedValueEntity entity = new FixedValueEntity("fixed");

        ValueComputationInstruction instruction = mapper.fromEntity(entity);

        assertInstanceOf(FixedValueInstruction.class, instruction);
        assertEquals("fixed", ((FixedValueInstruction) instruction).value());
    }

    @Test
    void fromEntity_RelativeDateInstructionEntity_MapsToRelativeDateInstruction() {
        RelativeDateInstructionEntity entity = new RelativeDateInstructionEntity(10, "dd.MM.yyyy");

        ValueComputationInstruction instruction = mapper.fromEntity(entity);

        assertInstanceOf(RelativeDateInstruction.class, instruction);
        RelativeDateInstruction mapped = (RelativeDateInstruction) instruction;
        assertEquals(10, mapped.offsetDays());
        assertEquals("dd.MM.yyyy", mapped.formatPattern());
    }

    @Test
    void fromEntity_RandomValueInstructionEntity_MapsToRandomValueInstruction() {
        RandomValueInstructionEntity entity = new RandomValueInstructionEntity("pre", 7, "xyz", "suf");

        ValueComputationInstruction instruction = mapper.fromEntity(entity);

        assertInstanceOf(RandomValueInstruction.class, instruction);
        RandomValueInstruction mapped = (RandomValueInstruction) instruction;
        assertEquals("pre", mapped.prefix());
        assertEquals("suf", mapped.suffix());
        assertEquals("xyz", mapped.charPool());
        assertEquals(7, mapped.randomStringLength());
    }

    @Test
    void fromEntity_EnvInstructionEntity_MapsToEnvInstruction() {
        EnvInstructionEntity entity = new EnvInstructionEntity("ENV_NAME", "default");

        ValueComputationInstruction instruction = mapper.fromEntity(entity);

        assertInstanceOf(EnvInstruction.class, instruction);
        EnvInstruction mapped = (EnvInstruction) instruction;
        assertEquals("ENV_NAME", mapped.variableName());
        assertEquals("default", mapped.defaultValue());
    }

    @Test
    void fromEntity_TimeStampInstructionEntity_MapsToTimeStampInstruction() {
        TimeStampInstructionEntity entity = new TimeStampInstructionEntity("yyyy/MM/dd HH:mm");

        ValueComputationInstruction instruction = mapper.fromEntity(entity);

        assertInstanceOf(TimeStampInstruction.class, instruction);
        assertEquals("yyyy/MM/dd HH:mm", ((TimeStampInstruction) instruction).formatPattern());
    }

    @Test
    void fromEntity_TestDataValueInstructionEntity_MapsToTestDataValueInstruction() {
        TestDataValueInstructionEntity entity = new TestDataValueInstructionEntity(15L, "email");

        ValueComputationInstruction instruction = mapper.fromEntity(entity);

        assertInstanceOf(TestDataValueInstruction.class, instruction);
        TestDataValueInstruction mapped = (TestDataValueInstruction) instruction;
        assertEquals(15L, mapped.testDataId());
        assertEquals("email", mapped.key());
    }

    @Test
    void fromEntity_UnknownEntityType_ThrowsIllegalArgumentException() {
        ValueComputationInstructionEntity unsupportedEntity = new ValueComputationInstructionEntity() {
        };

        assertThrows(IllegalArgumentException.class, () -> mapper.fromEntity(unsupportedEntity));
    }

}
