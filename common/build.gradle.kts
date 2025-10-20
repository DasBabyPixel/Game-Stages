plugins {
    id("loader-base")
}

val templatesSourceSet = sourceSets.register("templates")

val generateJavaTemplates = tasks.register<Sync>("generateJavaTemplates") {
    from(templatesSourceSet.map { it.java.sourceDirectories })
    val expand = loaderBase.expandProperties.get()
    inputs.properties(expand)
    filesMatching("**/BuildConstants.java") {
        expand(expand)
    }
    into(temporaryDir)
}

sourceSets {
    main {
        java.srcDir(generateJavaTemplates.map { it.outputs })
    }
}

dependencies {
    compileOnly(projects.compileHacks)
    api("org.jspecify:jspecify:1.0.0")
}
