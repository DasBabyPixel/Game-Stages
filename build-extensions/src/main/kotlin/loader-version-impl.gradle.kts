plugins {
    id("loader-version-base")
}

val loaderVersionBase = extensions.getByType<BaseExtension>()
val expandProperties = loaderVersionBase.expandProperties
val gameStagesComponent = configurations.dependencyScope("gameStagesComponent")
val actualNeo = configurations.resolvable("resolvableGameStagesComponent") { extendsFrom(gameStagesComponent.get()) }
configurations.compileOnly.configure { extendsFrom(gameStagesComponent.get()) }

fun AbstractCopyTask.configureProcessResources() {
    val expand = expandProperties.get()
    inputs.properties(expand)
    filesMatching("META-INF/neoforge.mods.toml") {
        expand(expand)
    }
}

val prepareComponents = tasks.register<Sync>("prepareComponents") {
    dependsOn(actualNeo)
    from(actualNeo.map {
        it.incoming.artifacts.artifactFiles.map { f -> zipTree(f) }
    })
    into(temporaryDir)

    configureProcessResources()
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
tasks.processResources {
    configureProcessResources()
}

sourceSets.main {
    (output.classesDirs as ConfigurableFileCollection).from(prepareComponents)
}
