package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.core.test.Position;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.DownloadedDocumentTextCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.PositionEntityMapper;
import io.github.yaforster.trails.core.test.action.document.CheckDownloadedDocumentTextAction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DownloadedDocumentTextCheckDetailsMapper {

    private final PositionEntityMapper positionMapper;

    public DownloadedDocumentTextCheckDetailsEntity toEntity(CheckDownloadedDocumentTextAction action) {
        return new DownloadedDocumentTextCheckDetailsEntity(action.getFileName(), action.getExpectedText(),
                action.isCaseSensitive());
    }

    public CheckDownloadedDocumentTextAction fromEntity(ActionEntity entity,
            DownloadedDocumentTextCheckDetailsEntity details) {
        boolean caseSensitive = Boolean.TRUE.equals(details.getCaseSensitive());
        Position position = positionMapper.fromEntity(entity.getPosition());
        return CheckDownloadedDocumentTextAction.builder()
                                                .actionID(entity.getId())
                                                .label(entity.getLabel())
                                                .nextActions(entity.getNextActions())
                                                .fileName(details.getFileName())
                                                .expectedText(details.getExpectedText())
                                                .caseSensitive(caseSensitive)
                                                .position(position)
                                                .build();
    }

}
