package io.github.yaforster.trails.adapter.mysql.testplan.promotion;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;

import java.util.Optional;

abstract class AbstractActionDetailsCopyVariant<D extends ActionDetailsEntity> implements ActionDetailsCopyVariant {

    private final Class<D> detailsType;

    protected AbstractActionDetailsCopyVariant(Class<D> detailsType) {
        this.detailsType = detailsType;
    }

    @Override
    public Class<? extends ActionDetailsEntity> detailsType() {
        return detailsType;
    }

    @Override
    public Optional<ActionDetailsEntity> copy(ActionDetailsEntity details, ActionDetailsCopyContext context) {
        return copyDetails(detailsType.cast(details), context);
    }

    protected abstract Optional<ActionDetailsEntity> copyDetails(D details, ActionDetailsCopyContext context);

}
