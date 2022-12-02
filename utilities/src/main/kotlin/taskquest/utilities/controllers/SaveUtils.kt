package taskquest.utilities.controllers

import com.azure.core.credential.AccessToken
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import taskquest.utilities.models.Store
import taskquest.utilities.models.User
import java.io.File

class SaveUtils {
    companion object {
        val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
        val userDataFileName = "userdata.json"
        var appDataPath = System.getProperty("user.home") + File.separator + "TaskQuestAppData"

        fun saveUserData(user: User) {
            val json = mapper.writeValueAsString(user)
            if (!File(appDataPath).exists()) {
                File(appDataPath).mkdirs()
            }
            val filePath = appDataPath + File.separator + userDataFileName
            if (!File(filePath).exists()) {
                File(filePath).createNewFile()
            }
            File(filePath).writeText(json)
        }

        fun restoreUserData(): User {
            if (!File(appDataPath).exists()) {
                File(appDataPath).mkdirs()
            }
            val filePath = appDataPath + File.separator + userDataFileName
            if (!File(filePath).exists()) {
                File(filePath).createNewFile()
                val json = mapper.writeValueAsString(User(0))
                File(filePath).writeText(json)
                return mapper.readValue<User>(json)
            } else {
                val json = File(filePath).readText()
                return mapper.readValue<User>(json)
            }
        }

        fun saveStoreData(store: Store, filename: String) {
            val json = mapper.writeValueAsString(store)
            File(filename).writeText(json)
        }

        fun restoreStoreData(filename: String): Store {
            val json = File(filename).readText()
            return mapper.readValue<Store>(json)
        }

        fun restoreStoreDataFromText(text: String): Store {
            return mapper.readValue<Store>(text)
        }

        fun cloneUserData(user: User): User {
            val json = mapper.writeValueAsString(user)
            return mapper.readValue<User>(json)
        }
    }
}
