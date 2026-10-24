package io.github.yaforster.trails.adapter.mysql.testplan;

import io.github.yaforster.trails.adapter.mysql.SpringPageMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.TestPlanEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.TestPlanEntityMapper;
import io.github.yaforster.trails.adapter.mysql.testplan.repo.ActionRepository;
import io.github.yaforster.trails.adapter.mysql.testplan.repo.TestPlanRepository;
import io.github.yaforster.trails.app.services.TestDataValueResolutionService;
import io.github.yaforster.trails.app.services.TestPlanDatabaseService;
import io.github.yaforster.trails.core.PagedResult;
import io.github.yaforster.trails.core.data.TestPlan;
import io.github.yaforster.trails.core.definition.TestPlanDefinition;
import io.github.yaforster.trails.core.deletion.DatabaseDeletionResult;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import io.github.yaforster.trails.core.persisted.PersistedTestPlan;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class TestPlanDatabaseServiceImpl implements TestPlanDatabaseService {

    private final TestPlanRepository testPlanRepository;
    private final ActionRepository actionRepository;
    private final TestPlanEntityMapper testPlanEntityMapper;
    private final TestDataValueResolutionService valueResolutionService;
    private final TestPlanActionReferenceResolver actionReferenceResolver;
    private final TestPlanPromotionService promotionService;

    public TestPlanDatabaseServiceImpl(TestPlanRepository testPlanRepository, ActionRepository actionRepository,
            TestPlanEntityMapper testPlanEntityMapper, @Lazy TestDataValueResolutionService valueResolutionService,
            TestPlanActionReferenceResolver actionReferenceResolver, TestPlanPromotionService promotionService) {
        this.testPlanRepository = testPlanRepository;
        this.actionRepository = actionRepository;
        this.testPlanEntityMapper = testPlanEntityMapper;
        this.valueResolutionService = valueResolutionService;
        this.actionReferenceResolver = actionReferenceResolver;
        this.promotionService = promotionService;
    }

    @Override
    @Transactional
    public PersistedTestPlan storeTestPlan(TestPlanDefinition definition) {
        TestPlanEntity entity = testPlanEntityMapper.toEntity(definition);
        TestPlanEntity storedEntity = testPlanRepository.save(entity);

        // Cascading the initial save assigns the action IDs needed to resolve definition references.
        actionReferenceResolver.resolveReferences(storedEntity);
        actionRepository.saveAll(storedEntity.getTestSteps());

        return testPlanEntityMapper.mapEntityToPersistedTestPlan(storedEntity);
    }

    @Override
    public DatabaseDeletionResult deleteTestPlan(TestPlanReference testPlanReference) {
        Optional<TestPlanEntity> entity = testPlanRepository.findByApplicationIdAndStageIdAndId(
                testPlanReference.applicationId(), testPlanReference.stageId(), testPlanReference.testPlanId());
        if (entity.isEmpty()) {
            return new DeletionNotFound(testPlanReference.testPlanId());
        }
        try {
            TestPlanEntity testPlan = entity.get();
            testPlan.setRetired(true);
            testPlanRepository.save(testPlan);
            return new DeletionSuccess(testPlanReference.testPlanId());
        } catch (Exception e) {
            return new DeletionFailure(testPlanReference.testPlanId(), e);
        }
    }

    @Override
    public DatabaseDeletionResult restoreTestPlan(TestPlanReference testPlanReference) {
        Optional<TestPlanEntity> entity = testPlanRepository.findByApplicationIdAndStageIdAndId(
                testPlanReference.applicationId(), testPlanReference.stageId(), testPlanReference.testPlanId());
        if (entity.isEmpty()) {
            return new DeletionNotFound(testPlanReference.testPlanId());
        }
        try {
            TestPlanEntity testPlan = entity.get();
            testPlan.setRetired(false);
            testPlanRepository.save(testPlan);
            return new DeletionSuccess(testPlanReference.testPlanId());
        } catch (Exception exception) {
            return new DeletionFailure(testPlanReference.testPlanId(), exception);
        }
    }

    @Override
    @Transactional
    public Optional<PersistedTestPlan> promoteTestPlan(TestPlanPromotion testPlanPromotion) {
        Optional<TestPlanEntity> source = testPlanRepository.findByApplicationIdAndStageIdAndIdAndRetiredFalse(
                testPlanPromotion.applicationId(), testPlanPromotion.sourceStageId(), testPlanPromotion.testPlanId());
        if (source.isEmpty()) {
            return Optional.empty();
        }
        TestPlanEntity sourcePlan = source.get();
        Optional<TestPlanCopy> copy = promotionService.copyToStage(sourcePlan, testPlanPromotion.targetStageId());
        if (copy.isEmpty()) {
            return Optional.empty();
        }
        TestPlanCopy testPlanCopy = copy.get();
        TestPlanEntity copiedPlan = testPlanCopy.entity();
        TestPlanEntity stored = testPlanRepository.save(copiedPlan);
        testPlanCopy.resolveNextActions();
        actionRepository.saveAll(stored.getTestSteps());
        PersistedTestPlan promotedPlan = testPlanEntityMapper.mapEntityToPersistedTestPlan(stored);
        return Optional.of(promotedPlan);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestPlan> getTestPlan(TestPlanReference testPlanReference) {
        Optional<TestPlanEntity> entity = testPlanRepository.findByApplicationIdAndStageIdAndId(
                testPlanReference.applicationId(), testPlanReference.stageId(), testPlanReference.testPlanId());
        return entity.map(testPlanEntityMapper::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PersistedTestPlan> getPersistedTestPlan(PersistedTestPlanDetails persistedTestPlanDetails) {
        Long applicationId = persistedTestPlanDetails.applicationId();
        Long stageId = persistedTestPlanDetails.stageId();
        Long testPlanId = persistedTestPlanDetails.testPlanId();
        Optional<TestPlanEntity> entity;
        if (persistedTestPlanDetails.includeRetired()) {
            entity = testPlanRepository.findByApplicationIdAndStageIdAndId(applicationId, stageId, testPlanId);
        } else {
            entity = testPlanRepository.findByApplicationIdAndStageIdAndIdAndRetiredFalse(applicationId, stageId,
                    testPlanId);
        }
        return entity.map(testPlanEntityMapper::mapEntityToPersistedTestPlan);
    }

    @Override
    public PagedResult<PersistedTestPlan> getTestPlans(TestPlanPage testPlanPage) {
        Pageable pageable = PageRequest.of(testPlanPage.page(), testPlanPage.size());
        Page<TestPlanEntity> testPlans;
        if (testPlanPage.includeRetired()) {
            testPlans = testPlanRepository.findByApplicationIdAndStageId(testPlanPage.applicationId(),
                    testPlanPage.stageId(), pageable);
        } else {
            testPlans = testPlanRepository.findByApplicationIdAndStageIdAndRetiredFalse(testPlanPage.applicationId(),
                    testPlanPage.stageId(), pageable);
        }
        Page<PersistedTestPlan> persistedPlans = testPlans.map(testPlanEntityMapper::mapEntityToPersistedTestPlan);
        return SpringPageMapper.toPagedResult(persistedPlans);
    }

    @Override
    public boolean hasActions(Long testPlanId) {
        return actionRepository.existsByTestPlanId(testPlanId);
    }

    @Override
    public Set<Long> findTestPlanIdsWithActions(TestPlanActions testPlanActions) {
        if (testPlanActions.testPlanIds()
                           .isEmpty()) {
            return Set.of();
        }
        return actionRepository.findTestPlanIdsWithActions(testPlanActions.applicationId(), testPlanActions.stageId(),
                testPlanActions.testPlanIds());
    }

    @Override
    public boolean existsByLabel(TestPlanLabel testPlanLabel) {
        return testPlanRepository.existsByApplicationIdAndStageIdAndLabel(testPlanLabel.applicationId(),
                testPlanLabel.stageId(), testPlanLabel.label());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestPlan> getExecutableTestPlan(Long testPlanId) {
        Optional<TestPlanEntity> entity = testPlanRepository.findById(testPlanId);
        return entity.map(testPlan -> testPlanEntityMapper.fromEntity(testPlan,
                valueResolutionService::resolveTestDataReference));
    }

}
