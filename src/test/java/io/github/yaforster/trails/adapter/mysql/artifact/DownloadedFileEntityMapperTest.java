package io.github.yaforster.trails.adapter.mysql.artifact;

import io.github.yaforster.trails.core.ArtifactType;
import io.github.yaforster.trails.core.persisted.PersistedArtifact;
import io.github.yaforster.trails.core.persisted.PersistedArtifactFile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DownloadedFileEntityMapperTest {

    private final DownloadedFileEntityMapper mapper = new DownloadedFileEntityMapper();

    @Test
    void toPersistedArtifact_ShouldMapFileEntityWithContent() {
        DownloadedFileEntity file = new DownloadedFileEntity().setId(4L)
                                                              .setTestPathResultId(5L)
                                                              .setFileName("report.txt")
                                                              .setContent(new byte[]{1, 2, 3});

        PersistedArtifact result = mapper.toPersistedArtifact(file);

        assertEquals(4L, result.id());
        assertEquals(5L, result.actionResultId());
        assertEquals(ArtifactType.FILE, result.type());
        assertEquals("report.txt", result.filename());
        assertNull(result.contentType());
        assertEquals(3L, result.size());
        assertNull(result.storageKey());
    }

    @Test
    void toPersistedArtifact_ShouldMapZeroSizeForNullContent() {
        DownloadedFileEntity file = new DownloadedFileEntity().setId(6L)
                                                              .setTestPathResultId(7L)
                                                              .setFileName("empty.txt")
                                                              .setContent(null);

        PersistedArtifact result = mapper.toPersistedArtifact(file);

        assertEquals(0L, result.size());
    }

    @Test
    void toPersistedArtifactFile_ShouldMapFileEntity() {
        byte[] content = new byte[]{7, 8, 9};
        DownloadedFileEntity file = new DownloadedFileEntity().setId(8L)
                                                              .setFileName("artifact.bin")
                                                              .setContent(content);

        PersistedArtifactFile result = mapper.toPersistedArtifactFile(file);

        assertEquals(8L, result.id());
        assertEquals("artifact.bin", result.fileName());
        assertNull(result.contentType());
        assertArrayEquals(content, result.content());
    }

}
