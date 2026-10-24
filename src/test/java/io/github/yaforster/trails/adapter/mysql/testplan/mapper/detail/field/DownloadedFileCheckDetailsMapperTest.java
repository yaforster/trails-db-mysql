package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.PositionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.DownloadedFileCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.core.test.action.download.CheckDownloadedFileAction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DownloadedFileCheckDetailsMapperTest {

    private final PositionEntityMapper positionMapper = mock(PositionEntityMapper.class);
    private final DownloadedFileCheckDetailsMapper mapper = new DownloadedFileCheckDetailsMapper(positionMapper);

    @Test
    void toEntity_ShouldMapFileName() {
        CheckDownloadedFileAction action = CheckDownloadedFileAction.builder()
                                                                    .fileName("report.pdf")
                                                                    .build();

        DownloadedFileCheckDetailsEntity entity = mapper.toEntity(action);

        assertEquals("report.pdf", entity.getFileName());
    }

    @Test
    void fromEntity_ShouldMapAction() {
        PositionEntity positionEntity = new PositionEntity();
        Position position = new Position(BigDecimal.ONE, BigDecimal.TEN);
        ActionEntity entity = new ActionEntity().setId(1L)
                                                .setLabel("download check")
                                                .setNextActions(List.of(2L))
                                                .setPosition(positionEntity);
        DownloadedFileCheckDetailsEntity details = new DownloadedFileCheckDetailsEntity("report.pdf");
        when(positionMapper.fromEntity(positionEntity)).thenReturn(position);

        CheckDownloadedFileAction action = mapper.fromEntity(entity, details);

        assertEquals(1L, action.getActionID());
        assertEquals("download check", action.getLabel());
        assertEquals(List.of(2L), action.getNextActions());
        assertEquals("report.pdf", action.getFileName());
        assertEquals(position, action.getPosition());
    }

}
