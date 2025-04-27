plugins {
    id("org.jetbrains.kotlin.jvm") version ("1.9.21") apply (false)
    id("org.jetbrains.dokka")
    id("org.jlleitschuh.gradle.ktlint") version ("12.2.0")
}

allprojects {
    repositories {
        mavenCentral()
    }
    apply { plugin("org.jlleitschuh.gradle.ktlint") }
    apply { from(rootProject.file("install-git-hooks.gradle")) }
}
