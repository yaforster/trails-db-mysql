package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.DownloadedFileCheckDetailsEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class DownloadedFileCheckDetailsCopyVariant extends AbstractActionDetailsCopyVariant<DownloadedFileCheckDetailsEntity> {

    DownloadedFileCheckDetailsCopyVariant() {
        super(DownloadedFileCheckDetailsEntity.class);
    }

    @Override
    protected Optional<ActionDetailsEntity> copyDetails(DownloadedFileCheckDetailsEntity details,
            ActionDetailsCopyContext context) {
        return Optional.of(new DownloadedFileCheckDetailsEntity(details.getFileName()));
    }

}
