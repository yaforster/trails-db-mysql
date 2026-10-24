package io.github.yaforster.trails.adapter.mysql.result.testpath;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TestPathResultRepository extends JpaRepository<TestPathResultEntity, Long> {

    Page<TestPathResultEntity> findByTestSetResultIdOrderByIdAsc(Long testSetResultId, Pageable pageable);

    Page<TestPathResultEntity> findByTestSetResultIdInOrderByTestSetResultIdAscIdAsc(Collection<Long> testSetResultIds,
            Pageable pageable);

}
