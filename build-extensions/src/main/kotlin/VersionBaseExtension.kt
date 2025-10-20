import org.gradle.api.provider.MapProperty

open class VersionBaseExtension(private val baseExtension: BaseExtension) {
    val minecraftVersionRange: MutableList<String> = ArrayList()
    val expandProperties: MapProperty<String, Any>
        get() = baseExtension.expandProperties
}
