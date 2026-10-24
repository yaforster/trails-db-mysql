package io.github.yaforster.trails.adapter.mysql.result.testpath;

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
public class TestPathResultEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Long testSetResultId;
    private String pathLabel;

}
