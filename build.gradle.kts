import com.github.gradle.node.npm.task.NpxTask

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.20"
    id("org.jetbrains.kotlin.plugin.allopen") version "1.9.20"
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.2.0"
    id("io.micronaut.aot") version "4.2.0"
    id("java")
    id("com.github.node-gradle.node") version "3.0.1"
}

version = "0.1"
group = "hu.matemagyar.wge"

val kotlinVersion= project.properties["kotlinVersion"]
repositories {
    mavenCentral()
}

dependencies {
    ksp("io.micronaut:micronaut-http-validation")
    ksp("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.kotlin:micronaut-kotlin-runtime")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${kotlinVersion}")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass.set("hu.matemagyar.wge.ApplicationKt")
}
java {
    sourceCompatibility = JavaVersion.toVersion("20")
}


graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("junit5")
    processing {
        incremental(true)
        annotations("hu.matemagyar.wge.*")
    }
    aot {
    // Please review carefully the optimizations enabled below
    // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }
}
// Node.js and NPM configuration
node {
    version = "21.4.0"
    download = true
    nodeProjectDir = file("frontend")
}

tasks {
    val buildAngularApp by creating(NpxTask::class) {
        group = "frontend"
        dependsOn(npmInstall)
        command = "ng"
        args.set(listOf("build", "--configuration", "production"))
        inputs.file("frontend/package.json")
        outputs.file("frontend/package-lock.json")
        inputs.dir(fileTree("frontend/node_modules").exclude(".cache"))
        outputs.dir("frontend/dist")
    }

    val copyFrontend by creating(Copy::class) {
        group = "frontend"
        dependsOn(buildAngularApp)
        from("frontend/dist")
        into("${layout.buildDirectory.get()}/resources/main/static")
    }
    processResources.get().dependsOn(copyFrontend)

    clean {
        delete("frontend/node_modules")
        delete("frontend/dist")
        delete("frontend/.angular")
    }
}