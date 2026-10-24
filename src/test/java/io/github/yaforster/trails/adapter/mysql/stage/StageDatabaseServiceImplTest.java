package io.github.yaforster.trails.adapter.mysql.stage;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.app.services.StageDatabaseService;
import io.github.yaforster.trails.core.definition.StageDefinition;
import io.github.yaforster.trails.core.deletion.DatabaseDeletionResult;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import io.github.yaforster.trails.core.persisted.PersistedStage;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class StageDatabaseServiceImplTest {

    private final StageRepository repository = Mockito.mock(StageRepository.class);
    private final StageEntityMapper stageEntityMapper = Mockito.mock(StageEntityMapper.class);
    private final StageDatabaseServiceImpl service = new StageDatabaseServiceImpl(repository, stageEntityMapper);

    @Test
    void storeStage_ShouldDelegateToMapperAndRepository() {
        StageDefinition definition = new StageDefinition("label", "https://x", List.of());
        StageEntity entity = new StageEntity();
        StageEntity saved = new StageEntity().setId(7L);
        PersistedStage persisted = Mockito.mock(PersistedStage.class);
        when(stageEntityMapper.toEntity(3L, definition)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(saved);
        when(stageEntityMapper.toPersisted(saved)).thenReturn(persisted);

        PersistedStage result = service.storeStage(new StageDatabaseService.StageCreation(3L, definition));

        assertEquals(persisted, result);
    }

    @Test
    void getStages_ShouldMapPage() {
        StageEntity entity = new StageEntity().setId(1L);
        PersistedStage persisted = Mockito.mock(PersistedStage.class);
        when(repository.findAllByApplicationIdAndRetiredFalse(3L, PageRequest.of(0, 5))).thenReturn(
                new PageImpl<>(List.of(entity), PageRequest.of(0, 5), 1));
        when(stageEntityMapper.toPersisted(entity)).thenReturn(persisted);

        PagedResult<PersistedStage> result = service.getStages(new StageDatabaseService.StagePage(3L, 0, 5, false));

        assertEquals(1, result.getContent()
                              .size());
        assertEquals(persisted, result.getContent()
                                      .getFirst());
    }

    @Test
    void getStage_ShouldMapOptional() {
        StageEntity entity = new StageEntity().setId(2L);
        PersistedStage persisted = Mockito.mock(PersistedStage.class);
        when(repository.findByIdAndApplicationId(2L, 3L)).thenReturn(Optional.of(entity));
        when(stageEntityMapper.toPersisted(entity)).thenReturn(persisted);

        Optional<PersistedStage> result = service.getStage(new StageDatabaseService.StageDetails(3L, 2L, true));

        assertTrue(result.isPresent());
        assertEquals(persisted, result.get());
    }

    @Test
    void getStage_ShouldReturnEmpty_WhenMissing() {
        when(repository.findByIdAndApplicationId(2L, 3L)).thenReturn(Optional.empty());

        Optional<PersistedStage> result = service.getStage(new StageDatabaseService.StageDetails(3L, 2L, true));

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteStage_ShouldReturnNotFound_WhenEntityMissing() {
        when(repository.findByIdAndApplicationId(2L, 3L)).thenReturn(Optional.empty());

        DatabaseDeletionResult result = service.deleteStage(new StageDatabaseService.StageReference(3L, 2L));

        assertInstanceOf(DeletionNotFound.class, result);
    }

    @Test
    void deleteStage_ShouldReturnSuccess_WhenRetireWorks() {
        StageEntity entity = new StageEntity().setId(2L);
        when(repository.findByIdAndApplicationId(2L, 3L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        DatabaseDeletionResult result = service.deleteStage(new StageDatabaseService.StageReference(3L, 2L));

        assertInstanceOf(DeletionSuccess.class, result);
        verify(repository).save(entity);
        assertTrue(entity.isRetired());
    }

    @Test
    void deleteStage_ShouldReturnFailure_WhenRetireThrows() {
        StageEntity entity = new StageEntity().setId(2L);
        when(repository.findByIdAndApplicationId(2L, 3L)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException("boom")).when(repository)
                                             .save(entity);

        DatabaseDeletionResult result = service.deleteStage(new StageDatabaseService.StageReference(3L, 2L));

        assertInstanceOf(DeletionFailure.class, result);
    }

    @Test
    void hasStages_ShouldDelegateToRepository() {
        when(repository.existsByApplicationIdAndRetiredFalse(3L)).thenReturn(true);

        assertTrue(service.hasStages(new StageDatabaseService.ApplicationReference(3L)));
    }

    @Test
    void findApplicationIdsWithStages_ShouldDelegateToRepository() {
        when(repository.findApplicationIdsWithActiveStages(List.of(3L, 4L))).thenReturn(Set.of(3L));

        Set<Long> result = service.findApplicationIdsWithStages(
                new StageDatabaseService.ApplicationsWithStages(List.of(3L, 4L)));

        assertEquals(Set.of(3L), result);
        verify(repository).findApplicationIdsWithActiveStages(List.of(3L, 4L));
    }

    @Test
    void findApplicationIdsWithStages_ShouldNotQueryRepository_WhenInputIsEmpty() {
        Set<Long> result = service.findApplicationIdsWithStages(
                new StageDatabaseService.ApplicationsWithStages(List.of()));

        assertTrue(result.isEmpty());
        verifyNoInteractions(repository);
    }

}
