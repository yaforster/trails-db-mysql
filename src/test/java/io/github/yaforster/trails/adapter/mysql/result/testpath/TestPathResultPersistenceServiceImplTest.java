package io.github.yaforster.trails.adapter.mysql.result.testpath;

import io.github.yaforster.trails.app.services.TestPathResultPersistence;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class TestPathResultPersistenceServiceImplTest {

    private final TestPathResultRepository repository = Mockito.mock(TestPathResultRepository.class);
    private final TestPathResultPersistenceServiceImpl persistence = new TestPathResultPersistenceServiceImpl(
            repository);

    @Test
    void store_ShouldReturnStoredPathId() {
        TestPathResultEntity storedEntity = new TestPathResultEntity().setId(8L);
        when(repository.save(any(TestPathResultEntity.class))).thenReturn(storedEntity);

        Long storedId = persistence.store(new TestPathResultPersistence.NamedTestPath(5L, "Path 1"));

        assertEquals(8L, storedId);
    }

}
