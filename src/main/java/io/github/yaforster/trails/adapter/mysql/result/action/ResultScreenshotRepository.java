package io.github.yaforster.trails.adapter.mysql.result.action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Collection;
import java.util.List;

@Repository
public interface ResultScreenshotRepository extends JpaRepository<ResultScreenshotEntity, Long> {

    Optional<ResultScreenshotEntity> findByResultId(Long resultId);

    List<ResultScreenshotEntity> findByResultIdIn(Collection<Long> resultIds);

}
