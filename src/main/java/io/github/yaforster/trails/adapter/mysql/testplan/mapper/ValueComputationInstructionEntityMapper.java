package io.github.yaforster.trails.adapter.mysql.testplan.mapper;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.*;
import io.github.yaforster.trails.core.test.action.value.*;
import org.springframework.stereotype.Component;

@Component
public class ValueComputationInstructionEntityMapper {

    private static final String UNSUPPORTED_INSTRUCTION_ENTITY = "Unsupported ValueComputationInstructionEntity: %s";

    public ValueComputationInstructionEntity toEntity(ValueComputationInstruction instruction) {
        return switch (instruction) {
            case FixedValueInstruction fixedValue -> new FixedValueEntity(fixedValue.value());
            case RelativeDateInstruction relativeDate ->
                new RelativeDateInstructionEntity(relativeDate.offsetDays(), relativeDate.formatPattern());
            case RandomValueInstruction randomValue -> new RandomValueInstructionEntity(randomValue.prefix(),
                    randomValue.randomStringLength(), randomValue.charPool(), randomValue.suffix());
            case EnvInstruction envInstruction ->
                new EnvInstructionEntity(envInstruction.variableName(), envInstruction.defaultValue());
            case TimeStampInstruction timeStampInstruction ->
                new TimeStampInstructionEntity(timeStampInstruction.formatPattern());
            case TestDataValueInstruction testDataValueInstruction -> new TestDataValueInstructionEntity(
                    testDataValueInstruction.testDataId(), testDataValueInstruction.key());
        };
    }

    public ValueComputationInstruction fromEntity(ValueComputationInstructionEntity entity) {
        return switch (entity) {
            case FixedValueEntity fixedValueEntity -> new FixedValueInstruction(fixedValueEntity.getValue());
            case RelativeDateInstructionEntity relativeDateInstructionEntity -> new RelativeDateInstruction(
                    relativeDateInstructionEntity.getOffsetDays(), relativeDateInstructionEntity.getFormatPattern());
            case RandomValueInstructionEntity randomValueInstructionEntity -> new RandomValueInstruction(
                    randomValueInstructionEntity.getPrefix(), randomValueInstructionEntity.getSuffix(),
                    randomValueInstructionEntity.getCharPool(), randomValueInstructionEntity.getRandomStringLength());
            case EnvInstructionEntity envInstructionEntity ->
                new EnvInstruction(envInstructionEntity.getVariableName(), envInstructionEntity.getDefaultValue());
            case TimeStampInstructionEntity timeStampInstructionEntity ->
                new TimeStampInstruction(timeStampInstructionEntity.getFormatPattern());
            case TestDataValueInstructionEntity testDataValueInstructionEntity -> new TestDataValueInstruction(
                    testDataValueInstructionEntity.getTestDataId(), testDataValueInstructionEntity.getTestDataKey());
            default -> {
                String message = UNSUPPORTED_INSTRUCTION_ENTITY.formatted(entity.getClass()
                                                                                .getName());
                throw new IllegalArgumentException(message);
            }
        };
    }

}
