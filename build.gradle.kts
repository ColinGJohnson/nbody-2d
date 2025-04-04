plugins {
    id("java")
    id("io.freefair.lombok") version "8.13"
    id("application")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    // Command-line argument parsing
    implementation("info.picocli:picocli:4.7.1")
    annotationProcessor("info.picocli:picocli-codegen:4.7.1")

    // Config file deserialization
    implementation("com.fasterxml.jackson.core:jackson-databind:2.18.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.18.3")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.9")
    runtimeOnly("ch.qos.logback:logback-classic:1.4.12")

    // Unit tests
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
