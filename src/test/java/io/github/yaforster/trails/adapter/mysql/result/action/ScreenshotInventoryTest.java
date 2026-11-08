package io.github.yaforster.trails.adapter.mysql.result.action;

import jakarta.persistence.EntityManager;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.jdbc.core.JdbcTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:screenshot-inventory;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
        "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=none",
        "spring.jpa.properties.hibernate.generate_statistics=true"})
@Transactional
class ScreenshotInventoryTest {
    @Autowired
    private JdbcTemplate database;
    @Autowired
    private ResultScreenshotRepository screenshots;
    @Autowired
    private EntityManager entities;

    @Test
    void shouldReturnOnlyScreenshotIdsInRequestedPath_withoutLoadingEntities() {
        storeResult(101L, 7L);
        storeResult(102L, 8L);
        storeResult(103L, 7L);
        screenshots.save(screenshot(101L));
        screenshots.save(screenshot(102L));
        entities.flush();
        entities.clear();
        Statistics statistics = entities.getEntityManagerFactory()
                                        .unwrap(SessionFactory.class)
                                        .getStatistics();
        statistics.clear();

        assertThat(screenshots.findActionResultIdsInPath(7L)).containsExactly(101L);
        assertThat(statistics.getEntityLoadCount()).isZero();
    }

    private void storeResult(long resultId, long pathId) {
        // Seed the query independently of Hibernate's dialect-specific result ID generation.
        database.update("insert into result_entity (id, dtype, success, test_path_result_id) values (?, '2', true, ?)",
                resultId, pathId);
    }

    private ResultScreenshotEntity screenshot(Long resultId) {
        return new ResultScreenshotEntity().setResultId(resultId)
                                           .setFileName("result.png")
                                           .setContentType("image/png")
                                           .setContent(new byte[]{1, 2, 3});
    }
}
