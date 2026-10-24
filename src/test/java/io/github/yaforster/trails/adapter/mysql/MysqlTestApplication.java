package io.github.yaforster.trails.adapter.mysql;

import io.github.yaforster.trails.sdk.autoconfigure.TrailsSdkAutoConfiguration;
import io.github.yaforster.trails.app.services.TestDataDatabaseService;
import io.github.yaforster.trails.app.services.TestDataValueResolutionService;
import io.github.yaforster.trails.sdk.host.testdata.TestDataValueResolver;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableAutoConfiguration(exclude = TrailsSdkAutoConfiguration.class)
class MysqlTestApplication {

    @Bean
    TestDataValueResolutionService testDataValueResolutionService(TestDataDatabaseService testDataDatabaseService) {
        return new TestDataValueResolver(testDataDatabaseService);
    }

}
