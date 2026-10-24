package io.github.yaforster.trails.adapter.mysql.result.testset;

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
public class TestCaseResultEntity {

    private Timestamp timestamp;
    private String testedInBrowser;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
