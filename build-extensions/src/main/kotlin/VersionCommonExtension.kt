import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.findByType

open class VersionCommonExtension(private var project: Project) {
    var neoformVersion: String? = null
        set(value) {
            if (field != null) error("NeoformVersion Already initialized")
            field = value
            project.extensions.findByType<NeoForgeExtension>()!!.neoFormVersion = value
        }

    fun parchment(minecraftVersion: String, mappingsVersion: String) {
        project.extensions.findByType<NeoForgeExtension>()!!.run {
            parchment.minecraftVersion.set(minecraftVersion)
            parchment.mappingsVersion.set(mappingsVersion)
        }
    }

    fun dependOnCommon(common: Any) {
        val dependency = (common as ProjectDependency)
        project.dependencies.add("commonJavaIn", dependency.copy()) {
            targetConfiguration = "commonJava"
        }
        project.dependencies.add("commonResourcesIn", dependency.copy()) {
            targetConfiguration = "commonResources"
        }
    }
}