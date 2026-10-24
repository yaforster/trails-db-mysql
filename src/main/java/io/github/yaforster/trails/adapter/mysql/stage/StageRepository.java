package io.github.yaforster.trails.adapter.mysql.stage;

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
public interface StageRepository extends JpaRepository<StageEntity, Long> {

    Page<StageEntity> findAllByApplicationId(Long applicationId, Pageable pageable);

    Page<StageEntity> findAllByApplicationIdAndRetiredFalse(Long applicationId, Pageable pageable);

    Optional<StageEntity> findByIdAndApplicationId(Long id, Long applicationId);

    Optional<StageEntity> findByIdAndApplicationIdAndRetiredFalse(Long id, Long applicationId);

    boolean existsByApplicationIdAndRetiredFalse(Long applicationId);

    @Query("""
            select distinct stage.applicationId
            from StageEntity stage
            where stage.applicationId in :applicationIds
            	and stage.retired = false
            """)
    Set<Long> findApplicationIdsWithActiveStages(@Param("applicationIds") Collection<Long> applicationIds);

    boolean existsByApplicationIdAndLabel(Long applicationId, String label);

    boolean existsByApplicationIdAndUrl(Long applicationId, String url);

}
