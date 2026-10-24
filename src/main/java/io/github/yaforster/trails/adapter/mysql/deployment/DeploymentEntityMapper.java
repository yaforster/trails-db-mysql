package io.github.yaforster.trails.adapter.mysql.deployment;

import io.github.yaforster.trails.core.definition.DeploymentDefinition;
import io.github.yaforster.trails.core.persisted.PersistedDeployment;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class DeploymentEntityMapper {

    public DeploymentEntity toEntity(Long applicationId, Long stageId, DeploymentDefinition definition) {
        Timestamp deployedAt = Timestamp.from(definition.deployedAt());
        return new DeploymentEntity().setApplicationId(applicationId)
                                     .setStageId(stageId)
                                     .setVersion(definition.version())
                                     .setDeployedAt(deployedAt);
    }

    public PersistedDeployment toPersisted(DeploymentEntity entity) {
        Instant deployedAt = entity.getDeployedAt()
                                   .toInstant();
        return new PersistedDeployment(entity.getId(), entity.getApplicationId(), entity.getStageId(),
                entity.getVersion(), deployedAt);
    }

}
