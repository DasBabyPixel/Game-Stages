plugins {
    id("loader-version-common")
}

loaderVersionCommon.neoformVersion = "1.21.1-20240808.144430"
loaderVersionCommon.parchment("1.21.1", "2024.11.17")

dependencies {
    api(projects.common)
}
