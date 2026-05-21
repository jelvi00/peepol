import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

group = "org.peepol"
version = "1.0-SNAPSHOT"

node {
    version.set("22.22.0")
    download.set(true)
    pnpmVersion.set("10")
    nodeProjectDir.set(layout.projectDirectory)
}

tasks.register("downloadDependencies") {
    group = "node"
    description = "Download node dependencies"
    dependsOn("pnpmInstall")
}

tasks.register<PnpmTask>("build") {
    group = "build"
    description = "Build the frontend project"
    dependsOn("pnpmInstall")
    args.set(listOf("run", "build"))
}

