package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant.ActionEntityVariantMapper;
import io.github.yaforster.trails.core.MappingException;
import io.github.yaforster.trails.core.test.Action;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/** Owns registration validation and cached runtime-type lookup for action mapping. */
final class ActionEntityMapperRegistry {

    private static final String UNHANDLED_ACTION_TYPE = "Unhandled action type: %s";
    private static final String UNHANDLED_DETAILS_TYPE = "Unhandled ActionDetailsEntity type: %s";
    private static final String DUPLICATE_MAPPER = "Multiple action entity mappers registered for %s %s";
    private final Map<Class<? extends Action>, ActionEntityVariantMapper> actionMappers;
    private final Map<Class<? extends ActionDetailsEntity>, ActionEntityVariantMapper> detailsMappers;
    private final Map<Class<?>, ActionEntityVariantMapper> resolvedActionMappers = new ConcurrentHashMap<>();
    private final Map<Class<?>, ActionEntityVariantMapper> resolvedDetailsMappers = new ConcurrentHashMap<>();

    ActionEntityMapperRegistry(List<ActionEntityVariantMapper> variantMappers) {
        actionMappers = indexVariantMappers(variantMappers, ActionEntityVariantMapper::actionType, "action type");
        detailsMappers = indexVariantMappers(variantMappers, ActionEntityVariantMapper::detailsType, "details type");
    }

    ActionEntityVariantMapper forAction(Class<? extends Action> actionType) {
        return resolvedActionMappers.computeIfAbsent(actionType, type -> {
            String message = UNHANDLED_ACTION_TYPE.formatted(type.getName());
            return findMapper(type, actionMappers, message);
        });
    }

    ActionEntityVariantMapper forDetails(Class<? extends ActionDetailsEntity> detailsType) {
        return resolvedDetailsMappers.computeIfAbsent(detailsType, type -> {
            String message = UNHANDLED_DETAILS_TYPE.formatted(type.getName());
            return findMapper(type, detailsMappers, message);
        });
    }

    private static <T> Map<Class<? extends T>, ActionEntityVariantMapper> indexVariantMappers(
            List<ActionEntityVariantMapper> variantMappers,
            Function<ActionEntityVariantMapper, Class<? extends T>> keyExtractor, String keyDescription) {
        Map<Class<? extends T>, ActionEntityVariantMapper> indexedMappers = new LinkedHashMap<>();
        for (ActionEntityVariantMapper mapper : variantMappers) {
            Class<? extends T> key = keyExtractor.apply(mapper);
            ActionEntityVariantMapper previous = indexedMappers.putIfAbsent(key, mapper);
            if (previous != null) {
                String message = DUPLICATE_MAPPER.formatted(keyDescription, key.getName());
                throw new IllegalStateException(message);
            }
        }
        return Map.copyOf(indexedMappers);
    }

    private static ActionEntityVariantMapper findMapper(Class<?> type,
            Map<? extends Class<?>, ActionEntityVariantMapper> indexedMappers, String errorMessage) {
        ActionEntityVariantMapper exactMapper = indexedMappers.get(type);
        if (exactMapper != null) {
            return exactMapper;
        }
        // Runtime subclasses (including persistence proxies) use their registered base type.
        return indexedMappers.entrySet()
                             .stream()
                             .filter(entry -> entry.getKey()
                                                   .isAssignableFrom(type))
                             .map(Map.Entry::getValue)
                             .findFirst()
                             .orElseThrow(() -> new MappingException(errorMessage));
    }
}
