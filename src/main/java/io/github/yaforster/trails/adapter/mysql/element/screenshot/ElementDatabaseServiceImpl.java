package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.adapter.mysql.SpringPageMapper;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.core.EntityMissingException;
import io.github.yaforster.trails.core.MappingException;
import io.github.yaforster.trails.core.PagedResult;
import io.github.yaforster.trails.core.deletion.DatabaseDeletionResult;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import io.github.yaforster.trails.core.persisted.PersistedElement;
import io.github.yaforster.trails.core.test.action.element.Locator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Service
public class ElementDatabaseServiceImpl implements ElementDatabaseService {

    private static final String ELEMENT_LOCATOR_NOT_FOUND = "No active element with the given locator was found in the application stage";
    private static final String ELEMENT_ID_NOT_FOUND = "No element with ID %s was found in the database";
    private final ElementRepository repository;
    private final ElementPromotionService promotionService;
    private final ElementEntityMapper elementEntityMapper;

    @Override
    public PersistedElement storeElement(ElementCreation elementCreation) {
        ElementEntity entity = elementEntityMapper.toEntity(elementCreation.applicationId(), elementCreation.stageId(),
                elementCreation.elementDefinition());
        ElementEntity storedEntity = repository.save(entity);
        return elementEntityMapper.toPersisted(storedEntity);
    }

    @Override
    public Optional<PersistedElement> updateElement(ElementUpdate elementUpdate) {
        return repository.findByIdAndApplicationIdAndStageIdAndRetiredFalse(elementUpdate.elementId(),
                elementUpdate.applicationId(), elementUpdate.stageId())
                         .map(entity -> elementEntityMapper.updateEntity(entity, elementUpdate.elementDefinition()))
                         .map(repository::save)
                         .map(elementEntityMapper::toPersisted);
    }

    @Override
    public Optional<PersistedElement> getElement(ElementDetails elementDetails) {
        Optional<ElementEntity> entity;
        if (elementDetails.includeRetired()) {
            entity = repository.findByIdAndApplicationIdAndStageId(elementDetails.elementId(),
                    elementDetails.applicationId(), elementDetails.stageId());
        } else {
            entity = repository.findByIdAndApplicationIdAndStageIdAndRetiredFalse(elementDetails.elementId(),
                    elementDetails.applicationId(), elementDetails.stageId());
        }
        return entity.map(elementEntityMapper::toPersisted);
    }

    @Override
    public DatabaseDeletionResult deleteElement(ElementReference elementReference) {
        return updateRetirement(elementReference, element -> element.setRetired(true));
    }

    @Override
    public DatabaseDeletionResult restoreElement(ElementReference elementReference) {
        return updateRetirement(elementReference, element -> element.setRetired(false));
    }

    private DatabaseDeletionResult updateRetirement(ElementReference elementReference, Consumer<ElementEntity> update) {
        Optional<ElementEntity> entity = repository.findByIdAndApplicationIdAndStageId(elementReference.elementId(),
                elementReference.applicationId(), elementReference.stageId());
        if (entity.isEmpty()) {
            return new DeletionNotFound(elementReference.elementId());
        }
        try {
            ElementEntity element = entity.get();
            update.accept(element);
            repository.save(element);
            return new DeletionSuccess(elementReference.elementId());
        } catch (Exception exception) {
            return new DeletionFailure(elementReference.elementId(), exception);
        }
    }

    @Override
    @Transactional
    public Optional<PersistedElement> promoteElement(ElementPromotion elementPromotion) {
        return promotionService.promote(elementPromotion)
                               .map(elementEntityMapper::toPersisted);
    }

    @Override
    public PagedResult<PersistedElement> getElements(ElementPage elementPage) {
        Pageable pageable = PageRequest.of(elementPage.page(), elementPage.size());
        Page<ElementEntity> entities;
        if (elementPage.includeRetired()) {
            entities = repository.findAllByApplicationIdAndStageId(elementPage.applicationId(), elementPage.stageId(),
                    pageable);
        } else {
            entities = repository.findAllByApplicationIdAndStageIdAndRetiredFalse(elementPage.applicationId(),
                    elementPage.stageId(), pageable);
        }
        Page<PersistedElement> elements = entities.map(elementEntityMapper::toPersisted);
        return SpringPageMapper.toPagedResult(elements);
    }

    @Override
    public boolean hasElements(StageReference stageReference) {
        return repository.existsByApplicationIdAndStageIdAndRetiredFalse(stageReference.applicationId(),
                stageReference.stageId());
    }

    @Override
    public Set<Long> findStageIdsWithElements(CandidateStages candidateStages) {
        if (candidateStages.stageIds()
                           .isEmpty()) {
            return Set.of();
        }
        return repository.findStageIdsWithActiveElements(candidateStages.applicationId(), candidateStages.stageIds());
    }

    @Override
    public boolean existsByLabel(ElementLabel elementLabel) {
        return repository.existsByApplicationIdAndStageIdAndLabel(elementLabel.applicationId(), elementLabel.stageId(),
                elementLabel.label());
    }

    @Override
    public boolean existsByLocatorString(ElementLocatorText locatorText) {
        return repository.existsByApplicationIdAndStageIdAndLocator(locatorText.applicationId(), locatorText.stageId(),
                locatorText.locatorString());
    }

    @Override
    public Long getElementIDByLocator(ElementLocator elementLocator) {
        Optional<ElementEntity> elementToActOn = repository.findByApplicationIdAndStageIdAndLocatorAndRetiredFalse(
                elementLocator.applicationId(), elementLocator.stageId(), elementLocator.locator());
        if (elementToActOn.isEmpty()) {
            throw new EntityMissingException(ELEMENT_LOCATOR_NOT_FOUND);
        }
        return elementToActOn.get()
                             .getId();
    }

    @Override
    public Locator getElementLocator(Long elementID) {
        Optional<ElementEntity> elementToActOn = repository.findById(elementID);
        if (elementToActOn.isEmpty()) {
            String message = ELEMENT_ID_NOT_FOUND.formatted(elementID);
            throw new MappingException(message);
        }
        ElementEntity element = elementToActOn.get();
        return new Locator(element.getLocatorType(), element.getLocator());

    }

    @Override
    public Optional<Long> findPromotionTargetElementId(ElementPromotionTarget elementPromotionTarget) {
        return repository.findById(elementPromotionTarget.sourceElementId())
                         .flatMap(
                                 sourceElement -> repository.findByApplicationIdAndStageIdAndLocatorAndLocatorTypeAndTypeAndRetiredFalse(
                                         elementPromotionTarget.applicationId(), elementPromotionTarget.targetStageId(),
                                         sourceElement.getLocator(), sourceElement.getLocatorType(),
                                         sourceElement.getType()))
                         .map(ElementEntity::getId);
    }

}
