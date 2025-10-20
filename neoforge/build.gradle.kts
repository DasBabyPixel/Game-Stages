plugins {
    id("loader-base")
}

dependencies {
    api(projects.common)
    compileOnly(projects.compileHacks)
}
