package io.github.yaforster.trails.adapter.mysql.deployment;

import io.github.yaforster.trails.adapter.mysql.SpringPageMapper;
import io.github.yaforster.trails.app.services.DeploymentDatabaseService;
import io.github.yaforster.trails.app.services.StageDatabaseService;
import io.github.yaforster.trails.core.PagedResult;
import io.github.yaforster.trails.core.persisted.PersistedDeployment;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class DeploymentDatabaseServiceImpl implements DeploymentDatabaseService {

    private final DeploymentRepository deploymentRepository;
    private final StageDatabaseService stageDatabaseService;
    private final DeploymentEntityMapper mapper;

    @Override
    public Optional<PersistedDeployment> storeDeployment(StageDeployment stageDeployment) {
        StageDatabaseService.StageDetails stage = new StageDatabaseService.StageDetails(stageDeployment.applicationId(),
                stageDeployment.stageId(), true);
        if (stageDatabaseService.getStage(stage)
                                .isEmpty()) {
            return Optional.empty();
        }

        DeploymentEntity entity = mapper.toEntity(stageDeployment.applicationId(), stageDeployment.stageId(),
                stageDeployment.definition());
        DeploymentEntity stored = deploymentRepository.save(entity);
        PersistedDeployment deployment = mapper.toPersisted(stored);
        return Optional.of(deployment);
    }

    @Override
    public Optional<PersistedDeployment> getDeployment(DeploymentDetails deploymentDetails) {
        return deploymentRepository.findByApplicationIdAndStageIdAndId(deploymentDetails.applicationId(),
                deploymentDetails.stageId(), deploymentDetails.deploymentId())
                                   .map(mapper::toPersisted);
    }

    @Override
    public Optional<PagedResult<PersistedDeployment>> listDeployments(StageDeploymentPage deploymentPage) {
        StageDatabaseService.StageDetails stage = new StageDatabaseService.StageDetails(deploymentPage.applicationId(),
                deploymentPage.stageId(), true);
        if (stageDatabaseService.getStage(stage)
                                .isEmpty()) {
            return Optional.empty();
        }

        Pageable pageable = PageRequest.of(deploymentPage.page(), deploymentPage.size());
        Page<PersistedDeployment> deployments = deploymentRepository.findByApplicationIdAndStageIdOrderByDeployedAtAscIdAsc(
                deploymentPage.applicationId(), deploymentPage.stageId(), pageable)
                                                                    .map(mapper::toPersisted);
        PagedResult<PersistedDeployment> result = SpringPageMapper.toPagedResult(deployments);
        return Optional.of(result);
    }

}
