package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.*;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ExplicitWaitDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.repo.TestPlanRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:plan-copy;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
        "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=none"})
@Transactional
class TestPlanCopyPersistenceTest {

    private final TestPlanRepository repository;
    private final TestPlanPromotionService promotionService;
    private final EntityManager entityManager;

    @Autowired
    TestPlanCopyPersistenceTest(TestPlanRepository repository, TestPlanPromotionService promotionService,
            EntityManager entityManager) {
        this.repository = repository;
        this.promotionService = promotionService;
        this.entityManager = entityManager;
    }

    @Test
    void shouldPersistCopiedGraph_whenGeneratedIdsReplaceSourceLinks() {
        ActionEntity first = action("first");
        ActionEntity second = action("second");
        TestPlanEntity source = new TestPlanEntity().setApplicationId(1L)
                                                    .setStageId(2L)
                                                    .setLabel("source")
                                                    .setTestSteps(new ArrayList<>(List.of(first, second)))
                                                    .setGroups(new ArrayList<>());
        repository.save(source);
        first.setTestPlanId(source.getId())
             .setNextActions(new ArrayList<>(List.of(second.getId(), first.getId())));
        second.setTestPlanId(source.getId())
              .setNextActions(new ArrayList<>(List.of(first.getId())));
        entityManager.flush();
        TestPlanCopy copy = promotionService.copyToStage(source, 3L)
                                            .orElseThrow();
        ActionEntity firstCopy = copy.entity()
                                     .getTestSteps()
                                     .getFirst();
        ActionEntity secondCopy = copy.entity()
                                      .getTestSteps()
                                      .getLast();
        java.util.Collections.reverse(copy.entity()
                                          .getTestSteps());

        repository.save(copy.entity());
        copy.resolveNextActions();
        entityManager.flush();
        entityManager.clear();

        ActionEntity reloadedFirst = entityManager.find(ActionEntity.class, firstCopy.getId());
        ActionEntity reloadedSecond = entityManager.find(ActionEntity.class, secondCopy.getId());
        ActionEntity unchangedSource = entityManager.find(ActionEntity.class, first.getId());
        assertThat(List.of(reloadedFirst.getTestPlanId(), reloadedSecond.getTestPlanId(),
                List.copyOf(reloadedFirst.getNextActions()), List.copyOf(reloadedSecond.getNextActions()),
                List.copyOf(unchangedSource.getNextActions()))).containsExactly(copy.entity()
                                                                                    .getId(),
                        copy.entity()
                            .getId(),
                        List.of(secondCopy.getId(), firstCopy.getId()), List.of(firstCopy.getId()),
                        List.of(second.getId(), first.getId()));
    }

    private ActionEntity action(String label) {
        return new ActionEntity().setLabel(label)
                                 .setDetails(new ExplicitWaitDetailsEntity(1L))
                                 .setPosition(new PositionEntity())
                                 .setNextActions(new ArrayList<>());
    }
}
