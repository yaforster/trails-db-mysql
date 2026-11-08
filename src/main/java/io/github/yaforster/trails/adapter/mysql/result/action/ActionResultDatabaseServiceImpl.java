package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.app.services.ActionResultQueryService;
import io.github.yaforster.trails.app.services.ActionResultScreenshotQueryService;
import io.github.yaforster.trails.app.services.TestPathResultQueryService;
import io.github.yaforster.trails.core.persisted.PersistedActionResult;
import io.github.yaforster.trails.core.persisted.PersistedArtifactFile;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ActionResultDatabaseServiceImpl implements ActionResultQueryService, ActionResultScreenshotQueryService {

    private final TestPathResultQueryService testPathResultQueryService;
    private final ResultRepository resultRepository;
    private final PersistedActionResultMapper persistedActionResultMapper;
    private final ResultScreenshotRepository resultScreenshotRepository;

    @Override
    public Set<Long> findActionResultIdsInPath(Long pathResultId) {
        return Set.copyOf(resultScreenshotRepository.findActionResultIdsInPath(pathResultId));
    }

    @Override
    public Optional<List<PersistedActionResult>> listActionResultChain(Long pathResultId) {
        if (testPathResultQueryService.getPathResult(pathResultId)
                                      .isEmpty()) {
            return Optional.empty();
        }
        List<PersistedActionResult> orderedResults = resultRepository.findByTestPathResultIdOrderByExecutionOrderAscIdAsc(
                pathResultId)
                                                                     .stream()
                                                                     .map(persistedActionResultMapper::fromEntity)
                                                                     .toList();
        return Optional.of(orderedResults);
    }

    @Override
    public Map<Long, List<PersistedActionResult>> listActionResultChains(ActionResultChains actionResultChains) {
        Map<Long, List<PersistedActionResult>> mutableResultsByPathId = new LinkedHashMap<>();
        actionResultChains.pathResultIds()
                          .forEach(pathResultId -> mutableResultsByPathId.put(pathResultId, new ArrayList<>()));
        if (mutableResultsByPathId.isEmpty()) {
            return Map.of();
        }
        resultRepository.findByTestPathResultIdInOrderByTestPathResultIdAscExecutionOrderAscIdAsc(
                mutableResultsByPathId.keySet())
                        .stream()
                        .map(persistedActionResultMapper::fromEntity)
                        .forEach(result -> mutableResultsByPathId.get(result.testPathResultId())
                                                                 .add(result));
        Map<Long, List<PersistedActionResult>> resultsByPathId = new LinkedHashMap<>();
        mutableResultsByPathId.forEach(
                (pathResultId, results) -> resultsByPathId.put(pathResultId, List.copyOf(results)));
        return resultsByPathId;
    }

    @Override
    public Optional<PersistedArtifactFile> findInPath(PathActionScreenshot request) {
        return resultRepository.findByIdAndTestPathResultId(request.actionResultId(), request.pathResultId())
                               .flatMap(resultEntity -> resultScreenshotRepository.findByResultId(resultEntity.getId()))
                               .map(screenshotEntity -> toPersistedFile(request.actionResultId(), screenshotEntity));
    }

    @Override
    public Map<Long, PersistedArtifactFile> findAll(ActionScreenshots request) {
        if (request.actionResultIds()
                   .isEmpty()) {
            return Map.of();
        }
        return resultScreenshotRepository.findByResultIdIn(request.actionResultIds())
                                         .stream()
                                         .map(screenshotEntity -> toPersistedFile(screenshotEntity.getResultId(),
                                                 screenshotEntity))
                                         .collect(Collectors.toMap(PersistedArtifactFile::id, Function.identity()));
    }

    private PersistedArtifactFile toPersistedFile(Long resultId, ResultScreenshotEntity screenshotEntity) {
        String contentType = normalizeContentType(screenshotEntity.getContentType());
        return new PersistedArtifactFile(resultId, screenshotEntity.getFileName(), contentType,
                screenshotEntity.getContent());
    }

    private String normalizeContentType(String contentType) {
        // Older screenshot rows predate MIME metadata and contain PNG images.
        if (contentType == null || contentType.isBlank()) {
            return "image/png";
        }
        return contentType;
    }

}
