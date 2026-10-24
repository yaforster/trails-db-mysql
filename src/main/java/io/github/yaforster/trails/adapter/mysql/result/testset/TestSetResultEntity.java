package io.github.yaforster.trails.adapter.mysql.result.testset;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TestSetResultEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    private TestCaseResultEntity testCaseResult;
    private int totalRunTime;
    private Long testRunId;
    private String browser;
    private String testPlanLabel;

}
