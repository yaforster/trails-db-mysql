package io.github.yaforster.trails.adapter.mysql.testplan.entity.details;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMove;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMoveDirection;
import io.github.yaforster.trails.core.test.action.browser.viewport.ViewportMoveUnit;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@DiscriminatorValue("13")
public class ViewportMoveDetailsEntity extends ActionDetailsEntity {

    @Enumerated(EnumType.STRING)
    private ViewportMove movement;
    @Enumerated(EnumType.STRING)
    private ViewportMoveDirection direction;
    private double amount;
    @Enumerated(EnumType.STRING)
    private ViewportMoveUnit unit;
    private long delayAfterMoveMillis;

}
