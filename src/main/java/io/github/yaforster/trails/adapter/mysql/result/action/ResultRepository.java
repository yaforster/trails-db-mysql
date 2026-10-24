package io.github.yaforster.trails.adapter.mysql.result.action;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Collection;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<ResultEntity, Long> {

    List<ResultEntity> findByTestPathResultIdOrderByExecutionOrderAscIdAsc(Long pathId);

    List<ResultEntity> findByTestPathResultIdInOrderByTestPathResultIdAscExecutionOrderAscIdAsc(
            Collection<Long> pathIds);

    Optional<ResultEntity> findByIdAndTestPathResultId(Long id, Long testPathResultId);

}
