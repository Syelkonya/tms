plugins {
	java
	id("org.springframework.boot") version "4.0.1"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "su.ternovskii"
version = "0.0.1-SNAPSHOT"
description = "TMS project for Spring Boot"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(25)
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
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	// Redis
	implementation("org.springframework.boot:spring-boot-starter-data-redis")

	// Kafka
	implementation("org.springframework.kafka:spring-kafka")

	// Kafka для event-driven архитектуры
	implementation("org.springframework.kafka:spring-kafka")
	testImplementation("org.springframework.kafka:spring-kafka-test")

	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	runtimeOnly("org.postgresql:postgresql:42.7.7")

	implementation("jakarta.validation:jakarta.validation-api:4.0.0-M1")

	// Email
	implementation("org.springframework.boot:spring-boot-starter-mail")

	// Мониторинг и метрики
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")

	// TestContainers для интеграционных тестов
	testImplementation("org.testcontainers:testcontainers:2.0.3")
	testImplementation("org.testcontainers:postgresql:1.21.4")
	testImplementation("org.testcontainers:junit-jupiter:1.21.4")

	// AssertJ для удобных assertions в тестах
	testImplementation("org.assertj:assertj-core:3.24.2")

	// Actuator + Prometheus
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
