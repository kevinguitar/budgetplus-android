package common

import org.gradle.api.Project
import java.util.Properties

private const val LOCAL_PROPERTIES_PATH = "local.properties"

fun Project.localProperty(name: String): String? {
    val localProperties = Properties()
    return if (rootProject.file(LOCAL_PROPERTIES_PATH).exists()) {
        localProperties.load(rootProject.file(LOCAL_PROPERTIES_PATH).inputStream())
        localProperties.getProperty(name)
    } else {
        null
    }
}
