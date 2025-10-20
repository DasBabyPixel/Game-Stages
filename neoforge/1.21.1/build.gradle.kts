plugins {
    id("loader-version-neoforge")
}

loaderVersionBase.minecraftVersionRange.addAll(listOf("1.21", "1.21.1"))
loaderVersionNeo.neoVersion = "21.1.209"
loaderVersionNeo.parchment("1.21.1", "2024.11.17")

dependencies {
    gameStagesComponent(projects.common1211)

    implementation("curse.maven:kubejs-238086:5810100")
    implementation("curse.maven:rhino-416294:6886923")
    implementation("curse.maven:probejs-585406:5820894")
    implementation("curse.maven:jei-238222:7014282")
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
}
