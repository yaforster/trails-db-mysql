package io.github.yaforster.trails.adapter.mysql.stage;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class StageEntity {

    @Id
    @GeneratedValue
    private Long id;
    private Long applicationId;
    private String label;
    private String url;
    @JdbcTypeCode(Types.TINYINT)
    private boolean retired;

}
