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

### Test Coverage Reports

```bash
./gradlew jacocoTestReport
```

The report (overviews as well as line-by-line details) can be found in `backend/build/reports/jacoco/test/html/index.html`

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
