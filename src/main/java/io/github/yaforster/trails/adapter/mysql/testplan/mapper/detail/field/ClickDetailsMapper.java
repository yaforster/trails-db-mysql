package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ClickActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.core.test.action.element.ClickAction;
import io.github.yaforster.trails.core.test.action.element.Locator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClickDetailsMapper {

    private final ElementDatabaseService elementDatabaseService;
    private final PositionEntityMapper positionMapper;

    public ClickActionDetailsEntity toEntity(ClickAction action, Long applicationId, Long stageId) {
        String locatorText = action.getLocatorForElementToActOn()
                                   .locatorString();
        ElementDatabaseService.ElementLocator elementLocator = new ElementDatabaseService.ElementLocator(applicationId,
                stageId, locatorText);
        Long elementID = elementDatabaseService.getElementIDByLocator(elementLocator);
        return new ClickActionDetailsEntity(elementID);
    }

    public ClickAction fromEntity(ActionEntity entity, ClickActionDetailsEntity details) {
        Locator locator = elementDatabaseService.getElementLocator(details.getElementID());
        Position position = positionMapper.fromEntity(entity.getPosition());
        return ClickAction.builder()
                          .actionID(entity.getId())
                          .label(entity.getLabel())
                          .nextActions(entity.getNextActions())
                          .locatorForElementToActOn(locator)
                          .position(position)
                          .build();
    }

}
