package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.CheckExistenceActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.element.CheckExistenceAction;
import io.github.yaforster.trails.core.test.action.element.Locator;
import io.github.yaforster.trails.core.test.action.element.LocatorType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CheckExistenceDetailsMapperTest extends TrailsTest {

    @Test
    void mapsBothDirections() {
        ElementDatabaseService elementDatabaseService = Mockito.mock(ElementDatabaseService.class);
        PositionEntityMapper positionMapper = Mockito.mock(PositionEntityMapper.class);
        CheckExistenceDetailsMapper mapper = new CheckExistenceDetailsMapper(elementDatabaseService, positionMapper);

        CheckExistenceAction action = Mockito.mock(CheckExistenceAction.class);
        Locator locatorForEntity = new Locator(LocatorType.CSS, ".exists");
        when(action.getLocatorForElementToActOn()).thenReturn(locatorForEntity);
        when(elementDatabaseService.getElementIDByLocator(
                new ElementDatabaseService.ElementLocator(1L, 2L, ".exists"))).thenReturn(55L);

        CheckExistenceActionDetailsEntity detailsEntity = mapper.toEntity(action, 1L, 2L);
        assertEquals(55L, detailsEntity.getElementID());

        ActionEntity actionEntity = getInstancioOf(ActionEntity.class).create()
                                                                      .setId(11L)
                                                                      .setLabel("exists");
        PositionEntity positionEntity = getInstancioOf(PositionEntity.class).create();
        actionEntity.setPosition(positionEntity);
        Locator locatorFromEntity = new Locator(LocatorType.XPATH, "//div");
        Position mappedPosition = getInstancioOf(Position.class).create();
        when(elementDatabaseService.getElementLocator(detailsEntity.getElementID())).thenReturn(locatorFromEntity);
        when(positionMapper.fromEntity(positionEntity)).thenReturn(mappedPosition);

        CheckExistenceAction mappedAction = mapper.fromEntity(actionEntity, detailsEntity);
        assertEquals(actionEntity.getId(), mappedAction.getActionID());
        assertEquals(actionEntity.getLabel(), mappedAction.getLabel());
        assertEquals(actionEntity.getNextActions(), mappedAction.getNextActions());
        assertEquals(locatorFromEntity, mappedAction.getLocatorForElementToActOn());
        assertEquals(mappedPosition, mappedAction.getPosition());
    }

}
