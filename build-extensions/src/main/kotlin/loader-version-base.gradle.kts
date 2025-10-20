plugins {
    id("loader-base")
}

val baseExtension = extensions.findByType<BaseExtension>()!!
val expandProperties = baseExtension.expandProperties
val extension = extensions.create<VersionBaseExtension>("loaderVersionBase", baseExtension)
val versionRange = provider { extension.minecraftVersionRange.joinToString(",", "[", "]") }

expandProperties.put("minecraft_version_range", versionRange)
