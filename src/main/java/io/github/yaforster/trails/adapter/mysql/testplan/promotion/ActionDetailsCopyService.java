package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ActionDetailsCopyService {

    private static final String DUPLICATE_COPY_VARIANT = "Multiple action details copy variants registered for %s";
    private final ElementDatabaseService elementDatabaseService;
    private final ValueComputationInstructionCopyService instructionCopyService;
    private final Map<Class<? extends ActionDetailsEntity>, ActionDetailsCopyVariant> variants;
    private final Map<Class<?>, ActionDetailsCopyVariant> resolvedVariants = new ConcurrentHashMap<>();

    public ActionDetailsCopyService(ElementDatabaseService elementDatabaseService,
            ValueComputationInstructionCopyService instructionCopyService, List<ActionDetailsCopyVariant> variants) {
        this.elementDatabaseService = elementDatabaseService;
        this.instructionCopyService = instructionCopyService;
        this.variants = indexVariants(variants);
    }

    public Optional<ActionDetailsEntity> copy(ActionDetailsEntity details, Long applicationId, Long targetStageId) {
        ActionDetailsCopyContext context = new ActionDetailsCopyContext(elementDatabaseService, instructionCopyService,
                applicationId, targetStageId);
        return variantFor(details).flatMap(variant -> variant.copy(details, context));
    }

    private Optional<ActionDetailsCopyVariant> variantFor(ActionDetailsEntity details) {
        ActionDetailsCopyVariant resolvedVariant = resolvedVariants.get(details.getClass());
        if (resolvedVariant != null) {
            return Optional.of(resolvedVariant);
        }
        Optional<ActionDetailsCopyVariant> variant = variantFor(details.getClass());
        variant.ifPresent(
                actionDetailsCopyVariant -> resolvedVariants.put(details.getClass(), actionDetailsCopyVariant));
        return variant;
    }

    private Optional<ActionDetailsCopyVariant> variantFor(Class<?> detailsType) {
        ActionDetailsCopyVariant exactVariant = variants.get(detailsType);
        if (exactVariant != null) {
            return Optional.of(exactVariant);
        }
        return variants.entrySet()
                       .stream()
                       .filter(entry -> entry.getKey()
                                             .isAssignableFrom(detailsType))
                       .map(Map.Entry::getValue)
                       .findFirst();
    }

    private static Map<Class<? extends ActionDetailsEntity>, ActionDetailsCopyVariant> indexVariants(
            List<ActionDetailsCopyVariant> variants) {
        Map<Class<? extends ActionDetailsEntity>, ActionDetailsCopyVariant> indexedVariants = new LinkedHashMap<>();
        for (ActionDetailsCopyVariant variant : variants) {
            Class<? extends ActionDetailsEntity> key = variant.detailsType();
            ActionDetailsCopyVariant previous = indexedVariants.putIfAbsent(key, variant);
            if (previous != null) {
                String message = DUPLICATE_COPY_VARIANT.formatted(key.getName());
                throw new IllegalStateException(message);
            }
        }
        return Map.copyOf(indexedVariants);
    }

}
