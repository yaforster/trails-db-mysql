package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
class ElementRepositoryChildPresenceTest {

    @Autowired
    private ElementRepository elementRepository;

    @Test
    void shouldExcludeRetiredAndOtherApplicationElements() {
        elementRepository.save(new ElementEntity().setApplicationId(1L)
                                                  .setStageId(10L)
                                                  .setRetired(false));
        elementRepository.save(new ElementEntity().setApplicationId(1L)
                                                  .setStageId(11L)
                                                  .setRetired(true));
        elementRepository.save(new ElementEntity().setApplicationId(2L)
                                                  .setStageId(10L)
                                                  .setRetired(false));

        Set<Long> stageIds = elementRepository.findStageIdsWithActiveElements(1L, List.of(10L, 11L));

        assertThat(stageIds).containsExactly(10L);
    }

}
