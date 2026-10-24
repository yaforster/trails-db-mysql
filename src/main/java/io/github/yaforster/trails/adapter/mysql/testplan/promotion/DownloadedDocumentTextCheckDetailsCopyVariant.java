package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.DownloadedDocumentTextCheckDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class DownloadedDocumentTextCheckDetailsCopyVariant
        extends
            AbstractActionDetailsCopyVariant<DownloadedDocumentTextCheckDetailsEntity> {

    DownloadedDocumentTextCheckDetailsCopyVariant() {
        super(DownloadedDocumentTextCheckDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(DownloadedDocumentTextCheckDetailsEntity details,
            ActionDetailsCopyContext context) {
        return Optional.of(new DownloadedDocumentTextCheckDetailsEntity(details.getFileName(),
                details.getExpectedText(), details.getCaseSensitive()));
    }

}
