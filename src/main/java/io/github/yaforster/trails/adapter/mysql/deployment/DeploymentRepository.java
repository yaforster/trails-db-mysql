package io.github.yaforster.trails.adapter.mysql.deployment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeploymentRepository extends JpaRepository<DeploymentEntity, Long> {

    Optional<DeploymentEntity> findByApplicationIdAndStageIdAndId(Long applicationId, Long stageId, Long id);

    Page<DeploymentEntity> findByApplicationIdAndStageIdOrderByDeployedAtAscIdAsc(Long applicationId, Long stageId,
            Pageable pageable);

}
