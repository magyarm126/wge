plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp")
    id("io.micronaut.library")
    id("io.micronaut.aot")
    id("org.jetbrains.dokka")
}

version = "0.1"
group = "hu.matemagyar.wge"

dependencies {
    implementation(project(":middleware"))
    ksp("io.micronaut.serde:micronaut-serde-processor")
    compileOnly("io.micronaut.serde:micronaut-serde-api")
    annotationProcessor("io.micronaut.data:micronaut-data-processor")
    implementation("io.micronaut.sql:micronaut-hibernate-jpa")
    implementation("io.micronaut.data:micronaut-data-tx-hibernate")
    implementation("io.micronaut.data:micronaut-data-model")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")
    implementation("io.micronaut.liquibase:micronaut-liquibase")
    implementation("io.micronaut.data:micronaut-data-hibernate-jpa")
    runtimeOnly("org.postgresql:postgresql:42.7.2")
    runtimeOnly("ch.qos.logback:logback-classic:1.5.3")
    runtimeOnly("com.h2database:h2")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.mockito.kotlin", "mockito-kotlin", "5.4.0")
    testImplementation("io.strikt", "strikt-core", "0.35.1")
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
}

tasks {
    test {
        jvmArgs = listOf("-XX:+EnableDynamicAgentLoading", "-Xshare:off")
    }
}

micronaut {
    version("4.3.4")
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
