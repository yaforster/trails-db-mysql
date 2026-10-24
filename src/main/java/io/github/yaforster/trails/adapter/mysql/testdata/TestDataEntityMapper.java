package io.github.yaforster.trails.adapter.mysql.testdata;

import io.github.yaforster.trails.core.data.TestDataEntry;
import io.github.yaforster.trails.core.data.TestDataSet;
import io.github.yaforster.trails.core.definition.TestDataSetDefinition;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TestDataEntityMapper {

    public TestDataSetEntity toEntity(TestDataSetDefinition definition) {
        TestDataSetEntity entity = new TestDataSetEntity();
        updateEntity(entity, definition);
        return entity;
    }

    public void updateEntity(TestDataSetEntity entity, TestDataSetDefinition definition) {
        entity.setLabel(definition.label());
        List<TestDataEntry> values = Optional.ofNullable(definition.values())
                                             .orElseGet(List::of);
        Map<String, TestDataEntryEntity> existingValuesByKey = entity.getValues()
                                                                     .stream()
                                                                     .collect(Collectors.toMap(
                                                                             TestDataEntryEntity::getKey,
                                                                             Function.identity()));
        Set<String> requestedKeys = values.stream()
                                          .map(TestDataEntry::key)
                                          .collect(Collectors.toSet());

        // Mutate the managed collection so JPA orphan removal deletes entries omitted by the update.
        entity.getValues()
              .removeIf(value -> !requestedKeys.contains(value.getKey()));
        values.forEach(value -> updateOrAddEntry(entity, existingValuesByKey, value));
    }

    public TestDataSet toDomain(TestDataSetEntity entity) {
        List<TestDataEntry> values = entity.getValues()
                                           .stream()
                                           .map(value -> new TestDataEntry(value.getKey(), value.getValue()))
                                           .toList();
        return new TestDataSet(entity.getId(), entity.getLabel(), entity.getCreationDate(), values, entity.isRetired());
    }

    private TestDataEntryEntity toEntryEntity(TestDataSetEntity testDataSet, TestDataEntry value) {
        return new TestDataEntryEntity().setTestDataSet(testDataSet)
                                        .setKey(value.key())
                                        .setValue(value.value());
    }

    private void updateOrAddEntry(TestDataSetEntity testDataSet, Map<String, TestDataEntryEntity> existingValuesByKey,
            TestDataEntry value) {
        TestDataEntryEntity existing = existingValuesByKey.get(value.key());
        if (existing != null) {
            existing.setValue(value.value());
            return;
        }
        TestDataEntryEntity newEntry = toEntryEntity(testDataSet, value);
        testDataSet.getValues()
                   .add(newEntry);
    }

}
