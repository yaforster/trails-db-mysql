package io.github.yaforster.trails.adapter.mysql.testplan.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ActionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The {@code referenceID} used when defining action chains in the API. This is only used for translating the
     * initial chain references into persisted action IDs.
     */
    private Long actionId;
    /**
     * Foreign key reference to the owning test plan. Stored as an ID on purpose to keep persistence and mapping
     * decoupled from object graphs.
     */
    private Long testPlanId;
    private String label;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "details_id", nullable = false)
    private ActionDetailsEntity details;
    @ElementCollection
    private List<Long> nextActions;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "position_id", nullable = false)
    private PositionEntity position;

}
