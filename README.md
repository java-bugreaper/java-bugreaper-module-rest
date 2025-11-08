# BUGREAPER: module-minio

for interact with the MINIO

## [apiDOC](https://bug-reaper.gitlab.io/java-bugreaper-module-rest/)

### [EXAMPLES.md](EXAMPLES.md)

### Requirements:

    JAVA >= 17
    Allure server >= 2.15

### Logging:

    <logger name="bugreaper-module-api" level="INFO" />
    <logger name="bugreaper-module-mocks" level="INFO" />
    <logger name="MockEnchantedReport="INFO" />


### Tested with:

    mockserver/mockserver:5.13.2

## Real examples here:
- ### [Report-api](https://bug-reaper.gitlab.io/java-bugreaper-sandbox/#behaviors)
- ### [Tests](https://gitlab.com/bug-reaper/java-bugreaper-sandbox/-/tree/main/java-test-part1)

### Dependencies
| Lib                                                                                   | Version  | Description    |
|---------------------------------------------------------------------------------------|----------|----------------|
| [bugreaper-core](https://mvnrepository.com/artifact/io.gitlab.ambu550/bugreaper-core) | 0.0.8-rc | BugReaper CORE |
| [REST Assured](https://mvnrepository.com/artifact/io.rest-assured/rest-assured)       | 5.5.1    | API interact   |
