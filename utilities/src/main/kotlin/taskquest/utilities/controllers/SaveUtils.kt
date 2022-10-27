package taskquest.utilities.controllers

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import taskquest.utilities.models.User
import java.io.File

class SaveUtils {
    companion object {
        val formattedJson = Json {
            encodeDefaults = true
            prettyPrint = true
        }

        fun saveData(user: User, filename: String) {
            val json = formattedJson.encodeToString(user)
            print(json)
            File(filename).writeText(json)
        }

        fun restoreData(filename: String): User {
            val json = File(filename).readText()
            print(json)
            return formattedJson.decodeFromString(json)
        }
    }
}
