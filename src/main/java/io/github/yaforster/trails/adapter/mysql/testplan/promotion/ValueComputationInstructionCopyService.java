package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ValueComputationInstructionCopyService {

    private static final String UNHANDLED_INSTRUCTION = "Unhandled value computation instruction: %s";
    private static final String DUPLICATE_COPY_VARIANT = "Multiple value computation instruction copy variants registered for %s";
    private final Map<Class<? extends ValueComputationInstructionEntity>, ValueComputationInstructionCopyVariant> variants;
    private final Map<Class<?>, ValueComputationInstructionCopyVariant> resolvedVariants = new ConcurrentHashMap<>();

    public ValueComputationInstructionCopyService(List<ValueComputationInstructionCopyVariant> variants) {
        this.variants = indexVariants(variants);
    }

    public ValueComputationInstructionEntity copy(ValueComputationInstructionEntity instruction) {
        if (instruction == null) {
            return null;
        }
        ValueComputationInstructionCopyVariant variant = variantFor(instruction);
        return variant.copy(instruction);
    }

    private ValueComputationInstructionCopyVariant variantFor(ValueComputationInstructionEntity instruction) {
        ValueComputationInstructionCopyVariant resolvedVariant = resolvedVariants.get(instruction.getClass());
        if (resolvedVariant != null) {
            return resolvedVariant;
        }
        ValueComputationInstructionCopyVariant variant = variantFor(instruction.getClass());
        resolvedVariants.put(instruction.getClass(), variant);
        return variant;
    }

    private ValueComputationInstructionCopyVariant variantFor(Class<?> instructionType) {
        ValueComputationInstructionCopyVariant exactVariant = variants.get(instructionType);
        if (exactVariant != null) {
            return exactVariant;
        }
        return variants.entrySet()
                       .stream()
                       .filter(entry -> entry.getKey()
                                             .isAssignableFrom(instructionType))
                       .map(Map.Entry::getValue)
                       .findFirst()
                       .orElseThrow(() -> {
                           String message = UNHANDLED_INSTRUCTION.formatted(instructionType.getName());
                           return new IllegalStateException(message);
                       });
    }

    private static Map<Class<? extends ValueComputationInstructionEntity>, ValueComputationInstructionCopyVariant> indexVariants(
            List<ValueComputationInstructionCopyVariant> variants) {
        Map<Class<? extends ValueComputationInstructionEntity>, ValueComputationInstructionCopyVariant> indexedVariants = new LinkedHashMap<>();
        for (ValueComputationInstructionCopyVariant variant : variants) {
            Class<? extends ValueComputationInstructionEntity> key = variant.instructionType();
            ValueComputationInstructionCopyVariant previous = indexedVariants.putIfAbsent(key, variant);
            if (previous != null) {
                String message = DUPLICATE_COPY_VARIANT.formatted(key.getName());
                throw new IllegalStateException(message);
            }
        }
        return Map.copyOf(indexedVariants);
    }

}
