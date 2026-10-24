package io.github.yaforster.trails.adapter.mysql.result.run;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TestRunResultEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Long applicationId;
    private Long stageId;
    private Long testPlanId;
    private Timestamp timestamp;
    @Enumerated(EnumType.STRING)
    private TestRunStatus status;
    private String label;

}
