import com.google.protobuf.gradle.id

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
    compileOnly("io.micronaut.grpc:micronaut-grpc-runtime") //Can we replace this with?
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.6.1"
    }

    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.62.2"
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