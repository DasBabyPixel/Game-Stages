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
    compileOnly("org.slf4j:slf4j-api:2.0.17")
    api("org.jspecify:jspecify:1.0.0")
    api("org.logicng:logicng:2.6.0")
}
