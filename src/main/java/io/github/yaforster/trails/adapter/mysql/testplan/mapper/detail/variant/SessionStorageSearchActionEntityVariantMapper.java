package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SessionStorageSearchDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.CheckSessionStorageDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.browser.storage.CheckSessionStorageAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class SessionStorageSearchActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<CheckSessionStorageAction, SessionStorageSearchDetailsEntity> {

    private final CheckSessionStorageDetailsMapper mapper;

    SessionStorageSearchActionEntityVariantMapper(CheckSessionStorageDetailsMapper mapper) {
        super(CheckSessionStorageAction.class, SessionStorageSearchDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(CheckSessionStorageAction action) {
        return mapper.toEntity(action);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, SessionStorageSearchDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details, instructionResolver);
    }

}
