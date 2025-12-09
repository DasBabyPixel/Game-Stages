plugins {
    id("loader-version-impl")
    id("net.neoforged.moddev")
    id("idea")
}

val loaderVersionBase = extensions.getByType<BaseExtension>()
val versionBaseExtension = extensions.getByType<VersionBaseExtension>()
val expandProperties = loaderVersionBase.expandProperties
val extension = extensions.create<LoaderNeoExtension>("loaderVersionNeo", project)
expandProperties.put("neo_version", provider { extension.neoVersion!! })

repositories {
    maven("https://maven.neoforged.net/releases/")
}

val gameStagesComponent = configurations.named("gameStagesComponent")
dependencies {
    gameStagesComponent(project(":neoforge"))
}

tasks.jar {
    archiveBaseName = "gamestages"
    archiveVersion = versionBaseExtension.minecraftVersionRange.firstOrNull()
        ?.let { if (version.toString().isEmpty()) it else "$it-$version" }
}

neoForge {
    runs {
        register("client") { client() }
    }
    mods {
        register("gamestages") {
            modSourceSets.add(sourceSets.main)
        }
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
