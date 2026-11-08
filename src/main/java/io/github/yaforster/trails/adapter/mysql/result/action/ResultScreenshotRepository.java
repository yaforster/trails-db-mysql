package io.github.yaforster.trails.adapter.mysql.result.action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ResultScreenshotRepository extends JpaRepository<ResultScreenshotEntity, Long> {
    @Query("select screenshot.resultId from ResultScreenshotEntity screenshot where screenshot.resultId in "
            + "(select result.id from ResultEntity result where result.testPathResultId = :pathResultId)")
    Set<Long> findActionResultIdsInPath(@Param("pathResultId") Long pathResultId);

    Optional<ResultScreenshotEntity> findByResultId(Long resultId);

    List<ResultScreenshotEntity> findByResultIdIn(Collection<Long> resultIds);

}
