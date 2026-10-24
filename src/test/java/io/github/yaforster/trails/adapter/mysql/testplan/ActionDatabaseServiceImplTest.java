package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PersistedActionMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.repo.ActionRepository;
import io.github.yaforster.trails.adapter.mysql.testplan.repo.TestPlanRepository;
import io.github.yaforster.trails.app.services.ActionQueryService;
import io.github.yaforster.trails.core.persisted.PersistedAction;
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

class ActionDatabaseServiceImplTest {

    private final TestPlanRepository testPlanRepository = Mockito.mock(TestPlanRepository.class);
    private final ActionRepository actionRepository = Mockito.mock(ActionRepository.class);
    private final PersistedActionMapper persistedActionMapper = Mockito.mock(PersistedActionMapper.class);
    private final ActionDatabaseServiceImpl service = new ActionDatabaseServiceImpl(testPlanRepository,
            actionRepository, persistedActionMapper);

    @Test
    void listActions_ShouldReturnEmpty_WhenTestPlanDoesNotExist() {
        when(testPlanRepository.findByApplicationIdAndStageIdAndIdAndRetiredFalse(1L, 2L, 3L)).thenReturn(
                Optional.empty());

        Optional<PagedResult<PersistedAction>> result = service.listActions(
                new ActionQueryService.ActionPage(1L, 2L, 3L, 0, 10));

        assertTrue(result.isEmpty());
        verify(actionRepository, never()).findByTestPlanIdOrderByIdAsc(any(), any());
    }

    @Test
    void listActions_ShouldMapPage_WhenTestPlanExists() {
        ActionEntity entity = new ActionEntity().setId(11L);
        PersistedAction persisted = new PersistedAction(11L, 22L, 33L, null);
        when(testPlanRepository.findByApplicationIdAndStageIdAndIdAndRetiredFalse(1L, 2L, 3L)).thenReturn(
                Optional.of(Mockito.mock()));
        when(actionRepository.findByTestPlanIdOrderByIdAsc(3L, PageRequest.of(1, 5))).thenReturn(
                new PageImpl<>(List.of(entity), PageRequest.of(1, 5), 6));
        when(persistedActionMapper.toPersisted(entity)).thenReturn(persisted);

        Optional<PagedResult<PersistedAction>> result = service.listActions(
                new ActionQueryService.ActionPage(1L, 2L, 3L, 1, 5));

        assertTrue(result.isPresent());
        assertEquals(1, result.get()
                              .getContent()
                              .size());
        assertEquals(persisted, result.get()
                                      .getContent()
                                      .getFirst());
    }

}
