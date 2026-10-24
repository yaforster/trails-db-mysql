package io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.ClickActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.details.TypingDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.mapper.detail.variant.ActionEntityVariantMapper;
import io.github.yaforster.trails.core.MappingException;
import io.github.yaforster.trails.core.test.Action;
import io.github.yaforster.trails.core.test.action.element.ClickAction;
import io.github.yaforster.trails.core.test.action.element.TypingAction;
import io.github.yaforster.trails.core.test.action.value.ValueComputationInstruction;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.UnaryOperator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActionEntityMapperRegistryTest {

    @Test
    void shouldPreferExactDetailsType_whenBaseTypeIsAlsoRegistered() {
        ActionEntityVariantMapper base = new RegisteredTypes(ClickAction.class, ActionDetailsEntity.class);
        ActionEntityVariantMapper exact = new RegisteredTypes(TypingAction.class, TypingDetailsEntity.class);
        ActionEntityMapperRegistry registry = new ActionEntityMapperRegistry(List.of(base, exact));

        ActionEntityVariantMapper selected = registry.forDetails(TypingDetailsEntity.class);

        assertThat(selected).isSameAs(exact);
    }

    @Test
    void shouldUseBaseDetailsMapper_whenRuntimeTypeIsASubclass() {
        ActionEntityVariantMapper base = new RegisteredTypes(ClickAction.class, ClickActionDetailsEntity.class);
        ActionEntityMapperRegistry registry = new ActionEntityMapperRegistry(List.of(base));

        ActionEntityVariantMapper selected = registry.forDetails(ClickDetailsProxy.class);

        assertThat(selected).isSameAs(base);
    }

    @Test
    void shouldPreferExactActionType_whenBaseTypeIsAlsoRegistered() {
        ActionEntityVariantMapper base = new RegisteredTypes(Action.class, TypingDetailsEntity.class);
        ActionEntityVariantMapper exact = new RegisteredTypes(ClickAction.class, ClickActionDetailsEntity.class);
        ActionEntityMapperRegistry registry = new ActionEntityMapperRegistry(List.of(base, exact));

        ActionEntityVariantMapper selected = registry.forAction(ClickAction.class);

        assertThat(selected).isSameAs(exact);
    }

    @Test
    void shouldUseBaseActionMapper_whenOnlyBaseTypeIsRegistered() {
        ActionEntityVariantMapper base = new RegisteredTypes(Action.class, ClickActionDetailsEntity.class);
        ActionEntityMapperRegistry registry = new ActionEntityMapperRegistry(List.of(base));

        ActionEntityVariantMapper selected = registry.forAction(ClickAction.class);

        assertThat(selected).isSameAs(base);
    }

    @Test
    void shouldRejectDuplicateActionTypes_whenRegisteringMappers() {
        ActionEntityVariantMapper first = new RegisteredTypes(ClickAction.class, ClickActionDetailsEntity.class);
        ActionEntityVariantMapper second = new RegisteredTypes(ClickAction.class, TypingDetailsEntity.class);
        List<ActionEntityVariantMapper> variants = List.of(first, second);

        assertThatThrownBy(() -> new ActionEntityMapperRegistry(variants)).isInstanceOf(IllegalStateException.class)
                                                                          .hasMessage(
                                                                                  "Multiple action entity mappers registered for action type "
                                                                                          + ClickAction.class.getName());
    }

    @Test
    void shouldRejectDuplicateDetailsTypes_whenRegisteringMappers() {
        ActionEntityVariantMapper first = new RegisteredTypes(ClickAction.class, ClickActionDetailsEntity.class);
        ActionEntityVariantMapper second = new RegisteredTypes(TypingAction.class, ClickActionDetailsEntity.class);
        List<ActionEntityVariantMapper> variants = List.of(first, second);

        assertThatThrownBy(() -> new ActionEntityMapperRegistry(variants)).isInstanceOf(IllegalStateException.class)
                                                                          .hasMessage(
                                                                                  "Multiple action entity mappers registered for details type "
                                                                                          + ClickActionDetailsEntity.class.getName());
    }

    @Test
    void shouldRejectUnknownAction_whenNoMapperMatches() {
        ActionEntityMapperRegistry registry = new ActionEntityMapperRegistry(List.of());

        assertThatThrownBy(() -> registry.forAction(ClickAction.class)).isInstanceOf(MappingException.class)
                                                                       .hasMessage("Unhandled action type: "
                                                                               + ClickAction.class.getName());
    }

    @Test
    void shouldRejectUnknownDetails_whenNoMapperMatches() {
        ActionEntityMapperRegistry registry = new ActionEntityMapperRegistry(List.of());

        assertThatThrownBy(() -> registry.forDetails(
                ClickActionDetailsEntity.class)).isInstanceOf(MappingException.class)
                                                .hasMessage("Unhandled ActionDetailsEntity type: "
                                                        + ClickActionDetailsEntity.class.getName());
    }

    private static class ClickDetailsProxy extends ClickActionDetailsEntity {}

    private record RegisteredTypes(Class<? extends Action> actionType,
            Class<? extends ActionDetailsEntity> detailsType) implements ActionEntityVariantMapper {

        @Override
        public ActionDetailsEntity toEntity(Action action, Long applicationId, Long stageId) {
            throw new AssertionError("Registry lookup must not perform mapping");
        }

        @Override
        public Action fromEntity(ActionEntity entity, ActionDetailsEntity details,
                UnaryOperator<ValueComputationInstruction> instructionResolver) {
            throw new AssertionError("Registry lookup must not perform mapping");
        }
    }
}
