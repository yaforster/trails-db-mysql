package io.github.yaforster.trails.adapter.mysql.result.testpath;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.app.services.TestPathResultQueryService;
import io.github.yaforster.trails.app.services.TestSetResultQueryService;
import io.github.yaforster.trails.core.persisted.PersistedTestPathResult;
import io.github.yaforster.trails.core.persisted.PersistedTestSetResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TestPathResultDatabaseServiceImplTest {

    private final TestSetResultQueryService testSetResultQueryService = Mockito.mock(TestSetResultQueryService.class);
    private final TestPathResultRepository testPathResultRepository = Mockito.mock(TestPathResultRepository.class);
    private final TestPathResultEntityMapper testPathResultEntityMapper = Mockito.mock(
            TestPathResultEntityMapper.class);
    private final TestPathResultDatabaseServiceImpl service = new TestPathResultDatabaseServiceImpl(
            testSetResultQueryService, testPathResultRepository, testPathResultEntityMapper);

    @Test
    void listPathResults_ShouldReturnEmpty_WhenTestSetResultMissing() {
        when(testSetResultQueryService.getTestSetResult(5L)).thenReturn(Optional.empty());

        Optional<PagedResult<PersistedTestPathResult>> result = service.listPathResults(
                new TestPathResultQueryService.TestSetPathPage(5L, 0, 20));

        assertTrue(result.isEmpty());
        verify(testPathResultRepository, never()).findByTestSetResultIdOrderByIdAsc(any(), any());
    }

    @Test
    void listPathResults_ShouldMapPage_WhenTestSetResultExists() {
        TestPathResultEntity entity = new TestPathResultEntity().setId(1L);
        PersistedTestPathResult mapped = Mockito.mock(PersistedTestPathResult.class);
        when(testSetResultQueryService.getTestSetResult(5L)).thenReturn(
                Optional.of(Mockito.mock(PersistedTestSetResult.class)));
        when(testPathResultRepository.findByTestSetResultIdOrderByIdAsc(5L, PageRequest.of(1, 3))).thenReturn(
                new PageImpl<>(List.of(entity), PageRequest.of(1, 3), 4));
        when(testPathResultEntityMapper.toPersisted(entity)).thenReturn(mapped);

        Optional<PagedResult<PersistedTestPathResult>> result = service.listPathResults(
                new TestPathResultQueryService.TestSetPathPage(5L, 1, 3));

        assertTrue(result.isPresent());
        assertEquals(mapped, result.get()
                                   .getContent()
                                   .getFirst());
    }

    @Test
    void getPathResult_ShouldMapOptional() {
        TestPathResultEntity entity = new TestPathResultEntity().setId(2L);
        PersistedTestPathResult mapped = Mockito.mock(PersistedTestPathResult.class);
        when(testPathResultRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(testPathResultEntityMapper.toPersisted(entity)).thenReturn(mapped);

        Optional<PersistedTestPathResult> result = service.getPathResult(2L);

        assertTrue(result.isPresent());
        assertEquals(mapped, result.get());
    }

}
