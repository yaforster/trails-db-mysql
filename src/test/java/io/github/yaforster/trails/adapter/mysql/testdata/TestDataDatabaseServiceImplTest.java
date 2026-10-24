package io.github.yaforster.trails.adapter.mysql.testdata;

import io.github.yaforster.trails.app.services.TestDataDatabaseService;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class TestDataDatabaseServiceImplTest {

    @Test
    void getValue_shouldReturnMatchingValue_whenTestDataIsActive() {
        TestDataRepository repository = Mockito.mock(TestDataRepository.class);
        TestDataSetEntity testDataSet = new TestDataSetEntity().setValues(
                List.of(new TestDataEntryEntity().setKey("username")
                                                 .setValue("alice")));
        when(repository.findByIdAndRetiredFalse(4L)).thenReturn(Optional.of(testDataSet));
        TestDataDatabaseServiceImpl service = new TestDataDatabaseServiceImpl(repository,
                Mockito.mock(TestDataEntityMapper.class));

        Optional<String> result = service.getValue(new TestDataDatabaseService.TestDataValue(4L, "username"));

        assertThat(result).contains("alice");
    }

    @Test
    void getValue_shouldReturnEmpty_whenTestDataIsMissingOrRetired() {
        TestDataRepository repository = Mockito.mock(TestDataRepository.class);
        when(repository.findByIdAndRetiredFalse(4L)).thenReturn(Optional.empty());
        TestDataDatabaseServiceImpl service = new TestDataDatabaseServiceImpl(repository,
                Mockito.mock(TestDataEntityMapper.class));

        Optional<String> result = service.getValue(new TestDataDatabaseService.TestDataValue(4L, "username"));

        assertThat(result).isEmpty();
    }

    @Test
    void deleteTestData_shouldReturnNotFound_whenTestDataDoesNotExist() {
        TestDataRepository repository = Mockito.mock(TestDataRepository.class);
        when(repository.findById(4L)).thenReturn(Optional.empty());
        TestDataDatabaseServiceImpl service = new TestDataDatabaseServiceImpl(repository,
                Mockito.mock(TestDataEntityMapper.class));

        assertThat(service.deleteTestData(4L)).isInstanceOf(DeletionNotFound.class);
    }

    @Test
    void restoreTestData_shouldMarkExistingTestDataActive() {
        TestDataRepository repository = Mockito.mock(TestDataRepository.class);
        TestDataSetEntity testDataSet = new TestDataSetEntity().setRetired(true);
        when(repository.findById(4L)).thenReturn(Optional.of(testDataSet));
        TestDataDatabaseServiceImpl service = new TestDataDatabaseServiceImpl(repository,
                Mockito.mock(TestDataEntityMapper.class));

        assertThat(service.restoreTestData(4L)).isInstanceOf(DeletionSuccess.class);
    }

}
