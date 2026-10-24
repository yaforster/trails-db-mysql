package io.github.yaforster.trails.adapter.mysql.testdata;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Data
@Accessors(chain = true)
public class TestDataEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "entry_key", nullable = false)
    private String key;
    @Column(name = "entry_value", nullable = false)
    private String value;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_data_set_id", nullable = false)
    private TestDataSetEntity testDataSet;

}
