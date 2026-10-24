package io.github.yaforster.trails.adapter.mysql.artifact;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.app.services.ArtifactQueryService;
import io.github.yaforster.trails.app.services.DownloadedFilePersistence;
import io.github.yaforster.trails.core.persisted.PersistedArtifact;
import io.github.yaforster.trails.core.persisted.PersistedArtifactFile;
import io.github.yaforster.trails.core.test.FileData;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class DownloadedFileDatabaseServiceImplTest {

    private final DownloadedFileRepository repository = Mockito.mock(DownloadedFileRepository.class);
    private final DownloadedFileEntityMapper downloadedFileEntityMapper = Mockito.mock(
            DownloadedFileEntityMapper.class);
    private final DownloadedFileDatabaseServiceImpl service = new DownloadedFileDatabaseServiceImpl(repository,
            downloadedFileEntityMapper);

    @Test
    void listArtifacts_ShouldMapPage() {
        DownloadedFileEntity file = new DownloadedFileEntity().setId(1L);
        PersistedArtifact mapped = Mockito.mock(PersistedArtifact.class);
        when(repository.findByTestPathResultIdOrderByIdAsc(10L, PageRequest.of(0, 5))).thenReturn(
                new PageImpl<>(List.of(file), PageRequest.of(0, 5), 1));
        when(downloadedFileEntityMapper.toPersistedArtifact(file)).thenReturn(mapped);

        PagedResult<PersistedArtifact> result = service.listArtifacts(
                new ArtifactQueryService.PathArtifactPage(10L, 0, 5));

        assertEquals(1, result.getContent()
                              .size());
        assertEquals(mapped, result.getContent()
                                   .getFirst());
    }

    @Test
    void findArtifactInPath_ShouldMapOptional() {
        DownloadedFileEntity file = new DownloadedFileEntity().setId(2L);
        PersistedArtifactFile mapped = Mockito.mock(PersistedArtifactFile.class);
        when(repository.findByIdAndTestPathResultId(2L, 10L)).thenReturn(Optional.of(file));
        when(downloadedFileEntityMapper.toPersistedArtifactFile(file)).thenReturn(mapped);

        Optional<PersistedArtifactFile> result = service.findArtifactInPath(
                new ArtifactQueryService.PathArtifact(10L, 2L));

        assertTrue(result.isPresent());
        assertEquals(mapped, result.get());
    }

    @Test
    void store_shouldPersistDownloadedFileInPath() {
        FileData file = new FileData(new byte[]{1, 2, 3}, "report.txt");

        service.store(new DownloadedFilePersistence.DownloadedFile(10L, file));

        org.mockito.ArgumentCaptor<DownloadedFileEntity> captor = org.mockito.ArgumentCaptor.forClass(
                DownloadedFileEntity.class);
        Mockito.verify(repository)
               .save(captor.capture());
        assertEquals(10L, captor.getValue()
                                .getTestPathResultId());
        assertEquals("report.txt", captor.getValue()
                                         .getFileName());
        assertEquals(3, captor.getValue()
                              .getContent().length);
    }

}
