package io.github.yaforster.trails.adapter.mysql.testplan.entity.details;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
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
@DiscriminatorValue("7")
public class CheckExistenceActionDetailsEntity extends ActionDetailsEntity {

    private Long elementID;

}
