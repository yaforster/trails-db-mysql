package io.github.yaforster.trails.adapter.mysql.result.action;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public abstract class ResultEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Long testPathResultId;
    private Integer executionOrder;
    private Long actionId;
    private String label;
    private boolean success;
    private String message;

}
