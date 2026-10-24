package io.github.yaforster.trails.adapter.mysql.result.testset;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestSetResultRepository extends JpaRepository<TestSetResultEntity, Long> {

    Page<TestSetResultEntity> findByTestRunIdOrderByIdAsc(Long testRunId, Pageable pageable);

}
