package io.github.yaforster.trails.adapter.mysql.application;

import io.github.yaforster.trails.core.PagedResult;

import io.github.yaforster.trails.app.services.ApplicationDatabaseService;
import io.github.yaforster.trails.core.definition.ApplicationDefinition;
import io.github.yaforster.trails.core.deletion.DeletionFailure;
import io.github.yaforster.trails.core.deletion.DeletionNotFound;
import io.github.yaforster.trails.core.deletion.DeletionSuccess;
import io.github.yaforster.trails.core.persisted.PersistedApplication;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class ApplicationDatabaseServiceImplTest {

    private final ApplicationRepository applicationRepository = Mockito.mock(ApplicationRepository.class);
    private final ApplicationEntityMapper applicationEntityMapper = Mockito.mock(ApplicationEntityMapper.class);
    private final ApplicationDatabaseServiceImpl service = new ApplicationDatabaseServiceImpl(applicationRepository,
            applicationEntityMapper);

    @Test
    void storeApplication_ShouldDelegateToMapperAndRepository() {
        ApplicationDefinition definition = new ApplicationDefinition("label");
        ApplicationEntity entity = new ApplicationEntity().setLabel("label");
        ApplicationEntity saved = new ApplicationEntity().setId(10L)
                                                         .setLabel("label");
        PersistedApplication persisted = new PersistedApplication(10L, "label");
        when(applicationEntityMapper.toEntity(definition)).thenReturn(entity);
        when(applicationRepository.save(entity)).thenReturn(saved);
        when(applicationEntityMapper.toPersisted(saved)).thenReturn(persisted);

        PersistedApplication result = service.storeApplication(definition);

        assertEquals(persisted, result);
    }

    @Test
    void getApplication_ShouldMapOptional() {
        ApplicationEntity entity = new ApplicationEntity().setId(1L)
                                                          .setLabel("a");
        PersistedApplication persisted = new PersistedApplication(1L, "a");
        when(applicationRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(applicationEntityMapper.toPersisted(entity)).thenReturn(persisted);

        Optional<PersistedApplication> result = service.getApplication(
                new ApplicationDatabaseService.ApplicationDetails(1L, true));

        assertTrue(result.isPresent());
        assertEquals(persisted, result.get());
    }

    @Test
    void getApplications_ShouldMapPage() {
        ApplicationEntity entity = new ApplicationEntity().setId(2L)
                                                          .setLabel("b");
        PersistedApplication persisted = new PersistedApplication(2L, "b");
        when(applicationRepository.findAllByRetiredFalse(PageRequest.of(0, 5))).thenReturn(
                new PageImpl<>(List.of(entity), PageRequest.of(0, 5), 1));
        when(applicationEntityMapper.toPersisted(entity)).thenReturn(persisted);

        PagedResult<PersistedApplication> result = service.getApplications(
                new ApplicationDatabaseService.ApplicationPage(0, 5, false));

        assertEquals(1, result.getContent()
                              .size());
        assertEquals(persisted, result.getContent()
                                      .getFirst());
    }

    @Test
    void deleteApplication_ShouldReturnNotFound_WhenEntityMissing() {
        when(applicationRepository.findById(9L)).thenReturn(Optional.empty());

        assertTrue(service.deleteApplication(9L) instanceof DeletionNotFound);
    }

    @Test
    void deleteApplication_ShouldReturnSuccess_WhenRetireWorks() {
        ApplicationEntity entity = new ApplicationEntity().setId(9L)
                                                          .setLabel("app");
        when(applicationRepository.findById(9L)).thenReturn(Optional.of(entity));
        when(applicationRepository.save(entity)).thenReturn(entity);

        assertTrue(service.deleteApplication(9L) instanceof DeletionSuccess);
        assertTrue(entity.isRetired());
    }

    @Test
    void deleteApplication_ShouldReturnFailure_WhenRetireThrows() {
        ApplicationEntity entity = new ApplicationEntity().setId(9L)
                                                          .setLabel("app");
        when(applicationRepository.findById(9L)).thenReturn(Optional.of(entity));
        doThrow(new RuntimeException("boom")).when(applicationRepository)
                                             .save(entity);

        assertTrue(service.deleteApplication(9L) instanceof DeletionFailure);
    }

    @Test
    void restoreApplication_ShouldReturnSuccess_WhenRestoreWorks() {
        ApplicationEntity entity = new ApplicationEntity().setId(9L)
                                                          .setLabel("app")
                                                          .setRetired(true);
        when(applicationRepository.findById(9L)).thenReturn(Optional.of(entity));
        when(applicationRepository.save(entity)).thenReturn(entity);

        assertTrue(service.restoreApplication(9L) instanceof DeletionSuccess);
        assertTrue(!entity.isRetired());
    }

}
