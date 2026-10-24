package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.DownloadedFileCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.action.download.CheckDownloadedFileAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DownloadedFileCheckDetailsMapper {

    private final PositionEntityMapper positionMapper;

    public DownloadedFileCheckDetailsEntity toEntity(CheckDownloadedFileAction action) {
        return new DownloadedFileCheckDetailsEntity(action.getFileName());
    }

    public CheckDownloadedFileAction fromEntity(ActionEntity entity, DownloadedFileCheckDetailsEntity details) {
        Position position = positionMapper.fromEntity(entity.getPosition());
        return CheckDownloadedFileAction.builder()
                                        .actionID(entity.getId())
                                        .label(entity.getLabel())
                                        .nextActions(entity.getNextActions())
                                        .fileName(details.getFileName())
                                        .position(position)
                                        .build();
    }

}
