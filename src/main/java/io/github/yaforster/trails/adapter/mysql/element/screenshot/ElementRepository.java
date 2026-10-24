package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.core.data.ElementType;
import io.github.yaforster.trails.core.test.action.element.LocatorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ElementRepository extends JpaRepository<ElementEntity, Long> {

    Optional<ElementEntity> findByIdAndApplicationIdAndStageId(Long id, Long applicationId, Long stageId);

    Optional<ElementEntity> findByIdAndApplicationIdAndStageIdAndRetiredFalse(Long id, Long applicationId,
            Long stageId);

    Optional<ElementEntity> findByApplicationIdAndStageIdAndLocatorAndLocatorTypeAndTypeAndRetiredFalse(
            Long applicationId, Long stageId, String locator, LocatorType locatorType, ElementType type);

    Optional<ElementEntity> findByApplicationIdAndStageIdAndLocatorAndRetiredFalse(Long applicationId, Long stageId,
            String locator);

    Page<ElementEntity> findAllByApplicationIdAndStageId(Long applicationId, Long stageId, Pageable pageable);

    Page<ElementEntity> findAllByApplicationIdAndStageIdAndRetiredFalse(Long applicationId, Long stageId,
            Pageable pageable);

    boolean existsByApplicationIdAndStageIdAndRetiredFalse(Long applicationId, Long stageId);

    @Query("""
            select distinct element.stageId
            from ElementEntity element
            where element.applicationId = :applicationId
            	and element.stageId in :stageIds
            	and element.retired = false
            """)
    Set<Long> findStageIdsWithActiveElements(@Param("applicationId") Long applicationId,
            @Param("stageIds") Collection<Long> stageIds);

    boolean existsByApplicationIdAndStageIdAndLabel(Long applicationId, Long stageId, String label);

    boolean existsByApplicationIdAndStageIdAndLocator(Long applicationId, Long stageId, String locator);

}
