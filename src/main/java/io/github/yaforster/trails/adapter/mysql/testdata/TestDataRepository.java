package io.github.yaforster.trails.adapter.mysql.testdata;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TestDataRepository extends JpaRepository<TestDataSetEntity, Long> {

    Optional<TestDataSetEntity> findByIdAndRetiredFalse(Long id);

    Page<TestDataSetEntity> findAllByRetiredFalse(Pageable pageable);

}
