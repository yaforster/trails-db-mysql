package io.github.yaforster.trails.adapter.mysql.testdata;

import io.github.yaforster.trails.core.data.TestDataEntry;
import io.github.yaforster.trails.core.data.TestDataSet;
import io.github.yaforster.trails.core.definition.TestDataSetDefinition;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class TestDataEntityMapperTest {

    private final TestDataEntityMapper mapper = new TestDataEntityMapper();

    @Test
    void toEntity_ShouldMapTestDataSetDefinition() {
        TestDataSetDefinition definition = new TestDataSetDefinition("dataset",
                List.of(new TestDataEntry("mail", "me@example.com")));

        TestDataSetEntity entity = mapper.toEntity(definition);

        assertEquals("dataset", entity.getLabel());
        assertEquals(1, entity.getValues()
                              .size());
        assertEquals("mail", entity.getValues()
                                   .getFirst()
                                   .getKey());
        assertEquals("me@example.com", entity.getValues()
                                             .getFirst()
                                             .getValue());
        assertSame(entity, entity.getValues()
                                 .getFirst()
                                 .getTestDataSet());
    }

    @Test
    void updateEntity_ShouldReuseExistingEntry_WhenKeyIsStillPresent() {
        TestDataSetEntity entity = new TestDataSetEntity().setLabel("old");
        TestDataEntryEntity existingEntry = new TestDataEntryEntity().setId(10L)
                                                                     .setTestDataSet(entity)
                                                                     .setKey("some value")
                                                                     .setValue("my.test@mail.de");
        entity.getValues()
              .add(existingEntry);

        mapper.updateEntity(entity, new TestDataSetDefinition("My first test data set",
                List.of(new TestDataEntry("some value", "my.test@mail.de"), new TestDataEntry("new value", "test"))));

        assertEquals("My first test data set", entity.getLabel());
        assertEquals(2, entity.getValues()
                              .size());
        assertSame(existingEntry, entity.getValues()
                                        .get(0));
        assertEquals("my.test@mail.de", entity.getValues()
                                              .get(0)
                                              .getValue());
        assertEquals("new value", entity.getValues()
                                        .get(1)
                                        .getKey());
        assertEquals("test", entity.getValues()
                                   .get(1)
                                   .getValue());
        assertSame(entity, entity.getValues()
                                 .get(1)
                                 .getTestDataSet());
    }

    @Test
    void updateEntity_ShouldRemoveEntriesMissingFromDefinition() {
        TestDataSetEntity entity = new TestDataSetEntity();
        entity.getValues()
              .add(new TestDataEntryEntity().setId(10L)
                                            .setTestDataSet(entity)
                                            .setKey("old")
                                            .setValue("unused"));
        TestDataEntryEntity keptEntry = new TestDataEntryEntity().setId(11L)
                                                                 .setTestDataSet(entity)
                                                                 .setKey("kept")
                                                                 .setValue("before");
        entity.getValues()
              .add(keptEntry);

        mapper.updateEntity(entity, new TestDataSetDefinition("dataset", List.of(new TestDataEntry("kept", "after"))));

        assertEquals(1, entity.getValues()
                              .size());
        assertSame(keptEntry, entity.getValues()
                                    .getFirst());
        assertEquals("after", entity.getValues()
                                    .getFirst()
                                    .getValue());
    }

    @Test
    void toDomain_ShouldMapTestDataSetEntity() {
        Instant creationDate = Instant.parse("2026-05-24T15:54:33Z");
        TestDataSetEntity entity = new TestDataSetEntity().setId(1L)
                                                          .setLabel("dataset")
                                                          .setCreationDate(creationDate)
                                                          .setRetired(false);
        entity.getValues()
              .add(new TestDataEntryEntity().setTestDataSet(entity)
                                            .setKey("mail")
                                            .setValue("me@example.com"));

        TestDataSet domain = mapper.toDomain(entity);

        assertEquals(1L, domain.id());
        assertEquals("dataset", domain.label());
        assertEquals(creationDate, domain.creationDate());
        assertEquals(List.of(new TestDataEntry("mail", "me@example.com")), domain.values());
    }

}
