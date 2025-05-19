import com.google.protobuf.gradle.*

group = "dev.cgj"
version = "1.0-SNAPSHOT"

plugins {
    id("java")
    id("application")
    id("io.freefair.lombok") version "8.13"

    // Compile *.proto files during build
    // https://github.com/google/protobuf-gradle-plugin
    id("com.google.protobuf") version "0.9.5"
}

repositories {
    mavenCentral()
}

application {
    mainClass.set("dev.cgj.nbody2d.NBody2dLauncher")
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
    runtimeOnly("ch.qos.logback:logback-classic:1.5.13")

    // Unit tests
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation(platform("org.mockito:mockito-bom:5.17.0"))
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")

    // Protocol Buffers
    implementation("com.google.protobuf:protobuf-java:4.30.2")
    implementation("io.grpc:grpc-stub:1.71.0")
    implementation("io.grpc:grpc-protobuf:1.71.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.30.2"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.71.0"
        }
    }
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") { }
            }
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
    jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes["Main-Class"] = "dev.cgj.nbody2d.NBody2dLauncher"
        }
        from({
            configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
        })
    }
}
