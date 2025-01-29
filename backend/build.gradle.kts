plugins {
	java
	id("org.springframework.boot") version "3.4.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
	id("org.sonarqube") version "6.0.1.5171"
}

group = "de.bund.digitalservice"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(23)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")
	implementation("org.springframework.session:spring-session-core")
	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-docker-compose")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:postgresql")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestReport {
    //    dependsOn(tasks.test)

    // Jacoco hooks into all tasks of type: Test automatically, but results for each of these
    // tasks are kept separately and are not combined out of the box. we want to gather
    // coverage of our unit and integration tests as a single report!
    executionData.setFrom(
        files(
            fileTree(project.layout.buildDirectory) {
                include("jacoco/*.exec")
            },
        ),
    )
    reports {
        xml.required = true
        html.required = true
    }
    dependsOn("test") // All tests are required to run before generating a report.
}

tasks.getByName("sonar") {
    dependsOn("jacocoTestReport")
}

sonar {
    // NOTE: sonarqube picks up combined coverage correctly without further configuration from:
    // build/reports/jacoco/test/jacocoTestReport.xml
    properties {
        property("sonar.projectKey", "digitalservicebund_ris-adm-vwv")
        property("sonar.organization", "digitalservicebund")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.token", System.getenv("SONAR_TOKEN"))
    }
}