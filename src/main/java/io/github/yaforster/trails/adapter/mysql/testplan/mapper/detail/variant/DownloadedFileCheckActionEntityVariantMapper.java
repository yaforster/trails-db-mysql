package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.DownloadedFileCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.DownloadedFileCheckDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.download.CheckDownloadedFileAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class DownloadedFileCheckActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<CheckDownloadedFileAction, DownloadedFileCheckDetailsEntity> {

    private final DownloadedFileCheckDetailsMapper mapper;

    DownloadedFileCheckActionEntityVariantMapper(DownloadedFileCheckDetailsMapper mapper) {
        super(CheckDownloadedFileAction.class, DownloadedFileCheckDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(CheckDownloadedFileAction action) {
        return mapper.toEntity(action);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, DownloadedFileCheckDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details);
    }

}
