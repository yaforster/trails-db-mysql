package io.github.yaforster.trails.adapter.mysql.result.action;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("1")
public class SkippedResultEntity extends ResultEntity {

}
