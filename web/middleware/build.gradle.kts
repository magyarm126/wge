plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
    id("io.micronaut.library")
    id("org.jetbrains.dokka")
}

version = "0.1"
group = "hu.matemagyar.wge"

repositories {
    mavenCentral()
}

dependencies {
    ksp("io.micronaut.serde:micronaut-serde-processor")
    compileOnly("io.micronaut.serde:micronaut-serde-api")
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
}