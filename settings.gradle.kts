rootProject.name = "peepol"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

include("backend")

if (file("frontend").exists()) {
    include(":frontend")
}
