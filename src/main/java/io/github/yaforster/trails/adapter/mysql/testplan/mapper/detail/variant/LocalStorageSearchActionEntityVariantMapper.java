package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.LocalStorageSearchDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.CheckLocalStorageDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckLocalStorageAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class LocalStorageSearchActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<CheckLocalStorageAction, LocalStorageSearchDetailsEntity> {

    private final CheckLocalStorageDetailsMapper mapper;

    LocalStorageSearchActionEntityVariantMapper(CheckLocalStorageDetailsMapper mapper) {
        super(CheckLocalStorageAction.class, LocalStorageSearchDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(CheckLocalStorageAction action) {
        return mapper.toEntity(action);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, LocalStorageSearchDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details, instructionResolver);
    }

}
