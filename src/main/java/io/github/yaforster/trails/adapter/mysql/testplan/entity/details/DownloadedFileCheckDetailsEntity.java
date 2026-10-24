package io.github.yaforster.trails.adapter.mysql.testplan.entity.details;

import io.github.yaforster.trails.adapter.mysql.testplan.entity.ActionDetailsEntity;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@DiscriminatorValue("12")
public class DownloadedFileCheckDetailsEntity extends ActionDetailsEntity {

    @Column(name = "downloaded_file_name")
    private String fileName;

}
