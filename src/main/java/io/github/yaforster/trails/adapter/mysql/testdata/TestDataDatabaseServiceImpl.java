package io.github.yaforster.trails.adapter.mysql.testdata;

import io.github.yaforster.trails.adapter.mysql.SpringPageMapper;
import io.github.yaforster.trails.app.services.TestDataDatabaseService;
import io.github.yaforster.trails.core.PagedResult;
import io.github.yaforster.trails.core.data.TestDataSet;
import io.github.yaforster.trails.core.definition.TestDataSetDefinition;
import io.github.yaforster.trails.core.deletion.DatabaseDeletionResult;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@AllArgsConstructor
@Service
public class TestDataDatabaseServiceImpl implements TestDataDatabaseService {

    private final TestDataRepository repository;
    private final TestDataEntityMapper mapper;

    @Override
    @Transactional
    public TestDataSet storeTestData(TestDataSetDefinition definition) {
        TestDataSetEntity entity = mapper.toEntity(definition);
        TestDataSetEntity stored = repository.save(entity);
        return mapper.toDomain(stored);
    }

    @Override
    @Transactional
    public Optional<TestDataSet> updateTestData(TestDataUpdate testDataUpdate) {
        return repository.findById(testDataUpdate.testDataId())
                         .map(entity -> {
                             mapper.updateEntity(entity, testDataUpdate.definition());
                             return repository.save(entity);
                         })
                         .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestDataSet> getTestData(Long id) {
        TestDataDetails activeTestData = new TestDataDetails(id, false);
        return getTestData(activeTestData);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TestDataSet> getTestData(TestDataDetails testDataDetails) {
        Optional<TestDataSetEntity> testData;
        if (testDataDetails.includeRetired()) {
            testData = repository.findById(testDataDetails.testDataId());
        } else {
            testData = repository.findByIdAndRetiredFalse(testDataDetails.testDataId());
        }
        return testData.map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResult<TestDataSet> listTestData(TestDataPage testDataPage) {
        PageRequest pageRequest = PageRequest.of(testDataPage.page(), testDataPage.size());
        Page<TestDataSetEntity> testData;
        if (testDataPage.includeRetired()) {
            testData = repository.findAll(pageRequest);
        } else {
            testData = repository.findAllByRetiredFalse(pageRequest);
        }
        Page<TestDataSet> testDataSets = testData.map(mapper::toDomain);
        return SpringPageMapper.toPagedResult(testDataSets);
    }

    @Override
    @Transactional
    public DatabaseDeletionResult deleteTestData(Long id) {
        return setRetired(id, true);
    }

    @Override
    @Transactional
    public DatabaseDeletionResult restoreTestData(Long id) {
        return setRetired(id, false);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getValue(TestDataValue testDataValue) {
        return repository.findByIdAndRetiredFalse(testDataValue.testDataId())
                         .flatMap(testDataSet -> testDataSet.getValues()
                                                            .stream()
                                                            .filter(value -> value.getKey()
                                                                                  .equals(testDataValue.key()))
                                                            .map(TestDataEntryEntity::getValue)
                                                            .findFirst());
    }

    private DatabaseDeletionResult setRetired(Long id, boolean retired) {
        Optional<TestDataSetEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {
            return new DeletionNotFound(id);
        }
        try {
            TestDataSetEntity testDataSet = entity.get();
            testDataSet.setRetired(retired);
            repository.save(testDataSet);
            return new DeletionSuccess(id);
        } catch (Exception exception) {
            return new DeletionFailure(id, exception);
        }
    }

}
