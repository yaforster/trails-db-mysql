package io.github.yaforster.trails.adapter.mysql.stage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:bulk-child-presence;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
        "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=none"})
@Transactional
class StageRepositoryChildPresenceTest {

    @Autowired
    private StageRepository stageRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldExcludeRetiredAndUnrequestedApplications() {
        storeStage(1L, 1L, false);
        storeStage(2L, 2L, true);
        storeStage(3L, 3L, false);

        Set<Long> applicationIds = stageRepository.findApplicationIdsWithActiveStages(List.of(1L, 2L));

        assertThat(applicationIds).containsExactly(1L);
    }

    private void storeStage(Long id, Long applicationId, boolean retired) {
        jdbcTemplate.update("insert into stage_entity (id, application_id, label, url, retired) values (?, ?, ?, ?, ?)",
                id, applicationId, "stage", "https://example.org", retired);
    }

}
