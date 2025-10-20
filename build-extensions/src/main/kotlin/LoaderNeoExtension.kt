import net.neoforged.moddevgradle.dsl.NeoForgeExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType

open class LoaderNeoExtension(private var project: Project) {
    var neoVersion: String? = null
        set(value) {
            if (field != null) error("NeoVersion Already initialized")
            field = value
            project.extensions.findByType<NeoForgeExtension>()!!.version = value
        }

    fun parchment(minecraftVersion: String, mappingsVersion: String) {
        project.extensions.findByType<NeoForgeExtension>()!!.run {
            parchment.minecraftVersion.set(minecraftVersion)
            parchment.mappingsVersion.set(mappingsVersion)
        }
    }
}