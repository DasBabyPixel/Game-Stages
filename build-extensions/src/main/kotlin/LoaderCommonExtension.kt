import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

open class LoaderCommonExtension(private var project: Project) {
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
}