plugins {
    id("loader-version-base")
    id("net.neoforged.moddev")
    id("idea")
}

repositories {
    maven("https://maven.neoforged.net/releases/")
}

project.extensions.create<LoaderCommonExtension>("loaderVersionCommon", project)

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
