package io.github.yaforster.trails.adapter.mysql.stage;

import io.github.yaforster.trails.adapter.mysql.SpringPageMapper;
import io.github.yaforster.trails.app.services.StageDatabaseService;
import io.github.yaforster.trails.core.PagedResult;
import io.github.yaforster.trails.core.deletion.DatabaseDeletionResult;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import io.github.yaforster.trails.core.persisted.PersistedStage;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class StageDatabaseServiceImpl implements StageDatabaseService {

    private final StageRepository repository;
    private final StageEntityMapper stageEntityMapper;

    @Override
    public PersistedStage storeStage(StageCreation stageCreation) {
        StageEntity entity = stageEntityMapper.toEntity(stageCreation.applicationId(), stageCreation.stageDefinition());
        StageEntity storedStage = repository.save(entity);
        return stageEntityMapper.toPersisted(storedStage);
    }

    @Override
    public DatabaseDeletionResult deleteStage(StageReference stageReference) {
        Optional<StageEntity> entity = repository.findByIdAndApplicationId(stageReference.stageId(),
                stageReference.applicationId());
        if (entity.isEmpty()) {
            return new DeletionNotFound(stageReference.stageId());
        }
        try {
            StageEntity stage = entity.get();
            stage.setRetired(true);
            repository.save(stage);
            return new DeletionSuccess(stageReference.stageId());
        } catch (Exception exception) {
            return new DeletionFailure(stageReference.stageId(), exception);
        }
    }

    @Override
    public DatabaseDeletionResult restoreStage(StageReference stageReference) {
        Optional<StageEntity> entity = repository.findByIdAndApplicationId(stageReference.stageId(),
                stageReference.applicationId());
        if (entity.isEmpty()) {
            return new DeletionNotFound(stageReference.stageId());
        }
        try {
            StageEntity stage = entity.get();
            stage.setRetired(false);
            repository.save(stage);
            return new DeletionSuccess(stageReference.stageId());
        } catch (Exception exception) {
            return new DeletionFailure(stageReference.stageId(), exception);
        }
    }

    @Override
    public PagedResult<PersistedStage> getStages(StagePage stagePage) {
        Pageable pageable = PageRequest.of(stagePage.page(), stagePage.size());
        Page<StageEntity> stages;
        if (stagePage.includeRetired()) {
            stages = repository.findAllByApplicationId(stagePage.applicationId(), pageable);
        } else {
            stages = repository.findAllByApplicationIdAndRetiredFalse(stagePage.applicationId(), pageable);
        }
        Page<PersistedStage> persistedStages = stages.map(stageEntityMapper::toPersisted);
        return SpringPageMapper.toPagedResult(persistedStages);
    }

    @Override
    public Optional<PersistedStage> getStage(StageDetails stageDetails) {
        Optional<StageEntity> stage;
        if (stageDetails.includeRetired()) {
            stage = repository.findByIdAndApplicationId(stageDetails.stageId(), stageDetails.applicationId());
        } else {
            stage = repository.findByIdAndApplicationIdAndRetiredFalse(stageDetails.stageId(),
                    stageDetails.applicationId());
        }
        return stage.map(stageEntityMapper::toPersisted);
    }

    @Override
    public boolean hasStages(ApplicationReference applicationReference) {
        return repository.existsByApplicationIdAndRetiredFalse(applicationReference.applicationId());
    }

    @Override
    public Set<Long> findApplicationIdsWithStages(ApplicationsWithStages applications) {
        if (applications.applicationIds()
                        .isEmpty()) {
            return Set.of();
        }
        return repository.findApplicationIdsWithActiveStages(applications.applicationIds());
    }

    @Override
    public boolean existsByLabel(StageLabel stageLabel) {
        return repository.existsByApplicationIdAndLabel(stageLabel.applicationId(), stageLabel.label());
    }

    @Override
    public boolean existsByUrl(StageUrl stageUrl) {
        return repository.existsByApplicationIdAndUrl(stageUrl.applicationId(), stageUrl.url());
    }

}
