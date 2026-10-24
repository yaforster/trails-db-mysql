package io.github.yaforster.trails.adapter.mysql.result.action;

import io.github.yaforster.trails.app.services.ActionResultPersistence;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class ActionResultPersistenceServiceImpl implements ActionResultPersistence {

    private final ResultRepository resultRepository;
    private final ResultScreenshotRepository resultScreenshotRepository;
    private final ResultScreenshotFactory resultScreenshotFactory;
    private final ResultEntityMapper resultEntityMapper;

    @Override
    @Transactional
    public void store(ActionResult actionResult) {
        ResultEntity entity = resultEntityMapper.toEntity(actionResult.testPathResultId(),
                actionResult.executionOrder(), actionResult.result());
        ResultEntity storedResult = resultRepository.save(entity);
        String base64Screenshot = actionResult.result()
                                              .getBase64Screenshot();
        resultScreenshotFactory.fromBase64(storedResult.getId(), base64Screenshot)
                               .ifPresent(resultScreenshotRepository::save);
    }

}
