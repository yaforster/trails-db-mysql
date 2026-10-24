package io.github.yaforster.trails.adapter.mysql.artifact;

import io.github.yaforster.trails.adapter.mysql.SpringPageMapper;
import io.github.yaforster.trails.app.services.ArtifactQueryService;
import io.github.yaforster.trails.app.services.DownloadedFilePersistence;
import io.github.yaforster.trails.core.PagedResult;
import io.github.yaforster.trails.core.persisted.PersistedArtifact;
import io.github.yaforster.trails.core.persisted.PersistedArtifactFile;
import io.github.yaforster.trails.core.test.FileData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class DownloadedFileDatabaseServiceImpl implements ArtifactQueryService, DownloadedFilePersistence {

    private final DownloadedFileRepository repository;
    private final DownloadedFileEntityMapper downloadedFileEntityMapper;

    @Override
    public PagedResult<PersistedArtifact> listArtifacts(PathArtifactPage pathArtifactPage) {
        Pageable pageable = PageRequest.of(pathArtifactPage.page(), pathArtifactPage.size());
        Page<DownloadedFileEntity> files = repository.findByTestPathResultIdOrderByIdAsc(
                pathArtifactPage.testPathResultId(), pageable);
        Page<PersistedArtifact> artifacts = files.map(downloadedFileEntityMapper::toPersistedArtifact);
        return SpringPageMapper.toPagedResult(artifacts);
    }

    @Override
    public Optional<PersistedArtifactFile> findArtifactInPath(PathArtifact pathArtifact) {
        return repository.findByIdAndTestPathResultId(pathArtifact.artifactId(), pathArtifact.pathResultId())
                         .map(downloadedFileEntityMapper::toPersistedArtifactFile);
    }

    @Override
    public void store(DownloadedFile downloadedFile) {
        FileData file = downloadedFile.file();
        DownloadedFileEntity fileEntity = new DownloadedFileEntity().setTestPathResultId(
                downloadedFile.testPathResultId())
                                                                    .setFileName(file.fileName())
                                                                    .setContent(file.content());
        repository.save(fileEntity);
    }

}
