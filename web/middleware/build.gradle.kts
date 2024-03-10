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

    //implementation("com.google.protobuf:protoc:21.0-rc-1")
    protobuf(files("../../common/protobuf"))
    implementation("io.micronaut.grpc:micronaut-grpc-runtime")
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
}

protobuf {
    protoc {
        //artifact = "com.google.protobuf:protoc:21.0-rc-1"
        artifact = "com.google.protobuf:protoc:3.6.1"
    }

    plugins {
        // Optional: an artifact spec for a protoc plugin, with "grpc" as
        // the identifier, which can be referred to in the "plugins"
        // container of the "generateProtoTasks" closure.
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.62.2"
        }
    }

    //generatedFilesBaseDir = "$projectDir/src/generated"
    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                // Apply the "grpc" plugin whose spec is defined above, without
                // options. Note the braces cannot be omitted, otherwise the
                // plugin will not be added. This is because of the implicit way
                // NamedDomainObjectContainer binds the methods.
                id("grpc") { }
            }
        }
    }

    // Enable Kotlin generation
    //generateProtoTasks {
//        all().forEach {
//            it.builtins {
//                id("kotlin")
//            }
//        }
//    }
}