# Trails MySQL Adapter repository map

```text
adapter/trails-db-mysql/
|-- pom.xml                                Published MySQL persistence adapter artifact
|-- format/java-formatter.xml              SDK-aligned Java formatting rules
|-- src/
|   |-- main/java/.../adapter/mysql/
|   |   |-- {application,artifact,deployment,stage,testdata,user}/ Feature-owned persistence implementations
|   |   |-- element/screenshot/            Element persistence and transactional promotion copying
|   |   |-- result/{action,run,testpath,testset}/ Persisted execution-result hierarchy
|   |   |-- testplan/                       Aggregate services, entities, mappers, promotion, and repositories
|   |   |   |-- TestPlanCopy.java          Source-to-copy identities for post-persist action links
|   |   |   `-- mapper/detail/ActionEntityMapperRegistry.java Cached variant selection and registration validation
|   |   `-- shared/                        Shared JPA helpers
|   |-- main/resources/
|   |   |-- db/changelog/                  Liquibase schema
|   |   `-- META-INF/spring/               Boot auto-configuration import
|   `-- test/java/.../adapter/mysql/       Persistence, Liquibase, mapping, and registration tests
|-- README.md                              Customer configuration guidance
`-- REPOMAP.md                             This map
```

This repository owns the complete atomic MySQL persistence registration. The SDK must not import this adapter package.
