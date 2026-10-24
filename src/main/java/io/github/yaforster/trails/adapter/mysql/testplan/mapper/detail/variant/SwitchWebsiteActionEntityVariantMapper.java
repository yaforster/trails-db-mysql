package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.SwitchWebsiteDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.field.SwitchWebsiteDetailsMapper;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.browser.SwitchWebsiteAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.springframework.stereotype.Component;

import java.util.function.UnaryOperator;

@Component
class SwitchWebsiteActionEntityVariantMapper
        extends
            AbstractActionEntityVariantMapper<SwitchWebsiteAction, SwitchWebsiteDetailsEntity> {

    private final SwitchWebsiteDetailsMapper mapper;

    SwitchWebsiteActionEntityVariantMapper(SwitchWebsiteDetailsMapper mapper) {
        super(SwitchWebsiteAction.class, SwitchWebsiteDetailsEntity.class);
        this.mapper = mapper;
    }

    @Override
    protected ActionDetailsEntity toVariantEntity(SwitchWebsiteAction action) {
        return mapper.toEntity(action);
    }

    @Override
    protected Action fromVariantEntity(ActionEntity entity, SwitchWebsiteDetailsEntity details,
            UnaryOperator<ValueComputationInstruction> instructionResolver) {
        return mapper.fromEntity(entity, details, instructionResolver);
    }

}
