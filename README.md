# Trails MySQL Adapter

`io.github.yaforster:trails-db-mysql` supplies the complete Trails persistence capability: all 19 SDK persistence
ports, JPA entities and repositories, and its Liquibase schema. Its Boot autoconfiguration contributes exactly
one `TrailsPersistenceRegistration` with adapter ID `mysql`; it never exposes a partial registration.

## Customer wrapper

Import the Trails BOM, then include the SDK and this adapter in a Spring Boot wrapper:

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.yaforster</groupId>
            <artifactId>trails-bom</artifactId>
            <version>0.0.1</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Configure its database using Spring Boot's standard datasource settings.
This adapter uses Liquibase which reads the adapter's `db/changelog/db.changelog-master.yaml` automatically;

```yaml
spring:
  datasource:
    url: jdbc:mysql:[...]
    username: ${TRAILS_DATABASE_USERNAME}
    password: ${TRAILS_DATABASE_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
```
The schema initializes an empty database; it does not perform data migration.