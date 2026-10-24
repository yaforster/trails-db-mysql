package io.github.yaforster.trails.adapter.mysql;

import io.github.yaforster.trails.app.services.ActionQueryService;
import io.github.yaforster.trails.app.services.ActionResultPersistence;
import io.github.yaforster.trails.app.services.ActionResultQueryService;
import io.github.yaforster.trails.app.services.ActionResultScreenshotQueryService;
import io.github.yaforster.trails.app.services.ApplicationDatabaseService;
import io.github.yaforster.trails.app.services.ArtifactQueryService;
import io.github.yaforster.trails.app.services.DeploymentDatabaseService;
import io.github.yaforster.trails.app.services.DownloadedFilePersistence;
import io.github.yaforster.trails.app.services.ElementDatabaseService;
import io.github.yaforster.trails.app.services.ScreenshotDatabaseService;
import io.github.yaforster.trails.app.services.StageDatabaseService;
import io.github.yaforster.trails.app.services.TestDataDatabaseService;
import io.github.yaforster.trails.app.services.TestPathResultPersistence;
import io.github.yaforster.trails.app.services.TestPathResultQueryService;
import io.github.yaforster.trails.app.services.TestPlanDatabaseService;
import io.github.yaforster.trails.app.services.TestRunResultDatabaseService;
import io.github.yaforster.trails.app.services.TestSetResultPersistence;
import io.github.yaforster.trails.app.services.TestSetResultQueryService;
import io.github.yaforster.trails.app.services.UserProfileService;
import io.github.yaforster.trails.sdk.composition.AdapterId;
import io.github.yaforster.trails.sdk.composition.TrailsPersistenceRegistration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/** Configures the complete MySQL-backed Trails persistence capability. */
@AutoConfiguration
@EntityScan(basePackageClasses = TrailsMysqlAutoConfiguration.class)
@EnableJpaRepositories(basePackageClasses = TrailsMysqlAutoConfiguration.class)
@ComponentScan(basePackageClasses = TrailsMysqlAutoConfiguration.class)
public class TrailsMysqlAutoConfiguration {

    @Bean
    TrailsPersistenceRegistration trailsMysqlPersistenceRegistration(
            ApplicationDatabaseService applicationDatabaseService, DeploymentDatabaseService deploymentDatabaseService,
            StageDatabaseService stageDatabaseService, ElementDatabaseService elementDatabaseService,
            ScreenshotDatabaseService screenshotDatabaseService, TestDataDatabaseService testDataDatabaseService,
            TestPlanDatabaseService testPlanDatabaseService, ActionQueryService actionQueryService,
            TestRunResultDatabaseService testRunResultDatabaseService,
            TestSetResultQueryService testSetResultQueryService, TestSetResultPersistence testSetResultPersistence,
            TestPathResultQueryService testPathResultQueryService, TestPathResultPersistence testPathResultPersistence,
            ActionResultQueryService actionResultQueryService, ActionResultPersistence actionResultPersistence,
            ActionResultScreenshotQueryService actionResultScreenshotQueryService,
            ArtifactQueryService artifactQueryService, DownloadedFilePersistence downloadedFilePersistence,
            UserProfileService userProfileService) {
        TrailsPersistenceRegistration.Ports ports = new TrailsPersistenceRegistration.Ports(applicationDatabaseService,
                deploymentDatabaseService, stageDatabaseService, elementDatabaseService, screenshotDatabaseService,
                testDataDatabaseService, testPlanDatabaseService, actionQueryService, testRunResultDatabaseService,
                testSetResultQueryService, testSetResultPersistence, testPathResultQueryService,
                testPathResultPersistence, actionResultQueryService, actionResultPersistence,
                actionResultScreenshotQueryService, artifactQueryService, downloadedFilePersistence,
                userProfileService);
        return new TrailsPersistenceRegistration(new AdapterId("mysql"), ports);
    }

}
