rootProject.name = "gitissues"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(
    ":gitissues-cli:jni",
)

project(":gitissues-cli:jni").projectDir =
    file("gitissues-cli/jni")
