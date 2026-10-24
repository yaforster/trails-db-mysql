package io.github.yaforster.trails.adapter.mysql.result.run;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TestRunResultRepository extends JpaRepository<TestRunResultEntity, Long> {

    Optional<TestRunResultEntity> findByApplicationIdAndStageIdAndId(Long appId, Long stageId, Long id);

    Page<TestRunResultEntity> findByApplicationIdAndStageIdOrderByIdDesc(Long appId, Long stageId, Pageable pageable);

    Page<TestRunResultEntity> findByApplicationIdAndStageIdAndTestPlanIdOrderByTimestampAscIdAsc(Long appId,
            Long stageId, Long testPlanId, Pageable pageable);

    long countByApplicationIdAndStageIdAndTestPlanId(Long appId, Long stageId, Long testPlanId);

    long countByApplicationIdAndStageIdAndTestPlanIdAndStatus(Long appId, Long stageId, Long testPlanId,
            TestRunStatus status);

}
