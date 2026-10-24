package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.DownloadedDocumentTextCheckDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.DownloadedDocumentTextCheckDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.document.CheckDownloadedDocumentTextAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class DownloadedDocumentTextCheckActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<CheckDownloadedDocumentTextAction, DownloadedDocumentTextCheckDetailsEntity> {

    private final DownloadedDocumentTextCheckDetailsMapper mapper;

    DownloadedDocumentTextCheckActionEntityVariantMapper(DownloadedDocumentTextCheckDetailsMapper mapper) {
        super(CheckDownloadedDocumentTextAction.class, DownloadedDocumentTextCheckDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(CheckDownloadedDocumentTextAction action) {
        return mapper.toEntity(action);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, DownloadedDocumentTextCheckDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details);
    }

}
