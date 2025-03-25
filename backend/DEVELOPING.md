# Developing

## Prerequisites

- Java 23
- Docker

## Run / Start

```bash
./gradlew bootRun
```

## Test

```bash
./gradlew test
```

## Formatting

Check formatting

```bash
./gradlew spotlessCheck
```

Fix formatting if possible

```bash
./gradlew spotlessApply
```

## Style Check

Check src

```bash
./gradlew checkstyleMain
```

Check tests

```bash
./gradlew checkstyleTest
```
