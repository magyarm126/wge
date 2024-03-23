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
    compileOnly("com.google.protobuf:protobuf-java:4.26.0-RC3")
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:4.26.0-RC3"
    }
}

micronaut {
    version("4.3.4")
}