//import com.google.protobuf.gradle.generateProtoTasks
//import com.google.protobuf.gradle.id
//import com.google.protobuf.gradle.protobuf
//import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.plugin.ide.kotlinExtrasSerialization
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.9.22"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion

    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"

//    id("com.google.protobuf") version "0.8.18"
    id("kotlinx-serialization") version kotlinVersion

    id("com.github.ben-manes.versions") version "0.50.0"
}

group = "org.devshred"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.apache.tika:tika-core:2.9.1")

    implementation("io.jenetics:jpx:3.1.0")

//    implementation("com.google.protobuf:protobuf-kotlin:3.19.4")
//    implementation("com.google.protobuf:protobuf-java-util:3.19.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.2")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "mockito-core")
    }
    implementation("io.jenetics:jpx:3.1.0")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.apache.commons:commons-lang3:3.14.0")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    dependencyUpdates {
        resolutionStrategy {
            componentSelection {
                all {
                    val rejected = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea")
                        .map { qualifier -> Regex("(?i).*[.-]$qualifier[.\\d-+]*") }
                        .any { it.matches(candidate.version) }
                    if (rejected) {
                        reject("Release candidate")
                    }
                }
            }
        }
    }
}

//protobuf {
//    protoc {
//        artifact = "com.google.protobuf:protoc:3.19.4"
//    }
//    generateProtoTasks {
//        all().forEach {
//            it.builtins {
//                id("kotlin")
//            }
//        }
//    }
//}