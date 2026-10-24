package io.github.yaforster.trails.adapter.mysql.artifact;

import io.github.yaforster.trails.core.ArtifactType;
import io.github.yaforster.trails.core.persisted.PersistedArtifact;
import io.github.yaforster.trails.core.persisted.PersistedArtifactFile;
import org.springframework.stereotype.Component;

@Component
public class DownloadedFileEntityMapper {

    public PersistedArtifact toPersistedArtifact(DownloadedFileEntity file) {
        byte[] content = file.getContent();
        int contentLength = 0;
        if (content != null) {
            contentLength = content.length;
        }
        return new PersistedArtifact(file.getId(), file.getTestPathResultId(), ArtifactType.FILE, file.getFileName(),
                null, contentLength, null);
    }

    public PersistedArtifactFile toPersistedArtifactFile(DownloadedFileEntity file) {
        return new PersistedArtifactFile(file.getId(), file.getFileName(), null, file.getContent());
    }

}
