package io.github.yaforster.trails.adapter.mysql.application;

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
public class ApplicationEntity {

    @Id
    @GeneratedValue
    private Long id;
    private String label;
    @JdbcTypeCode(Types.TINYINT)
    private boolean retired;

}
