package taskquest.utilities.controllers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import taskquest.utilities.models.TaskList
import java.io.File

class SaveUtils {
    companion object {
        private val formattedJson = Json {
            encodeDefaults = true
            prettyPrint = true
        }

        fun MutableList<TaskList>.save(filename: String) {
            val jsonList = formattedJson.encodeToString(this.toList())
            File(filename).writeText(jsonList)
        }

        fun MutableList<TaskList>.restore(filename: String) {
            try {
                val jsonList = File(filename).readText()
                this.addAll(formattedJson.decodeFromString(jsonList))
            } catch (_: Exception) {
            }
        }
    }
}
