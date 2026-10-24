package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.FixedValueEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.*;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ClickActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.TypingDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.TestPlanEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.repo.ActionRepository;
import io.github.yaforster.trails.adapter.mysql.testplan.repo.TestPlanRepository;
import io.github.yaforster.trails.app.services.TestDataValueResolutionService;
import io.github.yaforster.trails.app.services.TestPlanDatabaseService;
import io.github.yaforster.trails.core.data.ElementType;
import io.github.yaforster.trails.core.data.TestPlan;
import io.github.yaforster.trails.core.definition.TestPlanDefinition;
import io.github.yaforster.trails.core.deletion.DatabaseDeletionResult;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import io.github.yaforster.trails.core.persisted.PersistedTestPlan;
import io.github.yaforster.trails.core.test.action.element.LocatorType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TestPlanDatabaseServiceImplTest {

    private final TestPlanRepository testPlanRepository = Mockito.mock(TestPlanRepository.class);
    private final ActionRepository actionRepository = Mockito.mock(ActionRepository.class);
    private final TestPlanEntityMapper testPlanEntityMapper = Mockito.mock(TestPlanEntityMapper.class);
    private final TestDataValueResolutionService instructionResolutionService = Mockito.mock(
            TestDataValueResolutionService.class);
    private final TestPlanActionReferenceResolver actionReferenceResolver = new TestPlanActionReferenceResolver();
    private final TestPlanPromotionService promotionService = Mockito.mock(TestPlanPromotionService.class);
    private final TestPlanDatabaseServiceImpl service = new TestPlanDatabaseServiceImpl(testPlanRepository,
            actionRepository, testPlanEntityMapper, instructionResolutionService, actionReferenceResolver,
            promotionService);

    @Test
    void storeTestPlan_ShouldResolveNextActionsAndReturnPersisted() {
        TestPlanDefinition definition = Mockito.mock(TestPlanDefinition.class);
        ActionEntity action1 = new ActionEntity().setId(100L)
                                                 .setActionId(10L)
                                                 .setNextActions(List.of(20L));
        ActionEntity action2 = new ActionEntity().setId(200L)
                                                 .setActionId(20L)
                                                 .setNextActions(List.of());
        TestPlanEntity entity = new TestPlanEntity().setTestSteps(List.of(action1, action2));
        PersistedTestPlan persisted = Mockito.mock(PersistedTestPlan.class);
        when(testPlanEntityMapper.toEntity(definition)).thenReturn(entity);
        when(testPlanRepository.save(entity)).thenReturn(entity);
        when(testPlanEntityMapper.mapEntityToPersistedTestPlan(entity)).thenReturn(persisted);

        PersistedTestPlan result = service.storeTestPlan(definition);

        assertEquals(persisted, result);
        assertEquals(List.of(200L), action1.getNextActions());
        verify(actionRepository).saveAll(entity.getTestSteps());
    }

    @Test
    void storeTestPlan_ShouldAssignStoredTestPlanIdToAction() {
        TestPlanDefinition definition = Mockito.mock(TestPlanDefinition.class);
        ActionEntity action = new ActionEntity().setId(100L)
                                                .setActionId(10L)
                                                .setNextActions(List.of());
        TestPlanEntity entity = new TestPlanEntity().setId(300L)
                                                    .setTestSteps(List.of(action));
        PersistedTestPlan persisted = Mockito.mock(PersistedTestPlan.class);
        when(testPlanEntityMapper.toEntity(definition)).thenReturn(entity);
        when(testPlanRepository.save(entity)).thenReturn(entity);
        when(testPlanEntityMapper.mapEntityToPersistedTestPlan(entity)).thenReturn(persisted);

        service.storeTestPlan(definition);

        assertEquals(300L, action.getTestPlanId());
    }

    @Test
    void storeTestPlan_ShouldThrow_WhenUnknownNextActionReference() {
        TestPlanDefinition definition = Mockito.mock(TestPlanDefinition.class);
        ActionEntity action = new ActionEntity().setId(100L)
                                                .setActionId(10L)
                                                .setNextActions(List.of(999L));
        TestPlanEntity entity = new TestPlanEntity().setTestSteps(List.of(action));
        when(testPlanEntityMapper.toEntity(definition)).thenReturn(entity);
        when(testPlanRepository.save(entity)).thenReturn(entity);

        assertThrows(IllegalStateException.class, () -> service.storeTestPlan(definition));
        verify(actionRepository, never()).saveAll(any());
    }

    @Test
    void storeTestPlan_ShouldThrow_WhenActionReferenceIdMissing() {
        TestPlanDefinition definition = Mockito.mock(TestPlanDefinition.class);
        ActionEntity action = new ActionEntity().setId(100L)
                                                .setActionId(null)
                                                .setNextActions(List.of());
        TestPlanEntity entity = new TestPlanEntity().setTestSteps(List.of(action));
        when(testPlanEntityMapper.toEntity(definition)).thenReturn(entity);
        when(testPlanRepository.save(entity)).thenReturn(entity);

        assertThrows(IllegalStateException.class, () -> service.storeTestPlan(definition));
        verify(actionRepository, never()).saveAll(any());
    }

    @Test
    void storeTestPlan_ShouldReplaceNullNextActionsWithEmptyList() {
        TestPlanDefinition definition = Mockito.mock(TestPlanDefinition.class);
        ActionEntity action = new ActionEntity().setId(100L)
                                                .setActionId(10L)
                                                .setNextActions(null);
        TestPlanEntity entity = new TestPlanEntity().setTestSteps(List.of(action));
        PersistedTestPlan persisted = Mockito.mock(PersistedTestPlan.class);
        when(testPlanEntityMapper.toEntity(definition)).thenReturn(entity);
        when(testPlanRepository.save(entity)).thenReturn(entity);
        when(testPlanEntityMapper.mapEntityToPersistedTestPlan(entity)).thenReturn(persisted);

        PersistedTestPlan result = service.storeTestPlan(definition);

        assertEquals(persisted, result);
        assertEquals(List.of(), action.getNextActions());
        verify(actionRepository).saveAll(entity.getTestSteps());
    }

    @Test
    void deleteTestPlan_ShouldReturnNotFound_WhenEntityMissing() {
        when(testPlanRepository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.empty());

        DatabaseDeletionResult result = service.deleteTestPlan(
                new TestPlanDatabaseService.TestPlanReference(1L, 2L, 3L));

        assertInstanceOf(DeletionNotFound.class, result);
    }

    @Test
    void deleteTestPlan_ShouldReturnSuccess_WhenRetireWorks() {
        TestPlanEntity entity = new TestPlanEntity().setId(3L);
        when(testPlanRepository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.of(entity));
        when(testPlanRepository.save(entity)).thenReturn(entity);

        DatabaseDeletionResult result = service.deleteTestPlan(
                new TestPlanDatabaseService.TestPlanReference(1L, 2L, 3L));

        assertInstanceOf(DeletionSuccess.class, result);
        assertTrue(entity.isRetired());
    }

    @Test
    void deleteTestPlan_ShouldReturnFailure_WhenRetireThrows() {
        TestPlanEntity entity = new TestPlanEntity().setId(3L);
        when(testPlanRepository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException("boom")).when(testPlanRepository)
                                             .save(entity);

        DatabaseDeletionResult result = service.deleteTestPlan(
                new TestPlanDatabaseService.TestPlanReference(1L, 2L, 3L));

        assertInstanceOf(DeletionFailure.class, result);
    }

    @Test
    void restoreTestPlan_ShouldReturnNotFound_WhenEntityMissing() {
        when(testPlanRepository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.empty());

        DatabaseDeletionResult result = service.restoreTestPlan(
                new TestPlanDatabaseService.TestPlanReference(1L, 2L, 3L));

        assertInstanceOf(DeletionNotFound.class, result);
    }

    @Test
    void restoreTestPlan_ShouldReturnSuccess_WhenRestoreWorks() {
        TestPlanEntity entity = new TestPlanEntity().setId(3L)
                                                    .setRetired(true);
        when(testPlanRepository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.of(entity));
        when(testPlanRepository.save(entity)).thenReturn(entity);

        DatabaseDeletionResult result = service.restoreTestPlan(
                new TestPlanDatabaseService.TestPlanReference(1L, 2L, 3L));

        assertInstanceOf(DeletionSuccess.class, result);
        assertFalse(entity.isRetired());
    }

    @Test
    void restoreTestPlan_ShouldReturnFailure_WhenRestoreThrows() {
        TestPlanEntity entity = new TestPlanEntity().setId(3L)
                                                    .setRetired(true);
        when(testPlanRepository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException("boom")).when(testPlanRepository)
                                             .save(entity);

        DatabaseDeletionResult result = service.restoreTestPlan(
                new TestPlanDatabaseService.TestPlanReference(1L, 2L, 3L));

        assertInstanceOf(DeletionFailure.class, result);
    }

    @Test
    void getTestPlans_ShouldMapPageUsingMapper() {
        TestPlanEntity entity = new TestPlanEntity().setId(1L);
        PersistedTestPlan persisted = Mockito.mock(PersistedTestPlan.class);
        when(testPlanRepository.findByApplicationIdAndStageIdAndRetiredFalse(1L, 2L, PageRequest.of(0, 10))).thenReturn(
                new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1));
        when(testPlanEntityMapper.mapEntityToPersistedTestPlan(entity)).thenReturn(persisted);

        PagedResult<PersistedTestPlan> result = service.getTestPlans(
                new TestPlanDatabaseService.TestPlanPage(1L, 2L, 0, 10, false));

        assertEquals(1, result.getContent()
                              .size());
        assertEquals(persisted, result.getContent()
                                      .getFirst());
    }

    @Test
    void getTestPlans_ShouldIncludeRetired_WhenRequested() {
        TestPlanEntity entity = new TestPlanEntity().setId(1L);
        PersistedTestPlan persisted = Mockito.mock(PersistedTestPlan.class);
        when(testPlanRepository.findByApplicationIdAndStageId(1L, 2L, PageRequest.of(0, 10))).thenReturn(
                new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1));
        when(testPlanEntityMapper.mapEntityToPersistedTestPlan(entity)).thenReturn(persisted);

        PagedResult<PersistedTestPlan> result = service.getTestPlans(
                new TestPlanDatabaseService.TestPlanPage(1L, 2L, 0, 10, true));

        assertEquals(List.of(persisted), result.getContent());
        verify(testPlanRepository, never()).findByApplicationIdAndStageIdAndRetiredFalse(anyLong(), anyLong(), any());
    }

    @Test
    void promoteTestPlan_ShouldSavePromotedCopyAndResolveNextActions() {
        ActionEntity firstAction = new ActionEntity().setId(100L)
                                                     .setActionId(10L)
                                                     .setLabel("type")
                                                     .setNextActions(List.of(200L));
        ActionEntity secondAction = new ActionEntity().setId(200L)
                                                      .setActionId(20L)
                                                      .setLabel("click")
                                                      .setNextActions(List.of());
        TestPlanEntity source = new TestPlanEntity().setId(9L)
                                                    .setApplicationId(1L)
                                                    .setStageId(2L)
                                                    .setLabel("plan")
                                                    .setTestSteps(List.of(firstAction, secondAction));
        ActionEntity copiedFirstAction = new ActionEntity().setNextActions(List.of(200L));
        ActionEntity copiedSecondAction = new ActionEntity().setNextActions(List.of());
        TestPlanEntity copy = new TestPlanEntity().setApplicationId(1L)
                                                  .setStageId(3L)
                                                  .setLabel("plan")
                                                  .setTestSteps(List.of(copiedFirstAction, copiedSecondAction));
        PersistedTestPlan persisted = new PersistedTestPlan(12L, 1L, 3L, "plan");
        when(testPlanRepository.findByApplicationIdAndStageIdAndIdAndRetiredFalse(1L, 2L, 9L)).thenReturn(
                Optional.of(source));
        Map<Long, ActionEntity> copiedActions = Map.of(100L, copiedFirstAction, 200L, copiedSecondAction);
        TestPlanCopy testPlanCopy = new TestPlanCopy(copy, copiedActions);
        when(promotionService.copyToStage(source, 3L)).thenReturn(Optional.of(testPlanCopy));
        when(testPlanRepository.save(copy)).thenAnswer(invocation -> {
            copiedFirstAction.setId(101L);
            copiedSecondAction.setId(201L);
            return copy.setId(12L);
        });
        when(testPlanEntityMapper.mapEntityToPersistedTestPlan(Mockito.any(TestPlanEntity.class))).thenReturn(
                persisted);

        Optional<PersistedTestPlan> result = service.promoteTestPlan(
                new TestPlanDatabaseService.TestPlanPromotion(1L, 2L, 9L, 3L));

        assertTrue(result.isPresent());
        assertEquals(persisted, result.get());
        verify(promotionService).copyToStage(source, 3L);
        assertEquals(List.of(201L), copiedFirstAction.getNextActions());
        verify(testPlanRepository).save(copy);
        verify(actionRepository).saveAll(copy.getTestSteps());
    }

    @Test
    void promoteTestPlan_ShouldReturnEmpty_WhenPromotionCannotCopySourcePlan() {
        ActionEntity action = new ActionEntity().setId(100L)
                                                .setNextActions(List.of());
        TestPlanEntity source = new TestPlanEntity().setId(9L)
                                                    .setApplicationId(1L)
                                                    .setStageId(2L)
                                                    .setLabel("plan")
                                                    .setTestSteps(List.of(action));
        when(testPlanRepository.findByApplicationIdAndStageIdAndIdAndRetiredFalse(1L, 2L, 9L)).thenReturn(
                Optional.of(source));
        when(promotionService.copyToStage(source, 3L)).thenReturn(Optional.empty());

        Optional<PersistedTestPlan> result = service.promoteTestPlan(
                new TestPlanDatabaseService.TestPlanPromotion(1L, 2L, 9L, 3L));

        assertTrue(result.isEmpty());
        verify(promotionService).copyToStage(source, 3L);
        Mockito.verify(testPlanRepository, never())
               .save(any());
    }

    @Test
    void promoteTestPlan_ShouldReturnEmpty_WhenSourceTestPlanIsMissing() {
        when(testPlanRepository.findByApplicationIdAndStageIdAndIdAndRetiredFalse(1L, 2L, 9L)).thenReturn(
                Optional.empty());

        Optional<PersistedTestPlan> result = service.promoteTestPlan(
                new TestPlanDatabaseService.TestPlanPromotion(1L, 2L, 9L, 3L));

        assertTrue(result.isEmpty());
        verify(testPlanRepository, never()).save(any());
        verifyNoInteractions(actionRepository);
    }

    @Test
    void getTestPlan_ShouldReturnEmptyOptional_WhenEntityNotFound() {
        when(testPlanRepository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.empty());

        Optional<TestPlan> result = service.getTestPlan(new TestPlanDatabaseService.TestPlanReference(1L, 2L, 3L));

        assertTrue(result.isEmpty());
        verify(testPlanRepository).findByApplicationIdAndStageIdAndId(1L, 2L, 3L);
        verifyNoInteractions(testPlanEntityMapper);
    }

    @Test
    void getTestPlan_ShouldReturnMappedTestPlan_WhenApplicationStageAndPlanMatch() {
        TestPlanEntity entity = new TestPlanEntity().setId(3L);
        TestPlan testPlan = Mockito.mock(TestPlan.class);
        when(testPlanRepository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.of(entity));
        when(testPlanEntityMapper.fromEntity(entity)).thenReturn(testPlan);

        Optional<TestPlan> result = service.getTestPlan(new TestPlanDatabaseService.TestPlanReference(1L, 2L, 3L));

        assertEquals(Optional.of(testPlan), result);
    }

    @Test
    void getPersistedTestPlan_ShouldReturnMappedTestPlan_WhenIncludingRetired() {
        TestPlanEntity entity = new TestPlanEntity().setId(3L);
        PersistedTestPlan persisted = Mockito.mock(PersistedTestPlan.class);
        when(testPlanRepository.findByApplicationIdAndStageIdAndId(1L, 2L, 3L)).thenReturn(Optional.of(entity));
        when(testPlanEntityMapper.mapEntityToPersistedTestPlan(entity)).thenReturn(persisted);

        Optional<PersistedTestPlan> result = service.getPersistedTestPlan(
                new TestPlanDatabaseService.PersistedTestPlanDetails(1L, 2L, 3L, true));

        assertEquals(Optional.of(persisted), result);
    }

    @Test
    void getPersistedTestPlan_ShouldUseRetiredFalseQuery_WhenRetiredAreExcluded() {
        TestPlanEntity entity = new TestPlanEntity().setId(3L);
        PersistedTestPlan persisted = Mockito.mock(PersistedTestPlan.class);
        when(testPlanRepository.findByApplicationIdAndStageIdAndIdAndRetiredFalse(1L, 2L, 3L)).thenReturn(
                Optional.of(entity));
        when(testPlanEntityMapper.mapEntityToPersistedTestPlan(entity)).thenReturn(persisted);

        Optional<PersistedTestPlan> result = service.getPersistedTestPlan(
                new TestPlanDatabaseService.PersistedTestPlanDetails(1L, 2L, 3L, false));

        assertEquals(Optional.of(persisted), result);
        verify(testPlanRepository, never()).findByApplicationIdAndStageIdAndId(1L, 2L, 3L);
    }

    @Test
    void getPersistedTestPlan_ShouldReturnEmpty_WhenEntityIsMissing() {
        when(testPlanRepository.findByApplicationIdAndStageIdAndIdAndRetiredFalse(1L, 2L, 3L)).thenReturn(
                Optional.empty());

        Optional<PersistedTestPlan> result = service.getPersistedTestPlan(
                new TestPlanDatabaseService.PersistedTestPlanDetails(1L, 2L, 3L, false));

        assertTrue(result.isEmpty());
        verify(testPlanEntityMapper, never()).mapEntityToPersistedTestPlan(any());
    }

    @Test
    void getExecutableTestPlan_ShouldMapEntityWithInstructionResolver() {
        TestPlanEntity entity = new TestPlanEntity().setId(1L);
        TestPlan testPlan = Mockito.mock(TestPlan.class);
        when(testPlanRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(testPlanEntityMapper.fromEntity(eq(entity), any())).thenReturn(testPlan);

        Optional<TestPlan> result = service.getExecutableTestPlan(1L);

        assertTrue(result.isPresent());
        assertEquals(testPlan, result.get());
        verify(testPlanRepository).findById(1L);
        verify(testPlanEntityMapper).fromEntity(eq(entity), any());
    }

    @Test
    void getExecutableTestPlan_ShouldReturnEmpty_WhenEntityIsMissing() {
        when(testPlanRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<TestPlan> result = service.getExecutableTestPlan(1L);

        assertTrue(result.isEmpty());
        verify(testPlanEntityMapper, never()).fromEntity(any(), any());
    }

    @Test
    void hasActions_ShouldReturnTrue_WhenActionsExist() {
        when(actionRepository.existsByTestPlanId(1L)).thenReturn(true);

        boolean result = service.hasActions(1L);

        assertTrue(result);
        verify(actionRepository).existsByTestPlanId(1L);
    }

    @Test
    void hasActions_ShouldReturnFalse_WhenActionsDoNotExist() {
        when(actionRepository.existsByTestPlanId(1L)).thenReturn(false);

        boolean result = service.hasActions(1L);

        assertFalse(result);
        verify(actionRepository).existsByTestPlanId(1L);
    }

    @Test
    void findTestPlanIdsWithActions_ShouldDelegateToRepository() {
        when(actionRepository.findTestPlanIdsWithActions(1L, 2L, List.of(3L, 4L))).thenReturn(Set.of(3L));

        Set<Long> result = service.findTestPlanIdsWithActions(
                new TestPlanDatabaseService.TestPlanActions(1L, 2L, List.of(3L, 4L)));

        assertEquals(Set.of(3L), result);
        verify(actionRepository).findTestPlanIdsWithActions(1L, 2L, List.of(3L, 4L));
    }

    @Test
    void findTestPlanIdsWithActions_ShouldNotQueryRepository_WhenInputIsEmpty() {
        Set<Long> result = service.findTestPlanIdsWithActions(
                new TestPlanDatabaseService.TestPlanActions(1L, 2L, List.of()));

        assertTrue(result.isEmpty());
        verifyNoInteractions(actionRepository);
    }

    @Test
    void existsByLabel_ShouldDelegateToRepository() {
        when(testPlanRepository.existsByApplicationIdAndStageIdAndLabel(1L, 2L, "plan")).thenReturn(true);

        boolean result = service.existsByLabel(new TestPlanDatabaseService.TestPlanLabel(1L, 2L, "plan"));

        assertTrue(result);
        verify(testPlanRepository).existsByApplicationIdAndStageIdAndLabel(1L, 2L, "plan");
    }

}
