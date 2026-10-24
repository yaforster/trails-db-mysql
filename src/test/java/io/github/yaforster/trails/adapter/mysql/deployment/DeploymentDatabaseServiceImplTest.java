package io.github.yaforster.trails.adapter.mysql.deployment;

import io.github.yaforster.trails.app.services.DeploymentDatabaseService.StageDeployment;
import io.github.yaforster.trails.app.services.StageDatabaseService;
import io.github.yaforster.trails.app.services.StageDatabaseService.StageDetails;
import io.github.yaforster.trails.core.TrailsTest;
import io.github.yaforster.trails.core.definition.DeploymentDefinition;
import io.github.yaforster.trails.core.persisted.PersistedDeployment;
import io.github.yaforster.trails.core.persisted.PersistedStage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DeploymentDatabaseServiceImplTest extends TrailsTest {

    private final DeploymentRepository deploymentRepository = mock(DeploymentRepository.class);
    private final StageDatabaseService stageDatabaseService = mock(StageDatabaseService.class);
    private final DeploymentDatabaseServiceImpl service = new DeploymentDatabaseServiceImpl(deploymentRepository,
            stageDatabaseService, new DeploymentEntityMapper());

    @Test
    void storeDeployment_shouldReturnEmpty_whenStageDoesNotBelongToApplication() {
        when(stageDatabaseService.getStage(new StageDetails(7L, 9L, true))).thenReturn(Optional.empty());
        DeploymentDefinition definition = new DeploymentDefinition("1.2.3", Instant.now());

        Optional<PersistedDeployment> result = service.storeDeployment(new StageDeployment(7L, 9L, definition));

        assertTrue(result.isEmpty());
        verify(deploymentRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void storeDeployment_shouldPersistDeploymentForApplicationStage() {
        Instant deployedAt = Instant.parse("2026-05-24T12:15:30Z");
        when(stageDatabaseService.getStage(new StageDetails(7L, 9L, true))).thenReturn(
                Optional.of(new PersistedStage(9L, 7L, "stage", "https://example.org", false)));
        when(deploymentRepository.save(org.mockito.ArgumentMatchers.any(DeploymentEntity.class))).thenAnswer(
                invocation -> invocation.<DeploymentEntity>getArgument(0)
                                        .setId(55L));

        Optional<PersistedDeployment> result = service.storeDeployment(
                new StageDeployment(7L, 9L, new DeploymentDefinition("1.2.3", deployedAt)));

        assertTrue(result.isPresent());
        assertEquals(55L, result.get()
                                .id());
        assertEquals(7L, result.get()
                               .applicationId());
        assertEquals(9L, result.get()
                               .stageId());
        assertEquals("1.2.3", result.get()
                                    .version());
        assertEquals(deployedAt, result.get()
                                       .deployedAt());

        ArgumentCaptor<DeploymentEntity> captor = ArgumentCaptor.forClass(DeploymentEntity.class);
        verify(deploymentRepository).save(captor.capture());
        assertEquals(7L, captor.getValue()
                               .getApplicationId());
        assertEquals(9L, captor.getValue()
                               .getStageId());
        assertEquals("1.2.3", captor.getValue()
                                    .getVersion());
        assertEquals(deployedAt, captor.getValue()
                                       .getDeployedAt()
                                       .toInstant());
    }

}
