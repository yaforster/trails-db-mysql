package io.github.yaforster.trails.adapter.mysql.result.testset;

import io.github.yaforster.trails.app.services.TestSetResultPersistence;
import io.github.yaforster.trails.core.test.TestSetResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TestSetResultPersistenceServiceImplTest {

    private final TestSetResultRepository repository = Mockito.mock(TestSetResultRepository.class);
    private final TestSetResultEntityMapper mapper = Mockito.mock(TestSetResultEntityMapper.class);
    private final TestSetResultPersistenceServiceImpl persistence = new TestSetResultPersistenceServiceImpl(repository,
            mapper);

    @Test
    void store_ShouldReturnStoredTestSetId() {
        TestSetResult result = Mockito.mock(TestSetResult.class);
        TestSetResultEntity entity = new TestSetResultEntity();
        TestSetResultEntity storedEntity = new TestSetResultEntity().setId(77L);
        when(mapper.toEntity(99L, result)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(storedEntity);

        Long storedId = persistence.store(new TestSetResultPersistence.TestSetResultToStore(99L, result));

        assertEquals(77L, storedId);
    }

}
