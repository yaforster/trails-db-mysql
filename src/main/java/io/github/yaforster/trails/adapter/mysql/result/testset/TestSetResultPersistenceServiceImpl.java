package io.github.yaforster.trails.adapter.mysql.result.testset;

import io.github.yaforster.trails.app.services.TestSetResultPersistence;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class TestSetResultPersistenceServiceImpl implements TestSetResultPersistence {

    private final TestSetResultRepository testSetResultRepository;
    private final TestSetResultEntityMapper testSetResultEntityMapper;

    @Override
    @Transactional
    public Long store(TestSetResultToStore testSetResult) {
        TestSetResultEntity entity = testSetResultEntityMapper.toEntity(testSetResult.testRunId(),
                testSetResult.testSetResult());
        TestSetResultEntity storedResult = testSetResultRepository.save(entity);
        return storedResult.getId();
    }

}
