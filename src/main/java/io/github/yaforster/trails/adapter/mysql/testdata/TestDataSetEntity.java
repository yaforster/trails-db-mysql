package io.github.yaforster.trails.adapter.mysql.testdata;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Accessors(chain = true)
public class TestDataSetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String label;
    @Column(nullable = false, updatable = false)
    private Instant creationDate;
    @OneToMany(mappedBy = "testDataSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestDataEntryEntity> values = new ArrayList<>();
    @Column(nullable = false)
    @JdbcTypeCode(Types.TINYINT)
    private boolean retired;

    @PrePersist
    void setCreationDateIfMissing() {
        if (creationDate == null) {
            creationDate = Instant.now();
        }
    }

}
