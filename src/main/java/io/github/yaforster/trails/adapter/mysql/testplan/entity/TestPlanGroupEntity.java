package io.github.yaforster.trails.adapter.mysql.testplan.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TestPlanGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    private BigDecimal xCoordinatePixels;
    private BigDecimal yCoordinatePixels;
    private BigDecimal widthPixels;
    private BigDecimal heightPixels;
    @ElementCollection
    @CollectionTable(name = "test_plan_group_action_reference", joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "action_reference_id", nullable = false)
    private List<Long> actionReferenceIds;

}
