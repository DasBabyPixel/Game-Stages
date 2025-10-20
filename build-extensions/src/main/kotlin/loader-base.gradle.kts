plugins {
    id("java-library")
}

extensions.create<BuildConstantsExtension>("buildConstants")

java.toolchain {
    languageVersion = JavaLanguageVersion.of(21)
    vendor = JvmVendorSpec.ADOPTIUM
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

val buildConstants = extensions.getByType<BuildConstantsExtension>()
val expandProperties = objects.mapProperty<String, Any>()

val extension = extensions.create<BaseExtension>("loaderBase", expandProperties)
expandProperties.put("mod_version", provider { version.toString() })
expandProperties.put("mod_id", provider { buildConstants.modId })
expandProperties.put("mod_license", provider { buildConstants.modLicense })
expandProperties.put("mod_name", provider { buildConstants.modName })
expandProperties.put("mod_description", provider { buildConstants.modDescription })
expandProperties.put("mod_authors", provider { buildConstants.modAuthors.joinToString(",") })

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven {
                name = "CurseMaven"
                url = uri("https://cursemaven.com")
            }
        }
        filter {
            includeGroup("curse.maven")
        }
    }

    exclusiveContent {
        forRepository {
            maven {
                name = "LatvianDev"
                url = uri("https://maven.latvian.dev/releases")
            }
        }
        filter {
            includeGroup("dev.latvian.mods")
            includeGroup("dev.latvian.apps")
        }
    }
}
