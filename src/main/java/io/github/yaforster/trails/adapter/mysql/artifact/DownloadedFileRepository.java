package io.github.yaforster.trails.adapter.mysql.artifact;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DownloadedFileRepository extends JpaRepository<DownloadedFileEntity, Long> {

    Page<DownloadedFileEntity> findByTestPathResultIdOrderByIdAsc(Long testPathResultId, Pageable pageable);

    Optional<DownloadedFileEntity> findByIdAndTestPathResultId(Long id, Long testPathResultId);

}
