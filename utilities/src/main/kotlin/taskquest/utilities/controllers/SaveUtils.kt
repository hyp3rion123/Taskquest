package taskquest.utilities.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import taskquest.utilities.models.User
import java.io.File
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import taskquest.utilities.models.Store

class SaveUtils {
    companion object {
        val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

        fun saveUserData(user: User, filename: String) {
            val json = mapper.writeValueAsString(user)
            File(filename).writeText(json)
        }

        fun restoreUserData(filename: String): User {
            val json = File(filename).readText()
            return mapper.readValue<User>(json)
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
    }
}
