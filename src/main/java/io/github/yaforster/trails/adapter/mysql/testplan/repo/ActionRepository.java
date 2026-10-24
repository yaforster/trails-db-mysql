package io.github.yaforster.trails.adapter.mysql.testplan.repo;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface ActionRepository extends JpaRepository<ActionEntity, Long> {

    boolean existsByTestPlanId(Long testPlanId);

    @Query("""
            select distinct action.testPlanId
            from ActionEntity action, TestPlanEntity testPlan
            where action.testPlanId in :testPlanIds
            	and testPlan.id = action.testPlanId
            	and testPlan.applicationId = :applicationId
            	and testPlan.stageId = :stageId
            """)
    Set<Long> findTestPlanIdsWithActions(@Param("applicationId") Long applicationId, @Param("stageId") Long stageId,
            @Param("testPlanIds") Collection<Long> testPlanIds);

    Page<ActionEntity> findByTestPlanIdOrderByIdAsc(Long testPlanId, Pageable pageable);

}
