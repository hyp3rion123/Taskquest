package taskquest.utilities.controllers

import com.fasterxml.jackson.databind.SerializationFeature
import taskquest.utilities.models.User
import java.io.File
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

class SaveUtils {
    companion object {
        val mapper = jacksonObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

        fun saveData(user: User, filename: String) {
            val json = mapper.writeValueAsString(user)
            File(filename).writeText(json)
        }

        fun restoreData(filename: String): User {
            val json = File(filename).readText()
            return mapper.readValue<User>(json)
        }
    }
}
