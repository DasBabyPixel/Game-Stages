import net.neoforged.moddevgradle.internal.RunGameTask

plugins {
    id("loader-version-neoforge")
}

loaderVersionBase.minecraftVersionRange.addAll(listOf("1.21", "1.21.1"))
loaderVersionNeo.neoVersion = "21.1.209"
loaderVersionNeo.parchment("1.21.1", "2024.11.17")

neoForge {
    interfaceInjectionData.from("interfaces.json")

    validateAccessTransformers = true

    accessTransformers {
        from("src/main/resources/META-INF/accesstransformer.cfg")
    }
}

tasks.named<RunGameTask>("runServer") {
    gameDirectory.set(file("run/server"))
}

repositories {
    maven("https://maven.blamejared.com/") {
        // location of the maven that hosts JEI files since January 2023
        name = "Jared's maven"
    }
    maven("https://modmaven.dev") {
        // location of a maven mirror for JEI files, as a fallback
        name = "ModMaven"
    }
}

dependencies {
    gameStagesComponent(projects.common1211)

    val jeiVersion = "19.27.0.340"

    implementation("curse.maven:kubejs-238086:7198768")
    implementation("curse.maven:rhino-416294:7104526")
    implementation("curse.maven:probejs-585406:7105159")
//    implementation("curse.maven:jei-238222:7181665")
    compileOnly("mezz.jei:jei-1.21.1-neoforge-api:$jeiVersion")
    runtimeOnly("mezz.jei:jei-1.21.1-neoforge:$jeiVersion")
    implementation("curse.maven:jade-324717:6853386")
    implementation("curse.maven:sodium-394468:6382651")
    implementation("curse.maven:sodium-extra-447673:5913377")
    implementation("curse.maven:reeses-sodium-options-511319:6091021")
    implementation("curse.maven:borderless-378979:6577897")
    implementation("curse.maven:ftb-quests-forge-289412:6975870")
    implementation("curse.maven:ftb-library-forge-404465:6975628")
    implementation("curse.maven:architectury-api-419699:5786327")
    implementation("curse.maven:ftb-xmod-compat-889915:6979171")
    implementation("curse.maven:ftb-teams-forge-404468:6930910")
    implementation("curse.maven:ex-deorum-901420:7160864")
}
