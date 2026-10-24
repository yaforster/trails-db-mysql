package io.github.yaforster.trails.adapter.mysql;

import io.github.yaforster.trails.sdk.composition.TrailsPersistenceRegistration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:mysql-adapter-registration;MODE=MySQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa",
        "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=none"})
class TrailsMysqlAutoConfigurationTest {

    @Autowired
    private TrailsPersistenceRegistration persistenceRegistration;

    @Test
    void shouldRegisterTheMysqlAdapterIdentifier() {
        assertThat(persistenceRegistration.adapterId()
                                          .value()).isEqualTo("mysql");
    }

    @Test
    void shouldRegisterEveryRequiredPersistencePort() {
        assertThat(persistenceRegistration.ports()).hasNoNullFieldsOrProperties();
    }

}
