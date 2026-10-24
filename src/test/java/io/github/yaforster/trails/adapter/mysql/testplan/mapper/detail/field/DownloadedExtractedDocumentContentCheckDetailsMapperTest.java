package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.DownloadedDocumentTextCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.document.CheckDownloadedDocumentTextAction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

class DownloadedExtractedDocumentContentCheckDetailsMapperTest {

    private final PositionEntityMapper positionMapper = Mockito.mock(PositionEntityMapper.class);
    private final DownloadedDocumentTextCheckDetailsMapper mapper = new DownloadedDocumentTextCheckDetailsMapper(
            positionMapper);

    @Test
    void toEntity_MapsFields() {
        CheckDownloadedDocumentTextAction action = CheckDownloadedDocumentTextAction.builder()
                                                                                    .fileName("report.pdf")
                                                                                    .expectedText("approved")
                                                                                    .caseSensitive(true)
                                                                                    .build();

        DownloadedDocumentTextCheckDetailsEntity entity = mapper.toEntity(action);

        assertEquals("report.pdf", entity.getFileName());
        assertEquals("approved", entity.getExpectedText());
        assertEquals(true, entity.getCaseSensitive());
    }

    @Test
    void fromEntity_MapsFields() {
        PositionEntity positionEntity = new PositionEntity(1L, BigDecimal.ONE, BigDecimal.TEN);
        Position position = new Position(BigDecimal.ONE, BigDecimal.TEN);
        ActionEntity actionEntity = new ActionEntity();
        actionEntity.setId(10L);
        actionEntity.setLabel("document check");
        actionEntity.setNextActions(List.of(11L));
        actionEntity.setPosition(positionEntity);
        DownloadedDocumentTextCheckDetailsEntity details = new DownloadedDocumentTextCheckDetailsEntity("report.pdf",
                "approved", null);
        when(positionMapper.fromEntity(positionEntity)).thenReturn(position);

        CheckDownloadedDocumentTextAction action = mapper.fromEntity(actionEntity, details);

        assertEquals(10L, action.getActionID());
        assertEquals("document check", action.getLabel());
        assertEquals(List.of(11L), action.getNextActions());
        assertEquals("report.pdf", action.getFileName());
        assertEquals("approved", action.getExpectedText());
        assertFalse(action.isCaseSensitive());
        assertEquals(position, action.getPosition());
    }

}
