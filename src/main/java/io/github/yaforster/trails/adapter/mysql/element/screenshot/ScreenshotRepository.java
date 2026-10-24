package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenshotRepository extends JpaRepository<ElementScreenshotEntity, Long> {

    Optional<ElementScreenshotEntity> findByElementId(Long elementId);

    @Query("""
                select s.elementId, s.id
                from ElementScreenshotEntity s
                where s.elementId in :elementIds
            """)
    List<Object[]> findIdsByElementIds(@Param("elementIds") List<Long> elementIds);

}
