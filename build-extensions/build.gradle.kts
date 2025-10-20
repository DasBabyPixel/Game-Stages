plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven("https://maven.neoforged.net/releases/")
}

dependencies {
    implementation("net.neoforged.moddev:net.neoforged.moddev.gradle.plugin:2.0.107")
//    implementation("net.neoforged.gradle.userdev:net.neoforged.gradle.userdev.gradle.plugin:7.0.192")
    implementation("org.jetbrains.gradle.plugin.idea-ext:org.jetbrains.gradle.plugin.idea-ext.gradle.plugin:1.3")
}
