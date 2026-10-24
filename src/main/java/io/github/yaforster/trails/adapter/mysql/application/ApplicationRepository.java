package io.github.yaforster.trails.adapter.mysql.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, Long> {

    Optional<ApplicationEntity> findByIdAndRetiredFalse(Long id);

    Page<ApplicationEntity> findAllByRetiredFalse(Pageable pageable);

    boolean existsByLabel(String label);

}
