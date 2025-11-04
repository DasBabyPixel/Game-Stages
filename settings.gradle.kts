enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "Game-Stages"

dependencyResolutionManagement {
    repositories {

//        exclusiveContent {
//            forRepository {
//                maven {
//                    name = "Parchment"
//                    url = uri("https://maven.parchmentmc.org")
//                }
//            }
//            filter {
//                includeGroup("org.parchmentmc.data")
//            }
//        }

    }
}
pluginManagement {
    includeBuild("build-extensions")
    repositories {
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.neoforged.net/releases/") }
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include("common")
//include("fabric")
include("neoforge")
include("compile-hacks")

includeVersions("common", "1.21.1")
includeVersions("neoforge", "1.21.1")

fun includeVersions(path: String, vararg versions: String) {
    versions.forEach { version ->
        include("$path-v$version")
        project(":$path-v$version").projectDir = file("$path/$version")
        project(":$path-v$version").name = "$path-${version.replace(".", "_")}"
    }
}
