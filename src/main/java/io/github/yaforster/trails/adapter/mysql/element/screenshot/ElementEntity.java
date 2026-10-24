package io.github.yaforster.trails.adapter.mysql.element.screenshot;

import io.github.yaforster.trails.core.data.ElementType;
import io.github.yaforster.trails.core.test.action.element.LocatorType;
import jakarta.persistence.*;
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
public class ElementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String label;
    private LocatorType locatorType;
    private Long applicationId;
    private Long stageId;
    @Column()
    private String locator;
    private ElementType type;
    @JdbcTypeCode(Types.TINYINT)
    private boolean retired;

}
