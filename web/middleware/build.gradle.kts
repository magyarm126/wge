import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.remove

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
    id("io.micronaut.minimal.library")
    id("org.jetbrains.dokka")
    id("com.google.protobuf") version "0.9.4"
}

version = "0.1"
group = "hu.matemagyar.wge"

dependencies {
    ksp("io.micronaut.serde:micronaut-serde-processor")
    compileOnly("io.micronaut.serde:micronaut-serde-api")

    protobuf(files("../../common/protobuf"))
    implementation("com.google.protobuf:protobuf-lite:3.0.0")
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
}

protobuf {
    protoc {
        // You still need protoc like in the non-Android case
        artifact = "com.google.protobuf:protoc:3.7.0"
    }
    plugins {
        id("javalite") {
            artifact = "com.google.protobuf:protoc-gen-javalite:3.0.0"
        }
    }
    generateProtoTasks {
        all().configureEach {
            builtins {
                // In most cases you don't need the full Java output
                // if you use the lite output.
                remove("java")
            }
            plugins {
                id("javalite") {}
            }
        }
    }
}