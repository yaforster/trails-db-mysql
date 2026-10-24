package io.github.yaforster.trails.adapter.mysql.result.action;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("3")
public class TechnicalFailureEntity extends ResultEntity {

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "LONGTEXT")
    private String exceptionMessageFromAction;

}
