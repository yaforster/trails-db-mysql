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
@DiscriminatorValue("14")
public class DownloadedDocumentTextCheckDetailsEntity extends ActionDetailsEntity {

    @Column(name = "document_file_name")
    private String fileName;
    @Column(name = "document_expected_text", length = 2048)
    private String expectedText;
    @Column(name = "document_case_sensitive")
    private Boolean caseSensitive;

}
