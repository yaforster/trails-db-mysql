package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ExplicitWaitDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.repo.ActionRepository;
import io.github.yaforster.trails.adapter.mysql.testplan.repo.TestPlanRepository;
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
class ActionRepositoryChildPresenceTest {

    @Autowired
    private TestPlanRepository testPlanRepository;
    @Autowired
    private ActionRepository actionRepository;

    @Test
    void shouldExcludeActionsFromOtherApplicationAndStage() {
        TestPlanEntity matchingPlan = testPlanRepository.save(testPlan(1L, 2L));
        TestPlanEntity otherApplicationPlan = testPlanRepository.save(testPlan(3L, 2L));
        TestPlanEntity otherStagePlan = testPlanRepository.save(testPlan(1L, 4L));
        actionRepository.save(actionFor(matchingPlan.getId()));
        actionRepository.save(actionFor(otherApplicationPlan.getId()));
        actionRepository.save(actionFor(otherStagePlan.getId()));

        Set<Long> testPlanIds = actionRepository.findTestPlanIdsWithActions(1L, 2L,
                List.of(matchingPlan.getId(), otherApplicationPlan.getId(), otherStagePlan.getId()));

        assertThat(testPlanIds).containsExactly(matchingPlan.getId());
    }

    private TestPlanEntity testPlan(Long applicationId, Long stageId) {
        return new TestPlanEntity().setApplicationId(applicationId)
                                   .setStageId(stageId)
                                   .setLabel("plan")
                                   .setRetired(false)
                                   .setTestSteps(List.of())
                                   .setGroups(List.of());
    }

    private ActionEntity actionFor(Long testPlanId) {
        return new ActionEntity().setTestPlanId(testPlanId)
                                 .setLabel("action")
                                 .setDetails(new ExplicitWaitDetailsEntity(1L))
                                 .setPosition(new PositionEntity());
    }

}
