package io.github.yaforster.trails.adapter.mysql.testplan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TestPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long applicationId;
    private Long stageId;
    private String label;
    @JdbcTypeCode(Types.TINYINT)
    private boolean retired;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActionEntity> testSteps;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "test_plan_id", nullable = false)
    private List<TestPlanGroupEntity> groups;

}
