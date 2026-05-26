import com.github.gradle.node.pnpm.task.PnpmTask

plugins {
    id("peepol.node-conventions")
    id("com.github.node-gradle.node") version Versions.NODE_PLUGIN
}

node {
    version.set(Versions.NODE)
    download.set(true)
    pnpmVersion.set(Versions.PNPM)
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

