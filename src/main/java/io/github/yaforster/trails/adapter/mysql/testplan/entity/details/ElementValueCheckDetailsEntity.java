package io.github.yaforster.trails.adapter.mysql.testplan.entity.details;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import io.github.yaforster.trails.adapter.mysql.testplan.entity.instruction.ValueComputationInstructionEntity;
import io.github.yaforster.trails.core.test.action.element.ElementValueSource;
import jakarta.persistence.*;
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
@DiscriminatorValue("11")
public class ElementValueCheckDetailsEntity extends ActionDetailsEntity {

    private Long elementID;
    @Enumerated(EnumType.STRING)
    private ElementValueSource valueSource;
    private String valueName;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private ValueComputationInstructionEntity valueComputation;

}
