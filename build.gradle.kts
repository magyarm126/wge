import com.github.gradle.node.npm.task.NpmTask

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

val kotlinVersion=project.properties.get("kotlinVersion")
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
}

val commonNpmTask: NpmTask.() -> Unit = {
    workingDir = file("frontend")
}

tasks {
    val flagFile = layout.buildDirectory.file("cleanNodeModulesFlag.txt")

    // Defining a task to install npm dependencies
    val npmInstallAngular by creating(NpmTask::class) {
        group = "Frontend"
        commonNpmTask()
        inputs.file("frontend/package.json")
        outputs.file("frontend/package-lock.json")
        args.set(listOf("install"))
    }

    // Angular build task
    val ngBuild by creating(NpmTask::class) {
        group = "Frontend"
        dependsOn(npmInstallAngular)
        commonNpmTask()
        args.set(listOf("run", "build","--prod"))
    }

    // Angular serve task
    val ngServe by creating(NpmTask::class) {
        group = "Frontend"
        dependsOn(npmInstallAngular)
        commonNpmTask()
        args.set(listOf("start"))
    }

    val killEsbuild by registering(Exec::class) {
        group = "Frontend"
        commandLine("cmd", "/c", "tasklist | find /i \"esbuild.exe\" && taskkill /F /IM esbuild.exe || echo Process not found, moving on")
        isIgnoreExitValue = true
    }

    val killEsbuildLinux by registering(Exec::class) {
        group = "Frontend"
        commandLine("bash", "-c", "pkill -f esbuild || true") //might not work
    }

    val cleanNodeModules by registering(Delete::class) {
        group = "Frontend"
        description = "Clean the node modules folder"
        delete("frontend/node_modules")
        doLast {
            println("Cleaned node_modules directory")
            flagFile.get().asFile.writeText("cleaned")
        }
    }

    val cleanNodeDist by registering(Delete::class) {
        group = "Frontend"
        description = "Clean the node dist folder"
        delete("frontend/dist")
        doLast {
            println("Cleaned dist directory")
        }
    }

    npmInstallAngular.mustRunAfter(cleanNodeModules)
    npmInstallAngular.doLast {
        flagFile.get().asFile.delete()
    }

    cleanNodeModules.get().dependsOn(killEsbuild)
}