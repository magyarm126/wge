import com.github.gradle.node.npm.task.NpxTask

plugins {
    id("com.github.node-gradle.node") version "3.0.1"
    id("java")
}

version = "0.1"
group = "hu.matemagyar.wge"

// Node.js and NPM configuration
node {
    version = "21.4.0"
    download = true
}

tasks {
    val buildAngularApp by creating(NpxTask::class) {
        group = "frontend"
        dependsOn(npmInstall)
        command = "ng"
        args.set(listOf("build", "--configuration", "production"))
        inputs.file("package.json")
        outputs.file("package-lock.json")
        inputs.dir(fileTree("node_modules").exclude(".cache"))
        outputs.dir("dist")
        doLast{
            println("Built frontend distribution!")
        }
    }

    processResources{
        dependsOn(buildAngularApp)
    }

    clean {
        delete("node_modules")
        delete("dist")
        delete(".angular")
        doLast{
            println("Cleaned frontend caches!")
        }
    }
}